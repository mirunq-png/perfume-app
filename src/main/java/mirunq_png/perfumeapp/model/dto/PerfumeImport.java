package mirunq_png.perfumeapp.model.dto;

import java.util.List;

public class PerfumeImport {
    private String topNotes;
    private String heartNotes;
    private String baseNotes;
    private double rating;
    private String perfumeName;
    private String brandName;
    private String type;
    private List<String> seasons;

    public PerfumeImport() {}

    public String getTopNotes()
    {
        return topNotes;
    }

    public void setTopNotes(String topNotes) {
        this.topNotes = topNotes;
    }

    public String getHeartNotes() {
        return heartNotes;
    }

    public void setHeartNotes(String heartNotes) {
        this.heartNotes = heartNotes;
    }

    public String getBaseNotes() {
        return baseNotes;
    }

    public void setBaseNotes(String baseNotes) {
        this.baseNotes = baseNotes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPerfumeName() {
        return perfumeName;
    }

    public void setPerfumeName(String perfumeName) {
        this.perfumeName = perfumeName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<String> seasons) {
        this.seasons = seasons;
    }
}