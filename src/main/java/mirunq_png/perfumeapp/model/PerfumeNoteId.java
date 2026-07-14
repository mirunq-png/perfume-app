package mirunq_png.perfumeapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PerfumeNoteId implements Serializable
{
    @Column(name="parfum_id")
    private int perfumeId;

    @Column(name="nota_id")
    private int noteId;

    @Enumerated(EnumType.STRING)
    @Column(name="tip_nota")
    private NoteLayer layer;

    public PerfumeNoteId() {}
    public PerfumeNoteId(int perfumeId, int noteId, NoteLayer layer)
    {
        this.perfumeId = perfumeId;
        this.noteId = noteId;
        this.layer=layer;
    }

    public int getPerfumeId() {
        return perfumeId;
    }

    public void setPerfumeId(int perfumeId) {
        this.perfumeId = perfumeId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public NoteLayer getLayer() {
        return layer;
    }

    public void setLayer(NoteLayer layer) {
        this.layer = layer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PerfumeNoteId that = (PerfumeNoteId) o;
        return getPerfumeId() == that.getPerfumeId() && getNoteId() == that.getNoteId() && getLayer() == that.getLayer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPerfumeId(), getNoteId(), getLayer());
    }
}
