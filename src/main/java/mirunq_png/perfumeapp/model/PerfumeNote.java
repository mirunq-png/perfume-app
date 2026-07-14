package mirunq_png.perfumeapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="prfm_parfum_note")
public class PerfumeNote
{
    @EmbeddedId
    private PerfumeNoteId id;

    @ManyToOne
    @MapsId("perfumeId")
    @JoinColumn(name="parfum_id")
    @JsonIgnore
    private Perfume perfume;

    @ManyToOne
    @MapsId("noteId")
    @JoinColumn(name="nota_id")
    private Note note;

    public PerfumeNote() {}
    public PerfumeNote(Perfume perfume, Note note, NoteLayer layer)
    {
        this.perfume = perfume;
        this.note = note;
        this.id = new PerfumeNoteId(perfume.getId(), note.getId(), layer);
    }

    public PerfumeNoteId getId() { return id; }
    public Perfume getPerfume() { return perfume; }
    public Note getNote() { return note; }
    public NoteLayer getLayer() { return id.getLayer(); }
}
