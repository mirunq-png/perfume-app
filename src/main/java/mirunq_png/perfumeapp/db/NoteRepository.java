package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer>
{
    Optional<Note> findFirstByNameIgnoreCase(String name);
}
