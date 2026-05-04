package mirunq_png.perfumeapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mirunq_png.perfumeapp.db.DatabaseConnection;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.Perfume;
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
        String idParam = request.getParameter("id");
        String noteParam = request.getParameter("note");
        List<Perfume> perfumes = pr.getAllPerfumes();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(perfumes);
        PrintWriter out = response.getWriter();
        if (idParam != null)
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
}
