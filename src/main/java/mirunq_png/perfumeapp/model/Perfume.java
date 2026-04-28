package mirunq_png.perfumeapp.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Perfume
{
    private final String brand;
    private final String name;
    private final int ml;
    private List<Note> notes;
    private final Set<Season> seasons;
    private Type type;
    private float rating; // [!!!]

    public Perfume()
    {
        brand="Undefined";
        name="N/A";
        ml=0;
        notes=new ArrayList<>();
        seasons=new HashSet<>();
    }

    public Perfume(String brand, String name, int ml, Type type)
    {
        if(brand!=null&&!brand.isEmpty())
            this.brand = brand;
        else
            this.brand="Undefined";
        if (name!=null&&!name.isEmpty())
            this.name=name;
        else
            this.name="N/A";
        if (ml>0)
            this.ml = ml;
        else
            this.ml=0;
        notes = new ArrayList<>();
        seasons=new HashSet<>();
        this.type=type;
    }

    public Perfume(String brand, String name, int ml, List<Note> notes, Set<Season> seasons, Type type)
    {
        if(brand!=null&&!brand.isEmpty())
            this.brand = brand;
        else
            this.brand="Undefined";
        if (name!=null&&!name.isEmpty())
            this.name=name;
        else
            this.name="N/A";
        if (ml>0)
            this.ml = ml;
        else
            this.ml=0;

        if (notes!=null)
            this.notes=new ArrayList<>(notes);
        else
            this.notes=new ArrayList<>();
        if (seasons!=null)
            this.seasons=new HashSet<>(seasons);
        else
            this.seasons=new HashSet<>();
        this.type=type;
    }
    //add/set
    public void addNote(Note note) {notes.add(note);}
    public void addSeason(Season sz) {seasons.add(sz);}
    public void addRating(float r) {if (r>=0&&r<=10) rating=r; else rating=10;;}
    //get
    public String getName() { return name; }
    public String getBrand() { return brand; }
//    public int getMl() {return ml;}
    public List<Note> getNotes() { return notes; }
    public Set<Season> getSeasons() {return seasons;}
    public float getRating() { return rating; }
    public Type getType() { return type; }
    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        sb.append("Perfume: ").append(brand).append(" - ").append(name);
        if (type != null)
            sb.append(" (").append(type).append(")");
        sb.append(" [").append(ml).append("ml]\n");
        sb.append("Composition:\n");
        if (notes.isEmpty())
            sb.append("  • (No notes assigned)\n");
        else
            for (Note note : notes)
                sb.append("  • ").append(note).append("\n");
        sb.append("Seasons:\n");
        if (seasons.isEmpty())
            sb.append("  • (No seasons assigned)\n");
        else
            for (Season sz : seasons)
                sb.append("  • ").append(sz).append("\n");
        if (rating>0)
              sb.append("Rating: ").append(String.format("%.1f",rating)).append("/10\n");
        else
            sb.append("Rating: n/a\n");
        return sb.toString();
    }
