package mirunq_png.perfumeapp.controller;

import mirunq_png.perfumeapp.model.dto.PerfumeImport;
import mirunq_png.perfumeapp.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import")
public class ImportController
{
    @Autowired
    private ScraperService ss;

    @GetMapping("/fragrantica")
    public ResponseEntity<PerfumeImport> importFromUrl(@RequestParam String url)
    {
        PerfumeImport data = ss.scrapePerfume(url);
        return ResponseEntity.ok(data);
    }
}