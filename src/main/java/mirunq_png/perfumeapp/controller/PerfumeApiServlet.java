package mirunq_png.perfumeapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mirunq_png.perfumeapp.db.DatabaseConnection;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.*;
import mirunq_png.perfumeapp.service.LayeringService;
import mirunq_png.perfumeapp.service.SearchService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/perfume")
public class PerfumeApiServlet extends HttpServlet
{
    private PerfumeRepository pr;
    @Override
    public void init() throws ServletException
    {
        try
        {
            DatabaseConnection conn = DatabaseConnection.getInstance();
            pr = new PerfumeRepository(conn);
        } catch (SQLException | ClassNotFoundException e)
        {
            throw new ServletException("Failed to initialize database connection", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // for character accents
        response.setHeader("Access-Control-Allow-Origin", "*"); // for future ports
        String idParam = request.getParameter("id"); // to trigger the layering data for a perfume
        String noteParam = request.getParameter("note"); // to trigger filtering
        String fetchParam=request.getParameter("fetch"); // to trigger a perfume's data
        List<Perfume> perfumes = pr.getAllPerfumes();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(perfumes); // default, contains the data of all perfumes
        PrintWriter out = response.getWriter();
        if (fetchParam != null)
        {
            // SINGULAR PERFUME DATA
            try {
                int target = Integer.parseInt(fetchParam);
                Perfume p = pr.getPerfumeById(target);
                if (p == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Perfume not found\"}");
                } else
                    out.print(mapper.writeValueAsString(p));
            } catch (Exception e)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid id\"}");
            }
        }
        else if (idParam != null)
        {
            // LAYER LOGIC
            try {
                int targetId = Integer.parseInt(idParam);
                int limit = request.getParameter("limit") != null ? Integer.parseInt(request.getParameter("limit")) : 3;
                Perfume basePerfume = pr.getPerfumeById(targetId);
                List<Perfume> allPerfumes = pr.getAllPerfumes();
                LayeringService ls = new LayeringService();
                List<Perfume> recommendations = ls.getRecommendations(basePerfume, allPerfumes, limit);
                Map<String, Object> result = new HashMap<>();
                List<Map<String, Object>> matchesWithExplanations = new ArrayList<>();

                result.put("baseName", basePerfume.getBrand() + " " + basePerfume.getName());
                for (Perfume rec : recommendations)
                {
                    Map<String, Object> matchData = new HashMap<>();
                    matchData.put("perfume", rec);
                    matchData.put("explanation", ls.getExplanation(basePerfume, rec));
                    matchData.put("score", Math.round(ls.calculateScore(basePerfume, rec) * 100));
                    matchesWithExplanations.add(matchData);
                }
                result.put("recommendations", matchesWithExplanations);
                out.print(mapper.writeValueAsString(result));
            } catch (Exception e)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid request\"}");
            }
        }
        else if (noteParam!=null)
        {
            // FILTER LOGIC
            List<Perfume> allPerfumes = pr.getAllPerfumes();
            SearchService ss = new SearchService();
            List<Perfume> filteredResults = ss.searchByNote(allPerfumes, noteParam);
            out.print(mapper.writeValueAsString(filteredResults));
        }
        else // DEFAULT: ALL PERFUMES
            out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> perfumeData = mapper.readValue(request.getReader(), Map.class);
            String brand = ((String) perfumeData.get("brand")).toUpperCase();
            String name = ((String) perfumeData.get("name")).toUpperCase();
            int status = pr.checkAvailability(brand, name);
            if (status == 1) //already exists
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"This perfume is already in your collection.\"}");
            }
            else if (status == 0) //exists; is disabled
            {
                pr.updateActiv(brand,name,1);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\": \"Perfume already exists and is disabled- enabled successfully!\"}");
            }
            else //doesn't exist; create it
            {
                // brand (name) and name (for perfume) already extracted
                int ml=(Integer)perfumeData.get("ml");
                String typeStr=(String)perfumeData.get("type");
                Type type;
                if (typeStr!=null&&!typeStr.isBlank())
                    type=Type.valueOf(typeStr.toUpperCase());
                else type=Type.EDP;
                //float rating=(Float)perfumeData.get("rating"); // float cast can crash bc of jackson
                float rating=perfumeData.get("rating") != null ? ((Number) perfumeData.get("rating")).floatValue() : 0;
                int brandId=pr.getBrandIdByName(brand);
                if (brandId==-1) // brand doesn't already exist, add it
                    brandId=pr.addBrand(brand);
                if (brandId==-1) // pr.addBrand somehow failed
                {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Could not resolve brand.\"}");
                    return;
                }
                int id=pr.addPerfume(name,brandId,ml,type);
                if (id==-1) // pr.addPerfume somehow failed
                {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Could not insert perfume.\"}");
                    return;
                }
                if (rating>0)
                    pr.addRatingToPerfume(id,rating);
                else pr.addRatingToPerfume(id,0);
                String topNotes   = (String) perfumeData.get("topNotes");
                String heartNotes = (String) perfumeData.get("heartNotes");
                String baseNotes  = (String) perfumeData.get("baseNotes");
                processNotes(id, topNotes, NoteLayer.TOP);
                processNotes(id, heartNotes, NoteLayer.HEART);
                processNotes(id, baseNotes, NoteLayer.BASE);
                String seasons=(String)perfumeData.get("seasons");
                if (seasons!=null&&!seasons.trim().isEmpty())
                    for (String s:seasons.split(","))
                        pr.addSeasonToPerfume(id,s.trim());
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Perfume added successfully!\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
    private void processNotes(int perfumeId, String notesStr, NoteLayer layer)
    {
        if (notesStr != null && !notesStr.trim().isEmpty())
        {
            String[] notes = notesStr.split(",");
            for (String note : notes)
                pr.addNoteToPerfume(perfumeId, note.trim(), layer);
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException // edits a perfume
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            // load data
            Map<String, Object> perfumeData= mapper.readValue(request.getReader(), Map.class);
            int id=(Integer)perfumeData.get("id");
            String name=(String)perfumeData.get("name");
            String brand=(String)perfumeData.get("brand");
            int ml=(Integer)perfumeData.get("ml");
            String typeStr=(String)perfumeData.get("type");
            Type type=null;
            if (typeStr!=null&&!typeStr.isBlank())
                type=Type.valueOf(typeStr.toUpperCase());
            float rating=(float)perfumeData.get("rating");
            int brandId=pr.getBrandIdByName(brand);
            if (brandId==-1) // is the brand new?
                brandId=pr.addBrand(brand);
            if (brandId==-1) // pr.addBrand somehow failed
            {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Could not resolve brand.\"}");
                return;
            }
            // actual updates
            pr.updatePerfume(id,name,brandId,ml,type);
            pr.addRatingToPerfume(id,rating);
            // notes and seasons are wiped and reinserted
            String topNotes=(String)perfumeData.get("topNotes");
            String heartNotes=(String)perfumeData.get("heartNotes");
            String baseNotes=(String)perfumeData.get("baseNotes");
            List<Note> notes=new ArrayList<>();
            processNotes2(notes, topNotes, NoteLayer.TOP);
            processNotes2(notes, heartNotes, NoteLayer.HEART);
            processNotes2(notes, baseNotes, NoteLayer.BASE);
            pr.updateNotesForPerfume(id,notes);
            //
            String seasons=(String)perfumeData.get("seasons");
            List <Season> seasonList=new ArrayList<>();
            if (seasons!=null&&!seasons.trim().isEmpty())
                for (String s:seasons.split(","))
                    seasonList.add(Season.valueOf(s.trim().toUpperCase()));
            pr.updateSeasonsForPerfume(id,seasonList);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"message\": \"Perfume updated successfully!\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
    private void processNotes2(List <Note> notes, String notesStr, NoteLayer layer)
    { // necessary because the other method called pr.addNoteToPerfume
        if (notesStr != null && !notesStr.trim().isEmpty())
        {
            String[] splitNotes = notesStr.split(",");
                for (String n : splitNotes)
                    notes.add(new Note(n.trim(), layer));
        }
    }
}