//
//    /**
//     * Calculate overall layering score with another perfume (0.0 - 1.0)
//     * Factors:
//     * - Pyramid complementarity (20%): Base layer heavy on BASE → Top layer heavy on TOP
//     * - Shared notes (35%): Common notes between both
//     * - Type compatibility (20%): BM+EDP best, EDP+EDP mid, BM+BM worst
//     * - Season overlap (15%): Same seasons preferred
//     * - Rating (10%): User preference is taken into consideration
//     */
//    public double calculateLayeringScore(Perfume candidate)
//    {
//        double pyramidScore = calculatePyramidComplementarity(candidate);
//        double sharedNotesScore = calculateSharedNotes(candidate);
//        double typeCompatibilityScore = calculateTypeCompatibility(candidate);
//        double seasonScore = calculateSeasonOverlap(candidate);
//        double ratingScore = calculateRatingScore(candidate);
//        return (pyramidScore * 0.2) + (sharedNotesScore * 0.35) + (typeCompatibilityScore * 0.2) + (seasonScore * 0.15) + (ratingScore*0.1);
//    }
//    /**
//     * Pyramid Complementarity (0.0 - 1.0)
//     * Perfect: This BASE-heavy, Candidate TOP-heavy
//     * Good: This has BASE, Candidate has TOP
//     * Okay: This has BASE, Candidate has MIDDLE
//     * Poor: Both BASE-heavy (conflicting)
//     */
//    public double calculatePyramidComplementarity(Perfume candidate)
//    {
//        long thisTop = notes.stream().filter(n -> n.getLayer() == NoteLayer.TOP).count();
//        long thisMid = notes.stream().filter(n -> n.getLayer() == NoteLayer.HEART).count();
//        long thisBase = notes.stream().filter(n -> n.getLayer() == NoteLayer.BASE).count();
//
//        long candTop = candidate.getNotes().stream().filter(n -> n.getLayer() == NoteLayer.TOP).count();
//        long candMid = candidate.getNotes().stream().filter(n -> n.getLayer() == NoteLayer.HEART).count();
//        long candBase = candidate.getNotes().stream().filter(n -> n.getLayer() == NoteLayer.BASE).count();
//
//        // Check if they fill each other's gaps regardless of which one is the "base" perfume
//        boolean baseMeetsTop = (thisBase > 0 && candTop > 0) || (candBase > 0 && thisTop > 0);
//        boolean baseMeetsMid = (thisBase > 0 && candMid > 0) || (candBase > 0 && thisMid > 0);
//
//        // Are they clashing? (Both strictly base-heavy with no top notes between them to balance)
//        boolean bothStrictlyBase = (thisBase > thisTop && candBase > candTop) && (thisTop == 0 && candTop == 0);
//
//        if (bothStrictlyBase) {
//            return 0.25; // Clashing / too heavy
//        }
//        if (baseMeetsTop && (thisMid > 0 || candMid > 0)) {
//            return 1.0; // Perfect: Together they cover Top, Mid, and Base!
//        }
//        if (baseMeetsTop) {
//            return 0.75; // Good: Top and Base covered
//        }
//        if (baseMeetsMid) {
//            return 0.5; // Okay: Base and Mid covered
//        }
//        return 0.4; // Neutral fallback
//    }
//
//    /**
//     * Shared Notes Score (0.0 - 1.0)
//     * How many notes do these perfumes have in common?
//     */
//    public double calculateSharedNotes(Perfume candidate)
//    {
//        long sharedCount = countSharedNotes(candidate);
//        if (sharedCount >= 2) return 1.0; // Incredible similarity
//        if (sharedCount == 1) return 0.6; // Great anchor point
//        return 0.0;
//    }
//
//    /**
//     * Type Compatibility Score (0.0 - 1.0)
//     * Best: BM + EDP (1.0) - different types complement each other
//     * Mid: EDP + EDP (0.6) - same concentration
//     * Worst: BM + BM (0.2) - both light, don't layer well
//     */
//    public double calculateTypeCompatibility(Perfume candidate)
//    {
//        if (this.type == null || candidate.type == null)
//            return 0.5; // Neutral if type unknown
//        // Best: One is BM, other is EDP
//        if ((this.type.equals(Type.BM) && candidate.type.equals(Type.EDP)) || (this.type.equals(Type.EDP) && candidate.type.equals(Type.BM)))
//            return 1.0;
//        // Mid: Both EDP
//        if (this.type.equals(Type.EDP) && candidate.type.equals(Type.EDP))
//            return 0.6;
//        // Worst: Both BM
//        if (this.type.equals(Type.BM) && candidate.type.equals(Type.BM))
//            return 0.2;
//        return 0.5; // Fallback
//    }
//
//    /**
//     * Season Overlap Score (0.0 - 1.0)
//     * If both perfumes are suitable for same seasons, higher score
//     */
//    public double calculateSeasonOverlap(Perfume candidate)
//    {
//        if (this.seasons.isEmpty() || candidate.seasons.isEmpty())
//            return 0.5; // Neutral if season data missing
//        long sharedSeasons = this.seasons.stream().filter(candidate.seasons::contains).count();
//        double maxSeasons = Math.max(this.seasons.size(), candidate.seasons.size());
//        return sharedSeasons / maxSeasons;
//    }
//
//    /**
//     * Get the dominant layer of this perfume
//     */
//    public NoteLayer getDominantLayer()
//    {
//        long topCount = notes.stream().filter(n -> n.getLayer() == NoteLayer.TOP).count();
//        long midCount = notes.stream().filter(n -> n.getLayer() == NoteLayer.HEART).count();
//        long baseCount = notes.stream().filter(n -> n.getLayer() == NoteLayer.BASE).count();
//
//        if (baseCount >= topCount && baseCount >= midCount)
//            return NoteLayer.BASE;
//        if (topCount >= midCount)
//            return NoteLayer.TOP;
//        return NoteLayer.HEART;
//    }
//
//    /**
//     * Human-readable explanation of why this is a good layer
//     */
//    public String getLayeringExplanation(Perfume candidate)
//    {
//        StringBuilder explanation = new StringBuilder();
//        double pyramidScore = calculatePyramidComplementarity(candidate);
//        if (pyramidScore == 1.0)
//            explanation.append("Perfect Pyramid (Covers Top, Mid, Base). ");
//        else if (pyramidScore == 0.75)
//            explanation.append("Great Top & Base balance. ");
//
//        double typeScore = calculateTypeCompatibility(candidate);
//        if (typeScore == 1.0)
//            explanation.append("Flawless EDP + BM combo. ");
//        long shared = countSharedNotes(candidate);
//        if (shared >= 2)
//            explanation.append("Incredible bond (").append(shared).append(" shared notes). ");
//        else if (shared == 1)
//            explanation.append("Anchored by 1 shared note. ");
//
//        float avgRating = (this.rating + candidate.rating) / 2.0f;
//        if (avgRating >= 8.0f)
//            explanation.append("Both highly rated (avg ").append(String.format("%.1f", avgRating)).append("). ");
//        else if (this.rating >= 8.0f)
//            explanation.append(this.name).append(" is highly rated. ");
//        else if (candidate.rating >= 8.0f)
//            explanation.append(candidate.getName()).append(" is highly rated. ");
//
//        // If it somehow scored well but didn't trigger the above texts, give a nice default
//        if (explanation.length() == 0)
//            return "Compatible based on season and overall profile.";
//        return explanation.toString().trim();
//    }
//
//    /**
//     * Helper: Count shared notes between two perfumes
//     */
//    private long countSharedNotes(Perfume candidate)
//    {
//        return this.notes.stream().map(n -> n.getName().toUpperCase()).filter(noteName -> candidate.notes.stream().map(n -> n.getName().toUpperCase()).anyMatch(otherNote -> otherNote.equals(noteName))).count();
//    }
//
//    public double calculateRatingScore(Perfume candidate)
//    {
//        float avg = (this.rating + candidate.rating) / 2.0f;
//        return avg / 10.0;
//    }
}
