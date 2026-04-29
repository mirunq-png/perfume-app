package mirunq_png.perfumeapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mirunq_png.perfumeapp.db.DatabaseConnection;
import mirunq_png.perfumeapp.db.PerfumeRepository;
import mirunq_png.perfumeapp.model.Perfume;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // for character accents

        response.setHeader("Access-Control-Allow-Origin", "*"); // for future ports

        List<Perfume> perfumes = pr.getAllPerfumes();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(perfumes);

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
