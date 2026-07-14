package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.model.PerfumeNote;
import mirunq_png.perfumeapp.model.PerfumeNoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfumeNoteRepository extends JpaRepository<PerfumeNote, PerfumeNoteId>
{
    void deleteByPerfume(Perfume perfume);
}
