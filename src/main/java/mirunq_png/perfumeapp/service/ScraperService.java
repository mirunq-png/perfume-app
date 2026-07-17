package mirunq_png.perfumeapp.service;

import com.microsoft.playwright.*;
import jakarta.annotation.PreDestroy;
import mirunq_png.perfumeapp.model.dto.PerfumeImport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService
{
    private final Playwright playwright;
    private final Browser browser;
    private final ExecutorService executor = Executors.newCachedThreadPool(); // revisit pool size before launch

    public ScraperService()
    {
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(List.of("--disable-blink-features=AutomationControlled", "--no-sandbox")));
    }
    @PreDestroy
    public void cleanup()
    {
        browser.close();
        playwright.close();
        executor.shutdownNow();
    }

    public PerfumeImport scrapePerfume(String url) //timeout
    {
        Future<PerfumeImport> future = executor.submit(() -> scrapeInternal(url));
        try
        {
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e)
        {
            future.cancel(true);
            throw new RuntimeException("Scraping timed out after 10 seconds for: " + url);
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to scrape Fragrantica URL: " + e.getMessage());
        }
    }

    private PerfumeImport scrapeInternal(String url) // scraping logic
    {
        try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")))
        {
            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            Page page = context.newPage();
            page.setDefaultTimeout(5000);
            page.setDefaultNavigationTimeout(6000);

            page.route("**/*", route ->
            {
                String resourceType = route.request().resourceType();
                if (resourceType.equals("image") || resourceType.equals("stylesheet")
                        || resourceType.equals("font") || resourceType.equals("media"))
                    route.abort();
                else
                    route.resume();
            });

            page.navigate(url);
            page.waitForSelector("[itemprop='description']", new Page.WaitForSelectorOptions().setTimeout(5000)); // waits
            PerfumeImport dto = new PerfumeImport();
            String rawTitle = page.title();
            rawTitle = rawTitle.replaceAll("&", "and"); // special case for bath & body works
            String cleanTitle = rawTitle.contains(" perfume") ? rawTitle.split(" perfume")[0].trim() : rawTitle;
            String description = page.locator("[itemprop='description']").first().innerText().replaceAll("&", "and");
            parseNotesFromDescription(description, dto);
            extractBrandAndName(cleanTitle, description, dto);

            if (dto.getTopNotes().isEmpty() && dto.getHeartNotes().isEmpty()) // if there are no top/heart notes, assumes body mist, otherwise EDP
                dto.setType("BM");
            else
                dto.setType("EDP");
            try
            {
                String ratingText = page.locator("[itemprop='ratingValue']").first().textContent();
                if (ratingText != null)
                {
                    double rawRating = Double.parseDouble(ratingText.trim());
                    double scaledRating = Math.round((rawRating * 2) * 10.0) / 10.0; // fragrantica rating is /5, we need it /10
                    dto.setRating(scaledRating);
                }
            } catch (Exception e)
            {
                dto.setRating(0.0);
            }
            extractSeasons(page, dto);
            return dto;
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to scrape Fragrantica URL: " + e.getMessage());
        }
    }

    // auxiliary
    private void extractBrandAndName(String cleanTitle, String description, PerfumeImport dto)
    {
        // e.g. Strawberry Pound Cake 2020 by Bath & Body Works is a Floral Fruity Gourmand fragrance for women (...)
        Pattern brandPattern = Pattern.compile("^(.*?) by (.*?) is", Pattern.CASE_INSENSITIVE);
        Matcher matcher = brandPattern.matcher(description);
        if (matcher.find())
        {
            dto.setPerfumeName(matcher.group(1).trim());
            dto.setBrandName(matcher.group(2).trim());
        } else
        {
            // fallback
            dto.setPerfumeName(cleanTitle);
            dto.setBrandName("");
        }
    }

    private void parseNotesFromDescription(String desc, PerfumeImport dto)
    {
        dto.setTopNotes("");
        dto.setHeartNotes("");
        dto.setBaseNotes("");
        String lowerDesc = desc.toLowerCase();

        if (lowerDesc.contains("top note")) // e.g. (...) Top notes are Almond and Pistachio; middle notes are Heliotrope and Jasmine; base notes are Vanilla, Salted Caramel and Sandalwood.
        {
            Pattern topPattern = Pattern.compile("top notes?\\s+(?:are|is)\\s+(.*?);", Pattern.CASE_INSENSITIVE);
            Pattern middlePattern = Pattern.compile("(?:middle|heart) notes?\\s+(?:are|is)\\s+(.*?);", Pattern.CASE_INSENSITIVE);
            Pattern basePattern = Pattern.compile("base notes?\\s+(?:are|is)\\s+([^\\.]*)", Pattern.CASE_INSENSITIVE);

            Matcher topMatcher = topPattern.matcher(desc);
            Matcher middleMatcher = middlePattern.matcher(desc);
            Matcher baseMatcher = basePattern.matcher(desc);

            if (topMatcher.find())
                dto.setTopNotes(cleanNoteString(topMatcher.group(1)));
            if (middleMatcher.find())
                dto.setHeartNotes(cleanNoteString(middleMatcher.group(1)));
            if (baseMatcher.find())
                dto.setBaseNotes(cleanNoteString(baseMatcher.group(1)));
        }
        else if (lowerDesc.contains("features")) // e.g. (...) The fragrance features Strawberry, Cupcake and Sweet Notes.
        {
            Pattern featuresPattern = Pattern.compile("features\\s+([^\\.]*)", Pattern.CASE_INSENSITIVE);
            Matcher featuresMatcher = featuresPattern.matcher(desc);
            if (featuresMatcher.find())
                dto.setBaseNotes(cleanNoteString(featuresMatcher.group(1)));
        }
    }

    private void extractSeasons(Page page, PerfumeImport dto)
    {
        List<String> selectedSeasons = new ArrayList<>();
        try
        {
            String[] seasons = {"Winter", "Spring", "Summer", "Fall"};
            int[] votes = new int[4];
            int maxVotes = 0;

            for (int i = 0; i < 4; i++)
            {
                try
                {
                    Locator voteSpan = page.locator("div[index='" + i + "'] span.tabular-nums").first();
                    if (voteSpan.count() > 0)
                    {
                        String voteText = voteSpan.textContent().trim();
                        votes[i] = Integer.parseInt(voteText);
                        if (votes[i] > maxVotes)
                            maxVotes = votes[i];
                    }
                } catch (Exception e)
                {
                    votes[i] = 0;
                }
            }

            double threshold = maxVotes * 0.50;
            for (int i = 0; i < 4; i++)
                if (votes[i] >= threshold && votes[i] >= 10) // if votes for a season >50%, add it (also minimum of 10 votes)
                    selectedSeasons.add(seasons[i]);
        } catch (Exception e)
        {
            // fallback
        }
        dto.setSeasons(selectedSeasons);
    }
    private int parseFragranticaVoteCount(String text) // in case of votes like 1.3k
    {
        if (text == null || text.isEmpty())
            return 0;
        try
        {
            if (text.contains("k"))
            {
                String numericPart = text.replace("k", "").trim();
                double value = Double.parseDouble(numericPart);
                return (int) (value * 1000);
            } else
                return Integer.parseInt(text);
        } catch (Exception e)
        {
            return 0;
        }
    }
    private String cleanNoteString(String rawNotes)
    {
        if (rawNotes == null)
            return "";
        String cleaned = rawNotes.replaceAll("\\band\\b", ",")
                .replaceAll("\\s+", " ")
                .trim();
        cleaned = cleaned.replaceAll("\\s*,\\s*", ", ");

        String[] notes = cleaned.split(", ");
        List<String> capitalized = new ArrayList<>();
        for (String note : notes)
            if (!note.isEmpty())
                capitalized.add(Character.toUpperCase(note.charAt(0)) + note.substring(1));
        return String.join(", ", capitalized);
    }
}