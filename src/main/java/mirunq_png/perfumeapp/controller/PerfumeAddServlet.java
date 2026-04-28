package mirunq_png.perfumeapp.controller;

import mirunq_png.perfumeapp.db.DatabaseConnection;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.NoteLayer;
import mirunq_png.perfumeapp.model.Type;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/add")
public class PerfumeAddServlet extends HttpServlet
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        List<String> brands = pr.getAllBrandNames();
        request.setAttribute("brands", brands);
        request.getRequestDispatcher("perfumeAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String name = request.getParameter("name");
        String existingBrand = request.getParameter("existingBrand");
        String newBrand = request.getParameter("newBrand");
        String mlStr = request.getParameter("ml");
        String typeStr = request.getParameter("type");
        String ratingStr = request.getParameter("rating");
        String[] selectedSeasons = request.getParameterValues("season");
        String topNotes = request.getParameter("topNotes");
        String heartNotes = request.getParameter("heartNotes");
        String baseNotes = request.getParameter("baseNotes");
        String finalBrand;
        if (newBrand != null && !newBrand.trim().isEmpty())
            finalBrand=newBrand;
        else finalBrand=existingBrand;
        int ml; float rating; Type type;
        if (mlStr != null && !mlStr.trim().isEmpty())
            ml=Integer.parseInt(mlStr);
        else ml=100;
        if (typeStr != null && !typeStr.trim().isEmpty())
            type=Type.valueOf(typeStr.toUpperCase());
        else type=Type.EDP;
        if (ratingStr != null && !ratingStr.trim().isEmpty())
            rating=Float.parseFloat(ratingStr);
        else
            rating=10;
        try
        {
            int brandId = pr.addBrand(finalBrand);
            if (brandId==-1)
            {
                response.getWriter().println("Error: could not add brand");
                return;
            }
            int perfumeId = pr.addPerfume(name, brandId, ml, type);
            if (perfumeId==-1)
            {
                response.getWriter().println("Error: could not add perfume");
                return;
            }
            pr.addRatingToPerfume(perfumeId, rating);
            if (selectedSeasons != null)
                for (String s : selectedSeasons)
                    pr.addSeasonToPerfume(perfumeId, s);
            processNotes(perfumeId, topNotes, NoteLayer.TOP);
            processNotes(perfumeId, heartNotes, NoteLayer.HEART);
            processNotes(perfumeId, baseNotes, NoteLayer.BASE);
            response.sendRedirect("index.html");
        } catch (SQLException e)
        {
            e.printStackTrace();
            response.getWriter().println("Database error occurred!");
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            response.getWriter().println("Invalid Type or Layer provided.");
        }
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
}