package mirunq_png.perfumeapp.model;

import jakarta.persistence.*;

@Entity
@Table(name="prfm_branduri")
public class Brand
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="brand_id")
    private int id;
    @Column(name="nume_brand")
    private String name;

    public Brand(){}
    public Brand(int id, String name)
    {
        this.id=id; this.name=name;
    }

    public Brand(String name)
    {
        this.name=name;
    }

    public int getId(){ return this.id; }
    public String getName(){ return this.name; }
    public void setId(int id){ this.id=id; }
    public void setName(String name){ this.name=name; }
}
