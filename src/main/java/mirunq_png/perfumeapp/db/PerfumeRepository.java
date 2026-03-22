package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Note;
import mirunq_png.perfumeapp.model.NoteLayer;
import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.model.Type;
import mirunq_png.perfumeapp.model.Season;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerfumeRepository {
    private final DatabaseConnection dbConnection;

    public PerfumeRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Perfume getPerfumeById(int id) {
        String sql = "SELECT p.parfum_id, p.nume_parfum, b.nume_brand, p.cantitate_ml, p.tip_parfum " +
                "FROM prfm_parfumuri p " +
                "JOIN prfm_branduri b ON p.brand_id = b.brand_id " +
                "WHERE p.parfum_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String typeStr = rs.getString("tip_parfum");
                    Type type = (typeStr != null) ? Type.valueOf(typeStr.toUpperCase()) : null;

                    Perfume perfume = new Perfume(
                            rs.getString("nume_brand"),
                            rs.getString("nume_parfum"),
                            rs.getInt("cantitate_ml"),
                            type
                    );

                    // helpers
                    loadNotes(perfume, id);
                    loadSeasons(perfume, id);

                    return perfume;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching perfume ID " + id);
            e.printStackTrace();
        }
        return null;
    }

    // Checklist: getAllPerfumes() method
    public List<Perfume> getAllPerfumes() {
        List<Perfume> allPerfumes = new ArrayList<>();

        // Grab the core data for every perfume in the database
        String sql = "SELECT p.parfum_id, p.nume_parfum, b.nume_brand, p.cantitate_ml, p.tip_parfum " +
                "FROM prfm_parfumuri p, prfm_branduri b " +
                "WHERE p.brand_id = b.brand_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Loop through every single row found in the database
            while (rs.next()) {
                int id = rs.getInt("parfum_id");

                String typeStr = rs.getString("tip_parfum");
                Type type = (typeStr != null) ? Type.valueOf(typeStr.toUpperCase()) : null;

                Perfume perfume = new Perfume(
                        rs.getString("nume_brand"),
                        rs.getString("nume_parfum"),
                        rs.getInt("cantitate_ml"),
                        type
                );

                loadNotes(perfume, id);
                loadSeasons(perfume, id);

                allPerfumes.add(perfume);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching the perfume catalog.");
            e.printStackTrace();
        }

        return allPerfumes;
    }

    public List<Perfume> searchByNote(String noteName) {
        List<Perfume> matchingPerfumes = new ArrayList<>();

        String sql = "SELECT DISTINCT p.parfum_id, p.nume_parfum, b.nume_brand, p.cantitate_ml, p.tip_parfum " +
                "FROM prfm_parfumuri p, prfm_branduri b, prfm_parfum_note pn, prfm_note n " +
                "WHERE p.brand_id = b.brand_id " +
                "  AND p.parfum_id = pn.parfum_id " +
                "  AND pn.nota_id = n.nota_id " +
                "  AND UPPER(n.nume_nota) LIKE UPPER(?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // The % signs act as wildcards, so searching "vanilla" finds "Madagascar Vanilla"
            pstmt.setString(1, "%" + noteName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("parfum_id");

                    String typeStr = rs.getString("tip_parfum");
                    Type type = (typeStr != null) ? Type.valueOf(typeStr.toUpperCase()) : null;

                    Perfume perfume = new Perfume(
                            rs.getString("nume_brand"),
                            rs.getString("nume_parfum"),
                            rs.getInt("cantitate_ml"),
                            type
                    );

                    loadNotes(perfume, id);
                    loadSeasons(perfume, id);

                    matchingPerfumes.add(perfume);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching for note: " + noteName);
            e.printStackTrace();
        }

        return matchingPerfumes;
    }

    public List<Perfume> getLayeringRecommendations(Perfume basePerfume, int limit) {
        List<Perfume> allPerfumes = getAllPerfumes();

        // Remove the base perfume from the candidate list
        allPerfumes.removeIf(p -> p.getName().equalsIgnoreCase(basePerfume.getName()) &&
                p.getBrand().equalsIgnoreCase(basePerfume.getBrand()));

        allPerfumes.sort((p1, p2) -> Double.compare(
                basePerfume.calculateLayeringScore(p2),
                basePerfume.calculateLayeringScore(p1)
        ));

        List<Perfume> topPicks = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, allPerfumes.size()); i++) {
            topPicks.add(allPerfumes.get(i));
        }

        return topPicks;
    }

    private void loadNotes(Perfume perfume, int perfumeId) {
        String sql = "SELECT n.nume_nota, pn.tip_nota " +
                "FROM prfm_parfum_note pn, prfm_note n " +
                "WHERE pn.nota_id = n.nota_id " +
                "  AND pn.parfum_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, perfumeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String noteName = rs.getString("nume_nota");
                    String layerStr = rs.getString("tip_nota");
                    NoteLayer layer = (layerStr != null) ? NoteLayer.valueOf(layerStr.toUpperCase()) : null;

                    perfume.addNote(new Note(noteName, layer));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading notes for perfume ID " + perfumeId);
            e.printStackTrace();
        }
    }

    private void loadSeasons(Perfume perfume, int perfumeId) {
        String sql = "SELECT s.nume_sezon " +
                "FROM prfm_parfum_sezon ps, prfm_sezoane s " +
                "WHERE ps.sezon_id = s.sezon_id " +
                "  AND ps.parfum_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, perfumeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String seasonStr = rs.getString("nume_sezon");
                    if (seasonStr != null) {
                        try {
                            perfume.addSeason(Season.valueOf(seasonStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Unknown season found in DB: " + seasonStr);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading seasons for perfume ID " + perfumeId);
            e.printStackTrace();
        }
    }
}
