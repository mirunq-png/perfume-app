package mirunq_png.perfumeapp.model;

public class Note
{
    private final String name;
    private final NoteLayer layer;

    public Note(String name, NoteLayer layer)
    {
        this.name = name;
        this.layer = layer;
    }

    public String getName() { return name; }
    public NoteLayer getLayer() { return layer; }

    @Override
    public String toString()
    {
        return name + " (" + layer + ")";
    }
}
