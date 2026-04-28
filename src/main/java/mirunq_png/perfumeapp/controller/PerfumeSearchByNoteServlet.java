package mirunq_png.perfumeapp.controller;

import mirunq_png.perfumeapp.db.DatabaseConnection;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.Perfume;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/search")
public class PerfumeSearchByNoteServlet extends HttpServlet
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
        String searchNote = request.getParameter("note");
        if (searchNote != null && !searchNote.trim().isEmpty())
        {
            List<Perfume> foundPerfumes = pr.searchByNote(searchNote.trim());
            request.setAttribute("perfumes", foundPerfumes);
            request.setAttribute("searchNote", searchNote);
        }
        request.getRequestDispatcher("perfumeSearchByNote.jsp").forward(request, response);
    }
}