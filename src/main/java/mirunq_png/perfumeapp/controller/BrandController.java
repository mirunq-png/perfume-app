package mirunq_png.perfumeapp.controller;

import mirunq_png.perfumeapp.db.BrandRepository;
import mirunq_png.perfumeapp.model.Brand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/brands")
public class BrandController
{
    private final BrandRepository brandRepository;

    public BrandController(BrandRepository brandRepository)
    {
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public List<String> getAllBrands()
    {
        return brandRepository.findAll()
                .stream()
                .map(Brand::getName)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Brand> addBrand(@RequestBody String name)
    {
        if (brandRepository.findFirstByNameIgnoreCase(name).isPresent()) // check if the brand already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        Brand saved = brandRepository.save(new Brand(name));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
