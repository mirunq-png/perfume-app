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

@WebServlet("/view")
public class PerfumeInfoServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String inputName = request.getParameter("perfumeName");
        DatabaseConnection conn= null;
        try {
            conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        PerfumeRepository pr=new PerfumeRepository(conn);
        int id=pr.getPerfumeIdByName(inputName);
        Perfume p=pr.getPerfumeById(id);

        request.setAttribute("perfume", p);
        request.getRequestDispatcher("perfumeInfo.jsp").forward(request, response);
    }
}
