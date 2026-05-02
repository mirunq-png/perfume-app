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
    private int id;
    public Perfume()
    {
        brand="Undefined";
        name="N/A";
        ml=0;
        notes=new ArrayList<>();
        seasons=new HashSet<>();
        id=0;
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
    public void addRating(float r) {if (r>=0&&r<=10) rating=r; else rating=10;}
    public void addId(int id) {this.id=id;}
    //get
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public int getMl() {return ml;}
    public List<Note> getNotes() { return notes; }
    public Set<Season> getSeasons() {return seasons;}
    public float getRating() { return rating; }
    public Type getType() { return type; }
    public int getId() { return id; }
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
}
