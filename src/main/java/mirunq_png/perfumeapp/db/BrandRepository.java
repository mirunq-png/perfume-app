package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>
{
    Optional<Brand> findFirstByNameIgnoreCase(String name); // select * from prfm_branduri where upper(nume_brand)=upper(?) and grabs the first result
}