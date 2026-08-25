package mirunq_png.perfumeapp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="prfm_parfumuri")
public class Perfume
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="parfum_id")
    private int id;

    @Column(name="nume_parfum", nullable=false)
    private String name;

    @ManyToOne
    @JoinColumn(name="brand_id", nullable=false)
    private Brand brand;

    @Column(name="cantitate_ml")
    private int ml;

    @Column(name="tip_parfum")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name="rating")
    private float rating;

    @Column(name="activ")
    private int activ = 1;

    @OneToMany(mappedBy="perfume", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<PerfumeNote> notes = new ArrayList<>();

    @ElementCollection(targetClass=Season.class)
    @CollectionTable(
            name="prfm_parfum_sezon",
            joinColumns=@JoinColumn(name="parfum_id"))
    @Column(name="season")
    @Enumerated(EnumType.STRING)
    private Set<Season> seasons = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Perfume() {}
    public Perfume(String name, Brand brand, int ml, Type type)
    {
        this.name = name;
        this.brand = brand;
        this.ml = ml;
        this.type = type;
    }

    public Brand getBrand() {
        return brand;
    }
    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getMl() {
        return ml;
    }
    public void setMl(int ml) {
        this.ml = ml;
    }
    public List<PerfumeNote> getNotes() {
        return notes;
    }
    public void setNotes(List<PerfumeNote> notes) {
        this.notes = notes;
    }
    public Set<Season> getSeasons() {
        return seasons;
    }
    public void setSeasons(Set<Season> seasons) {
        this.seasons = seasons;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getActiv() {
        return activ;
    }
    public void setActiv(int activ) {
        this.activ = activ;
    }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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
            for (PerfumeNote note : notes)
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
