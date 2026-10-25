package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.PerfumeNote;
import mirunq_png.perfumeapp.model.PerfumeNoteId;
import mirunq_png.perfumeapp.model.dto.NoteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerfumeNoteRepository extends JpaRepository<PerfumeNote, PerfumeNoteId>
{
    @Query("""
            select new mirunq_png.perfumeapp.model.dto.NoteCount(n.name, count(distinct p.id))
            from PerfumeNote pn
            join pn.note n
            join pn.perfume p
            where p.activ = 1
            group by n.id, n.name
            order by count(distinct p.id) desc, n.name
            """)
    List<NoteCount> countByNote();
}
