package mirunq_png.perfumeapp.service;

import mirunq_png.perfumeapp.model.NoteLayer;
import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.model.Type;

import java.util.ArrayList;
import java.util.List;

public class LayeringService
{
    public List<Perfume> getRecommendations(Perfume base, List<Perfume> all, int limit)
    {
        List<Perfume> candidates = new ArrayList<>();
        List<Perfume> topPicks = new ArrayList<>();
        for (Perfume p : all)
            if (!p.getName().equalsIgnoreCase(base.getName()) || !p.getBrand().equalsIgnoreCase(base.getBrand()))
                candidates.add(p);
        candidates.sort((p1, p2) -> Double.compare(calculateScore(base, p2), calculateScore(base, p1)));
        for (int i = 0; i < Math.min(limit, candidates.size()); i++)
            topPicks.add(candidates.get(i));
        return topPicks;
    }
    /**
     * Calculate overall layering score with another perfume (0.0 - 1.0)
     * Factors:
     * - Pyramid complementarity (20%): Base layer heavy on BASE → Top layer heavy on TOP
     * - Shared notes (35%): Common notes between both
     * - Type compatibility (20%): BM+EDP best, EDP+EDP mid, BM+BM worst
     * - Season overlap (15%): Same seasons preferred
     * - Rating (10%): User preference is taken into consideration
     */
    public double calculateScore(Perfume p1, Perfume p2)
    {
        double pyramidScore = calculatePyramidComplementarity(p1,p2);
        double sharedNotesScore = calculateSharedNotes(p1,p2);
        double typeCompatibilityScore = calculateTypeCompatibility(p1,p2);
        double seasonScore = calculateSeasonOverlap(p1,p2);
        double ratingScore = calculateRatingScore(p1,p2);
        return (pyramidScore * 0.2) + (sharedNotesScore * 0.35) + (typeCompatibilityScore * 0.2) + (seasonScore * 0.15) + (ratingScore*0.1);
    }
    /**
     * Pyramid Complementarity (0.0 - 1.0)
     * Perfect: This BASE-heavy, Candidate TOP-heavy
     * Good: This has BASE, Candidate has TOP
     * Okay: This has BASE, Candidate has MIDDLE
     * Poor: Both BASE-heavy (conflicting)
     */
    public double calculatePyramidComplementarity(Perfume p1, Perfume p2)
    {
        long thisTop = countByLayer(p1, NoteLayer.TOP);
        long thisMid = countByLayer(p1, NoteLayer.HEART);
        long thisBase = countByLayer(p1, NoteLayer.BASE);

        long candTop = countByLayer(p2, NoteLayer.TOP);
        long candMid = countByLayer(p2, NoteLayer.HEART);
        long candBase = countByLayer(p2, NoteLayer.BASE);

        // Check if they fill each other's gaps regardless of which one is the "base" perfume
        boolean baseMeetsTop = (thisBase > 0 && candTop > 0) || (candBase > 0 && thisTop > 0);
        boolean baseMeetsMid = (thisBase > 0 && candMid > 0) || (candBase > 0 && thisMid > 0);

        // Are they clashing? (Both strictly base-heavy with no top notes between them to balance)
        boolean bothStrictlyBase = (thisBase > thisTop && candBase > candTop) && (thisTop == 0 && candTop == 0);

        if (bothStrictlyBase) {
            return 0.25; // Clashing / too heavy
        }
        if (baseMeetsTop && (thisMid > 0 || candMid > 0)) {
            return 1.0; // Perfect: Together they cover Top, Mid, and Base!
        }
        if (baseMeetsTop) {
            return 0.75; // Good: Top and Base covered
        }
        if (baseMeetsMid) {
            return 0.5; // Okay: Base and Mid covered
        }
        return 0.4; // Neutral fallback
    }
    /**
     * Shared Notes Score (0.0 - 1.0)
     * How many notes do these perfumes have in common?
     */
    public double calculateSharedNotes(Perfume p1, Perfume p2)
    {
        long sharedCount = countSharedNotes(p1, p2);
        if (sharedCount >= 2) return 1.0; // Incredible similarity
        if (sharedCount == 1) return 0.6; // Great anchor point
        return 0.0;
    }
    /**
     * Type Compatibility Score (0.0 - 1.0)
     * Best: BM + EDP (1.0) - different types complement each other
     * Mid: EDP + EDP (0.6) - same concentration
     * Worst: BM + BM (0.2) - both light, don't layer well
     */
    public double calculateTypeCompatibility(Perfume p1, Perfume p2)
    {
        if (p1.getType() == null || p2.getType() == null)
            return 0.5; // Neutral if type unknown
        // Best: One is BM, other is EDP
        if ((p1.getType().equals(Type.BM) && p2.getType().equals(Type.EDP)) || (p1.getType().equals(Type.EDP) && p2.getType().equals(Type.BM)))
            return 1.0;
        // Mid: Both EDP
        if (p1.getType().equals(Type.EDP) && p2.getType().equals(Type.EDP))
            return 0.6;
        // Worst: Both BM
        if (p1.getType().equals(Type.BM) && p2.getType().equals(Type.BM))
            return 0.2;
        return 0.5; // Fallback
    }
    /**
     * Season Overlap Score (0.0 - 1.0)
     * If both perfumes are suitable for same seasons, higher score
     */
    public double calculateSeasonOverlap(Perfume p1, Perfume p2)
    {
        if (p1.getSeasons().isEmpty() || p2.getSeasons().isEmpty())
            return 0.5; // Neutral if season data missing
        long sharedSeasons = p1.getSeasons().stream().filter(p2.getSeasons()::contains).count();
        double maxSeasons = Math.max(p1.getSeasons().size(), p2.getSeasons().size());
        return sharedSeasons / maxSeasons;
    }
    /**
     * Get the dominant layer of this perfume
     */
    public NoteLayer getDominantLayer(Perfume p)
    {
        long topCount = p.getNotes().stream().filter(n -> n.getLayer() == NoteLayer.TOP).count();
        long midCount = p.getNotes().stream().filter(n -> n.getLayer() == NoteLayer.HEART).count();
        long baseCount = p.getNotes().stream().filter(n -> n.getLayer() == NoteLayer.BASE).count();

        if (baseCount >= topCount && baseCount >= midCount)
            return NoteLayer.BASE;
        if (topCount >= midCount)
            return NoteLayer.TOP;
        return NoteLayer.HEART;
    }
    /**
     * Human-readable explanation of why this is a good layer
     */
    public String getExplanation(Perfume p1, Perfume p2)
    {
        StringBuilder explanation = new StringBuilder();
        double pyramidScore = calculatePyramidComplementarity(p1,p2);
        if (pyramidScore == 1.0)
            explanation.append("Perfect Pyramid (Covers Top, Mid, Base). ");
        else if (pyramidScore == 0.75)
            explanation.append("Great Top & Base balance. ");

        double typeScore = calculateTypeCompatibility(p1,p2);
        if (typeScore == 1.0)
            explanation.append("Flawless EDP + BM combo. ");
        long shared = countSharedNotes(p1,p2);
        if (shared >= 2)
            explanation.append("Incredible bond (").append(shared).append(" shared notes). ");
        else if (shared == 1)
            explanation.append("Anchored by 1 shared note. ");

        float avgRating = (p1.getRating() + p2.getRating()) / 2.0f;
        if (avgRating >= 8.0f)
            explanation.append("Both highly rated (avg ").append(String.format("%.1f", avgRating)).append("). ");
        else if (p1.getRating() >= 8.0f)
            explanation.append(p1.getName()).append(" is highly rated. ");
        else if (p2.getRating() >= 8.0f)
            explanation.append(p2.getName()).append(" is highly rated. ");

        // If it somehow scored well but didn't trigger the above texts, give a nice default
        if (explanation.length() == 0)
            return "Compatible based on season and overall profile.";
        return explanation.toString().trim();
    }
    /**
     * Helper: Count shared notes between two perfumes
     */
    private long countSharedNotes(Perfume p1, Perfume p2)
    {
        return p1.getNotes().stream()
                .map(n -> n.getName().toUpperCase())
                .filter(name -> p2.getNotes().stream().anyMatch(n2 -> n2.getName().equalsIgnoreCase(name)))
                .count();
    }
    private long countByLayer(Perfume p, NoteLayer layer)
    {
        return p.getNotes().stream().filter(n -> n.getLayer() == layer).count();
    }
    public double calculateRatingScore(Perfume p1, Perfume p2)
    {
        float avg = (p1.getRating() + p2.getRating()) / 2.0f;
        return avg / 10.0;
    }
}
