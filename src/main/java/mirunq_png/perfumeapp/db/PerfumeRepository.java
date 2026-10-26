package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.model.Season;
import mirunq_png.perfumeapp.model.User;
import mirunq_png.perfumeapp.model.dto.BrandCount;
import mirunq_png.perfumeapp.model.dto.SeasonCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume,Integer>
{
    @Query("select p from Perfume p join p.brand b where p.activ = :activ and p.user=:user order by b.name asc, p.name asc")
    List<Perfume> findByActivAndUser(@Param("activ") int activ, @Param("user") User user); // select * from prfm_parfumuri where activ=?, sorted by brand then name
    Optional<Perfume> findByIdAndActivAndUser(int id, int activ, User user); // <=> getPerfumeById with check for activ=1
    Optional<Perfume> findByNameIgnoreCaseAndBrand_NameIgnoreCaseAndUser(String name, String brandName, User user);
    @Query("select p from Perfume p join p.notes pn where upper(pn.note.name) like upper(concat('%',:noteName,'%')) " +
            "and p.activ=1" +
            "and p.user=:user")
    List<Perfume> searchByNote(@Param("noteName") String noteName);
    @Query("select p from Perfume p join p.seasons s where s = :season " +
            "and p.activ=1 " +
            "and p.user=:user")
    List<Perfume> searchBySeason(@Param("season") Season season, @Param("user") User user);
    @Query("""
        select new mirunq_png.perfumeapp.model.dto.SeasonCount(s, count(p))
        from Perfume p
        join p.seasons s
        where p.activ = 1
        group by s
        order by count(p) desc
        """)
    List<SeasonCount> countBySeason();
    @Query("""
        select new mirunq_png.perfumeapp.model.dto.BrandCount(b.name, count(p))
        from Perfume p
        join p.brand b
        where p.activ = 1
        group by b.id, b.name
        order by count(p) desc, b.name
        """)
    List<BrandCount> countByBrand();
}
