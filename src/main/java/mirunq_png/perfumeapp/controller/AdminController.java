package mirunq_png.perfumeapp.controller;

import mirunq_png.perfumeapp.service.AdminStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController
{
    private final AdminStatsService stats;

    public AdminController(AdminStatsService stats)
    {
        this.stats = stats;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats()
    {
        return Map.of(
                "notes", stats.notes(),
                "seasons", stats.seasons(),
                "brands",stats.brands()
        );
    }
}
