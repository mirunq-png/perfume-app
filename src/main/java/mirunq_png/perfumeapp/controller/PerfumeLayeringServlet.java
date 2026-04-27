package mirunq_png.perfumeapp.controller;

import mirunq_png.perfumeapp.db.DatabaseConnection;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.service.LayeringService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/recommend")
public class PerfumeLayeringServlet extends HttpServlet
{

    private LayeringService service = new LayeringService();
    private PerfumeRepository repo;

    @Override
    public void init() throws ServletException
    {
        try
        {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            repo = new PerfumeRepository(dbConn);
        } catch (Exception e)
        {
            throw new ServletException("Failed to initialize DB: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String inputName = request.getParameter("perfumeName");
        String limitStr = request.getParameter("limit");
        try
        {
            DatabaseConnection conn = DatabaseConnection.getInstance();
            int id = repo.getPerfumeIdByName(inputName);
            if (id == -1)
            {
                response.getWriter().println("Perfume '" + inputName + "' not found.");
                return;
            }

            Perfume basePerfume = repo.getPerfumeById(id);
            if (basePerfume == null)
            {
                response.getWriter().println("Found ID " + id + " but failed to load perfume object.");
                return;
            }

            List<Perfume> allPerfumes = repo.getAllPerfumes();
            List<Perfume> results = service.getRecommendations(basePerfume, allPerfumes, Integer.parseInt(limitStr));

            request.setAttribute("base", basePerfume);
            request.setAttribute("results", results);
            request.getRequestDispatcher("perfumeLayering.jsp").forward(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServletException("doGet failed: " + e.getMessage(), e);
        }
    }
}