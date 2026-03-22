package mirunq_png.perfumeapp.service;

import mirunq_png.perfumeapp.model.Perfume;
import java.util.ArrayList;
import java.util.List;

public class LayeringService
{
    public List<Perfume> getRecommendations(Perfume base, List<Perfume> all, int limit)
    {
        List<Perfume> candidates = new ArrayList<>();
        List<Perfume> topPicks = new ArrayList<>();
        for (Perfume p : all)
            if (!p.getName().equalsIgnoreCase(base.getName()) || !p.getBrand().equalsIgnoreCase(base.getBrand()))
                candidates.add(p);
        candidates.sort((p1, p2) -> Double.compare(calculateScore(base, p2), calculateScore(base, p1)));
        for (int i = 0; i < Math.min(limit, candidates.size()); i++)
            topPicks.add(candidates.get(i));
        return topPicks;
    }

    public double calculateScore(Perfume base, Perfume candidate) {
        return base.calculateLayeringScore(candidate);
    }

    public String getExplanation(Perfume base, Perfume candidate) {
        return base.getLayeringExplanation(candidate);
    }
}
