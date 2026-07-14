package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Perfume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume,Integer>
{
    List<Perfume> findByActiv(int activ); // select * from prfm_parfumuri where activ=?
    Optional<Perfume> findByIdAndActiv(int id, int activ); // <=> getPerfumeById with check for activ=1
    Optional<Perfume> findByNameIgnoreCaseAndBrand_NameIgnoreCase(String name, String brandName);
    @Query("select p from Perfume p join p.notes pn where upper(pn.note.name) like upper(concat('%',:noteName,'%')) and p.activ=1")
    List<Perfume> searchByNote(@Param("noteName") String noteName);
}
