package mirunq_png.perfumeapp.service;

import mirunq_png.perfumeapp.db.PerfumeNoteRepository;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.dto.BrandCount;
import mirunq_png.perfumeapp.model.dto.NoteCount;
import mirunq_png.perfumeapp.model.dto.SeasonCount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminStatsService
{
    private final PerfumeRepository perfumeRepository;
    private final PerfumeNoteRepository perfumeNoteRepository;

    public AdminStatsService(PerfumeRepository perfumeRepository,
                             PerfumeNoteRepository perfumeNoteRepository)
    {
        this.perfumeRepository = perfumeRepository;
        this.perfumeNoteRepository = perfumeNoteRepository;
    }

    public List<NoteCount> notes() { return perfumeNoteRepository.countByNote(); }
    public List<SeasonCount> seasons() { return perfumeRepository.countBySeason(); }
    public List<BrandCount> brands() { return perfumeRepository.countByBrand(); }
}
