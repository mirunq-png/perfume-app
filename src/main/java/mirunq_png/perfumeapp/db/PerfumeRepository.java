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

public class PerfumeRepository
{
    private final DatabaseConnection dbConnection;

    public PerfumeRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Perfume getPerfumeById(int id)
    {
        String sql = "SELECT p.parfum_id, p.nume_parfum, b.nume_brand, p.cantitate_ml, p.tip_parfum, p.rating " +
                "FROM prfm_parfumuri p " +
                "JOIN prfm_branduri b ON p.brand_id = b.brand_id " +
                "WHERE p.parfum_id = ? AND p.activ=1"; // avoiding a subquery

        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery())
            {
                if (rs.next())
                    return convertSqlToPerfume(rs);
            }
        } catch (SQLException e)
        {
            System.err.println("Error fetching perfume ID " + id);
            e.printStackTrace();
        }

        return null;
    }

    public int getPerfumeIdByName(String name)
    {
        String sql = "SELECT parfum_id FROM prfm_parfumuri WHERE UPPER(nume_parfum) = UPPER(?) AND activ=1";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery())
            {
                if (rs.next())
                    return rs.getInt("parfum_id");
            }
        } catch (SQLException e)
        {
            System.err.println("Error looking up ID for perfume: " + name);
            e.printStackTrace();
        }
        return -1;
    }

    public List<Perfume> getAllPerfumes()
    {
        List<Perfume> allPerfumes = new ArrayList<>();

        String sql = "SELECT p.parfum_id, p.nume_parfum, b.nume_brand, p.cantitate_ml, p.tip_parfum, p.rating " +
                "FROM prfm_parfumuri p, prfm_branduri b " +
                "WHERE p.brand_id = b.brand_id AND p.activ=1";

        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery())
        {
            while (rs.next())
                allPerfumes.add(convertSqlToPerfume(rs));
        } catch (SQLException e)
        {
            System.err.println("Error fetching the perfume catalog.");
            e.printStackTrace();
        }
        return allPerfumes;
    }

    public List<Perfume> searchByNote(String noteName)
    {
        List<Perfume> matchingPerfumes = new ArrayList<>();

        String sql = "SELECT DISTINCT p.parfum_id, p.nume_parfum, b.nume_brand, p.cantitate_ml, p.tip_parfum, p.rating " +
                "FROM prfm_parfumuri p, prfm_branduri b, prfm_parfum_note pn, prfm_note n " +
                "WHERE p.brand_id = b.brand_id " +
                "  AND p.parfum_id = pn.parfum_id " +
                "  AND pn.nota_id = n.nota_id " +
                "  AND UPPER(n.nume_nota) LIKE UPPER(?)" +
                "AND p.activ=1";

        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, "%" + noteName + "%");
            try (ResultSet rs = pstmt.executeQuery())
            {
                while (rs.next())
                    matchingPerfumes.add(convertSqlToPerfume(rs));
            }
        } catch (SQLException e)
        {
            System.err.println("Error searching for note: " + noteName);
            e.printStackTrace();
        }
        return matchingPerfumes;
    }

    private void loadNotes(Perfume perfume, int perfumeId)
    {
        String sql = "SELECT n.nume_nota, pn.tip_nota " +
                "FROM prfm_parfum_note pn, prfm_note n " +
                "WHERE pn.nota_id = n.nota_id " +
                "  AND pn.parfum_id = ?";

        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, perfumeId);
            try (ResultSet rs = pstmt.executeQuery())
            {
                while (rs.next())
                {
                    String noteName = rs.getString("nume_nota");
                    String layerStr = rs.getString("tip_nota");
                    NoteLayer layer = (layerStr != null) ? NoteLayer.valueOf(layerStr.toUpperCase()) : null;
                    perfume.addNote(new Note(noteName, layer));
                }
            }
        } catch (SQLException e)
        {
            System.err.println("Error loading notes for perfume ID " + perfumeId);
            e.printStackTrace();
        }
    }

    private void loadSeasons(Perfume perfume, int perfumeId)
    {
        String sql = "SELECT s.nume_sezon " +
                "FROM prfm_parfum_sezon ps, prfm_sezoane s " +
                "WHERE ps.sezon_id = s.sezon_id " +
                "  AND ps.parfum_id = ?";

        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, perfumeId);
            try (ResultSet rs = pstmt.executeQuery())
            {
                while (rs.next())
                {
                    String seasonStr = rs.getString("nume_sezon");
                    if (seasonStr==null)
                        continue; // skips everything down below and runs back to the top of the while
                    try
                    {
                        perfume.addSeason(Season.valueOf(seasonStr.toUpperCase()));
                    } catch (IllegalArgumentException e)
                    {
                        System.err.println("Unknown season in DB: " + seasonStr);
                    }
                }
            }
        } catch (SQLException e)
        {
            System.err.println("Error loading seasons for perfume ID " + perfumeId);
            e.printStackTrace();
        }
    }
    public boolean disablePerfume(int perfumeId)
    {
        String sql = "UPDATE prfm_parfumuri SET activ = 0 WHERE parfum_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, perfumeId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e)
        {
            System.err.println("Error disabling perfume ID " + perfumeId);
            e.printStackTrace();
        }
        return false;
    }

    public int addPerfume(String name, int brandId, int ml, Type type)
    {
        Connection conn = dbConnection.getConnection();
        int newId=getNextId("prfm_parfumuri","parfum_id");
        String insertSql = "INSERT INTO prfm_parfumuri (parfum_id, nume_parfum, brand_id, cantitate_ml, tip_parfum, activ) " + "VALUES (?, ?, ?, ?, ?, 1)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql))
        {
            pstmt.setInt(1, newId);
            pstmt.setString(2, name);
            pstmt.setInt(3, brandId);
            pstmt.setInt(4, ml);
            pstmt.setString(5, type != null ? type.name() : null);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) // if successful
                return newId;
        } catch (SQLException e)
        {
            System.err.println("Error adding new perfume: " + name);
            e.printStackTrace();
        }
        return -1;
    }

    public int getBrandIdByName(String brandName)
    {
        String sql = "SELECT brand_id FROM prfm_branduri WHERE UPPER(nume_brand) = UPPER(?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, brandName);
            try (ResultSet rs = pstmt.executeQuery())
            {
                if (rs.next())
                    return rs.getInt("brand_id");
            }
        } catch (SQLException e)
        {
            System.err.println("Error looking up brand: " + brandName);
            e.printStackTrace();
        }
        return -1;
    }
    public int addBrand(String brandName)
    {
        int existingId = getBrandIdByName(brandName);
        if (existingId != -1)
            return existingId;
        Connection conn = dbConnection.getConnection();
        int newId = 1;
        newId=getNextId("prfm_branduri","brand_id");
        if (newId==-1)
            return -1;
        String insertSql = "INSERT INTO prfm_branduri (brand_id, nume_brand) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql))
        {
            insertStmt.setInt(1, newId);
            insertStmt.setString(2, brandName);

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected<=0)
                return -1;
            System.out.println("New brand added to database: " + brandName);
            return newId;
        } catch (SQLException e)
        {
            System.err.println("Error adding brand: " + brandName);
            e.printStackTrace();
            return -1;
        }
    }
    public void addNoteToPerfume(int perfumeId, String noteName, NoteLayer layer)
    {
        try
        {
            int noteId = getNoteIdByName(noteName);

            if (noteId == -1) // doesn't exist? create it
                noteId = createNote(noteName);

            if (noteId == -1)
            {
                System.err.println("Failed to resolve note ID for: " + noteName);
                return;
            }

            String linkSql = "INSERT INTO prfm_parfum_note (parfum_id, nota_id, tip_nota) VALUES (?, ?, ?)";
            try (PreparedStatement psLink = dbConnection.getConnection().prepareStatement(linkSql))
            {
                psLink.setInt(1, perfumeId);
                psLink.setInt(2, noteId);
                if (layer!=null)
                    psLink.setString(3, layer.name());
                else psLink.setString(3,null);
                psLink.executeUpdate();
            }
        } catch (SQLException e)
        {
            System.err.println("Error linking note to perfume ID " + perfumeId);
            e.printStackTrace();
        }
    }

    public void addSeasonToPerfume(int perfumeId, String seasonName) throws SQLException
    {
        // to be added later in postgresql: INSERT ... ON CONFLICT DO NO NOTHING/UPDATE
        Connection conn = dbConnection.getConnection();
        int seasonId = -1;
        seasonId=getSeasonIdByName(seasonName);
        if (seasonId==-1) // doesn't exist? create it
            seasonId = createSeason(seasonName);
        if (seasonId == -1)
        {
            System.err.println("Failed to resolve season ID for: " + seasonName);
            return;
        }
        String sqlLink="INSERT INTO prfm_parfum_sezon (parfum_id, sezon_id) VALUES (?, ?)";
        try (PreparedStatement psLink=conn.prepareStatement(sqlLink))
        {
                psLink.setInt(1, perfumeId);
                psLink.setInt(2, seasonId);
                psLink.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void addRatingToPerfume(int perfumeId, float r)
    {
        String sql = "UPDATE prfm_parfumuri SET rating = ? WHERE parfum_id = ? AND activ = 1";
        Connection conn = dbConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setFloat(1, r);
            pstmt.setInt(2, perfumeId);
            pstmt.executeUpdate();
        } catch (SQLException e)
        {
            System.err.println("Error setting rating for perfume ID " + perfumeId);
            e.printStackTrace();
        }
    }
    private Perfume convertSqlToPerfume(ResultSet rs) throws SQLException
    {
        int id=rs.getInt("parfum_id");
        String typeSql = rs.getString("tip_parfum");
        Type type;
        if (typeSql!=null) // to avoid NullPointerException in valueOf
            type=Type.valueOf(typeSql.toUpperCase());
        else type=null;
        Perfume p=new Perfume(
                rs.getString("nume_brand"),
                rs.getString("nume_parfum"),
                rs.getInt("cantitate_ml"),
                type
        );
        float rating=rs.getFloat("rating");
        if (!rs.wasNull()) // despite rating having a default value in the db, safety for null
            p.addRating(rating);
        loadNotes(p,id);
        loadSeasons(p,id);

        return p;
    }

    private int getNextId(String tableName, String columnName)
    {
        String sql = "SELECT NVL(MAX(" + columnName + "), 0) + 1 FROM " + tableName; // table and column names don't work via '?'
        try (PreparedStatement pstmt= dbConnection.getConnection().prepareStatement(sql); ResultSet rs = pstmt.executeQuery())
        {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e)
        {
            System.err.println("Error calculating next ID for table: " + tableName);
            e.printStackTrace();
        }
        return -1;
    }

    private int getNoteIdByName(String noteName) throws SQLException
    {
        String sql="select nota_id from prfm_note where upper(nume_nota)=upper(?)";
        try(PreparedStatement pstmt =dbConnection.getConnection().prepareStatement(sql))
        {
          pstmt.setString(1,noteName);
          try (ResultSet rs = pstmt.executeQuery())
          {
              if (rs.next())
                return rs.getInt("nota_id");
              return -1;
          }
        }
    }

    private int createNote(String noteName) throws SQLException
    {
        int newId=getNextId("prfm_note","nota_id");
        if (newId==-1)
            return -1;
        String sql = "insert into prfm_note (nota_id,nume_nota) values (?,?)";
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql))
        {
            pstmt.setInt(1, newId);
            pstmt.setString(2,noteName.toUpperCase());
            pstmt.executeUpdate();
            return newId;
        }
    }

    private int getSeasonIdByName(String seasonName) throws SQLException
    {
        String sql="select sezon_id from prfm_sezoane where upper(nume_sezon)=upper(?)";
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql))
        {
            pstmt.setString(1,seasonName.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery())
            {
                if (rs.next())
                    return rs.getInt("sezon_id");
                return -1;
            }
        }
    }

    private int createSeason(String seasonName) throws SQLException
    {
        int newId=getNextId("prfm_sezoane","sezon_id");
        if (newId==-1)
            return -1;
        String sql = "insert into prfm_sezoane(sezon_id,nume_sezon) values (?,?)";
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql))
        {
            pstmt.setInt(1, newId);
            pstmt.setString(2,seasonName.toUpperCase());
            pstmt.executeUpdate();
            return newId;
        }
    }
}
