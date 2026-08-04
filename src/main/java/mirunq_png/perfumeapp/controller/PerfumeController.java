package mirunq_png.perfumeapp.controller;

import jakarta.transaction.Transactional;
import mirunq_png.perfumeapp.db.BrandRepository;
import mirunq_png.perfumeapp.db.NoteRepository;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.*;
import mirunq_png.perfumeapp.service.LayeringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/perfume")
public class PerfumeController
{
    private final PerfumeRepository perfumeRepository;
    private final BrandRepository brandRepository;
    private final NoteRepository noteRepository;

    public PerfumeController(PerfumeRepository perfumeRepository, BrandRepository brandRepository, NoteRepository noteRepository)
    {
        this.perfumeRepository = perfumeRepository;
        this.brandRepository = brandRepository;
        this.noteRepository = noteRepository;
    }

    // get = reads/retrieves data
    @GetMapping
    public ResponseEntity<?> get(
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer fetch,
            @RequestParam(required=false) String note,
            @RequestParam(required=false) String season,
            @RequestParam(required=false, defaultValue="3") int limit)
    {
        if (fetch != null) // EDITING, preloads fields
        {
            return perfumeRepository.findByIdAndActiv(fetch, 1)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        else if (id != null) // LAYERING
        {
            Perfume base = perfumeRepository.findByIdAndActiv(id, 1).orElse(null);
            if (base == null)
                return ResponseEntity.notFound().build();
            List<Perfume> all = perfumeRepository.findByActiv(1);
            LayeringService ls = new LayeringService();
            List<Perfume> recommendations = ls.getRecommendations(base, all, limit);
            Map<String, Object> result = new HashMap<>();
            result.put("baseName", base.getBrand().getName() + " " + base.getName());
            List<Map<String, Object>> matches = new ArrayList<>();
            for (Perfume rec : recommendations)
            {
                Map<String, Object> matchData = new HashMap<>();
                matchData.put("perfume", rec);
                matchData.put("explanation", ls.getExplanation(base, rec));
                matchData.put("score", Math.round(ls.calculateScore(base, rec) * 100));
                matches.add(matchData);
            }
            result.put("recommendations", matches);
            return ResponseEntity.ok(result);
        }
        else if (note != null) // NOTE SEARCHING
        {
            return ResponseEntity.ok(perfumeRepository.searchByNote(note));
        }
        else if (season != null) // SEASON SEARCHING
        {
            try {
                Season s = Season.valueOf(season.trim().toUpperCase());
                return ResponseEntity.ok(perfumeRepository.searchBySeason(s));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid season."));
            }
        }
        else // LOADS ALL PERFUMES
        {
            return ResponseEntity.ok(perfumeRepository.findByActiv(1));
        }
    }

    // post = new resource
    @PostMapping
    @Transactional
    public ResponseEntity<?> addPerfume(@RequestBody Map<String, Object> body)
    {
        String name  = (String) body.get("name");
        String brandName = (String) body.get("brand");
        Optional<Perfume> existing = perfumeRepository
                .findByNameIgnoreCaseAndBrand_NameIgnoreCase(name, brandName);
        if (existing.isPresent()) // already exists?
        {
            Perfume p = existing.get();
            if (p.getActiv() == 1)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "This perfume is already in your collection."));
            else // perfume was disabled, now reenables it
            {
                p.setActiv(1);
                perfumeRepository.save(p);
                return ResponseEntity.ok(Map.of("message", "Perfume re-enabled successfully!"));
            }
        }

        // new perfume
        Perfume p = buildPerfume(body);
        perfumeRepository.save(p);
        attachNotes(p, body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Perfume added successfully!"));
    }

