package mirunq_png.perfumeapp.model;

import jakarta.persistence.*;

@Entity
@Table(name="prfm_note")
public class Note
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) // for sequential gen of ids
    @Column(name="nota_id")
    private int id;
    @Column(name="nume_nota",nullable=false)
    private String name;

    public Note(){}
    public Note(String name)
    {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
