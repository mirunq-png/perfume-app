package mirunq_png.perfumeapp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Perfume
{
    private String brand;
    private String name;
    private int ml;
    private List<Note> notes;
    private Set<Season> seasons;

    public Perfume()
    {
        brand="Undefined";
        name="N/A";
        ml=0;
        notes=new ArrayList<>();
        seasons=new HashSet<>();
    }

    public Perfume(String brand, String name, int ml)
    {
        this.brand = brand;
        this.name = name;
        this.ml=ml;
        notes = new ArrayList<>();
        seasons=new HashSet<>();
    }

    public Perfume(String brand, String name, int ml, List<Note> notes, Set<Season> seasons) {
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
    }
    //add
    public void addNote(Note note) {
        notes.add(note);
    }
    public void addSeason(Season sz) {seasons.add(sz);}
    //get
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public int getMl() {return ml;}
    public List<Note> getNotes() { return notes; }
    public Set<Season> getSeasons() {return seasons;}

    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        sb.append("Perfume: ").append(brand).append(" - ").append(name).append(" (").append(ml).append("ml)\n");
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
        return sb.toString();
    }
}