    // put = modifying; idempotent
    @PutMapping
    @Transactional
    public ResponseEntity<?> updatePerfume(@RequestBody Map<String, Object> body)
    {
        int id = (Integer) body.get("id");
        Perfume p = perfumeRepository.findByIdAndActiv(id, 1).orElse(null);
        if (p == null)
            return ResponseEntity.notFound().build();
        String brandName = (String) body.get("brand");
        Brand brand = brandRepository.findFirstByNameIgnoreCase(brandName)
                .orElseGet(() -> brandRepository.save(new Brand(brandName)));
        p.setName((String) body.get("name"));
        p.setBrand(brand);
        p.setMl((Integer) body.get("ml"));
        String typeStr = (String) body.get("type");
        p.setType(typeStr != null && !typeStr.isBlank() ? Type.valueOf(typeStr.toUpperCase()) : null);
        float rating = body.get("rating") != null ? ((Number) body.get("rating")).floatValue() : 0;
        p.setRating(rating);

        p.getNotes().clear(); // wipes notes
        perfumeRepository.saveAndFlush(p);
        attachNotes(p, body);

        p.getSeasons().clear(); // wipes szns
        String seasonsRaw = (String) body.get("seasons");
        if (seasonsRaw != null && !seasonsRaw.trim().isEmpty())
            for (String s : seasonsRaw.split(","))
                p.getSeasons().add(Season.valueOf(s.trim().toUpperCase()));

        perfumeRepository.save(p);
        return ResponseEntity.ok(Map.of("message", "Perfume updated successfully!"));
    }
    // delete
    @DeleteMapping
    public ResponseEntity<?> deletePerfume(@RequestParam int id)
    {
        return perfumeRepository.findByIdAndActiv(id, 1)
                .map(p -> {
                    p.setActiv(0);
                    perfumeRepository.save(p);
                    return ResponseEntity.ok(Map.of("message", "Perfume deleted successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // auxiliary functions
    private Brand resolveBrand(String brandName)
    {
        return brandRepository.findFirstByNameIgnoreCase(brandName)
                .orElseGet(() -> brandRepository.save(new Brand(brandName)));
    }

    private Perfume buildPerfume(Map<String, Object> body)
    {
        String name=(String)body.get("name");
        String brandName=(String)body.get("brand");
        int ml=(Integer)body.get("ml");
        String typeStr=(String)body.get("type");
        Type type=typeStr != null && !typeStr.isBlank() ? Type.valueOf(typeStr.toUpperCase()) : null;
        float rating=body.get("rating") != null ? ((Number)body.get("rating")).floatValue() : 0;
        String seasonsRaw=(String)body.get("seasons");
        Brand brand = resolveBrand(brandName);
        Perfume p = new Perfume(name, brand, ml, type);
        p.setRating(rating);

        if (seasonsRaw != null && !seasonsRaw.trim().isEmpty())
            for (String s : seasonsRaw.split(","))
                p.getSeasons().add(Season.valueOf(s.trim().toUpperCase()));
        return p;
    }

    private void attachNotes(Perfume p, Map<String, Object> body)
    {
        Set<String> seenNotes = new HashSet<>(); // so it doesn't crash for duplicates
        processNotes(p, (String) body.get("topNotes"),   NoteLayer.TOP,seenNotes);
        processNotes(p, (String) body.get("heartNotes"), NoteLayer.HEART, seenNotes);
        processNotes(p, (String) body.get("baseNotes"),  NoteLayer.BASE, seenNotes);
    }

    private void processNotes(Perfume p, String notesStr, NoteLayer layer, Set<String> seenNotes)
    {
        if (notesStr == null || notesStr.trim().isEmpty()) return;
        for (String n : notesStr.split(","))
        {
            String noteName = n.trim();
            if (noteName.isEmpty()) continue;
            Note note = noteRepository.findFirstByNameIgnoreCase(noteName)
                    .orElseGet(() -> noteRepository.save(new Note(noteName)));

            String aux = note.getId() + "-" + layer.name(); // allows for "vanilla" in different layers but not on the same layer
            if (!seenNotes.contains(aux))
            {
                p.getNotes().add(new PerfumeNote(p, note, layer));
                seenNotes.add(aux);
            }
        }
    }
}
