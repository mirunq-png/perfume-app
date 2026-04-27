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

@WebServlet("/catalog")
public class PerfumeCatalogServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        DatabaseConnection conn= null;
        try {
            conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        PerfumeRepository rp=new PerfumeRepository(conn);
        List<Perfume> allPerfumes=rp.getAllPerfumes();
        System.out.println("[debug] nr parfumuri: " + allPerfumes.size());
        request.setAttribute("catalog",rp.getAllPerfumes());
        request.getRequestDispatcher("perfumeCatalog.jsp").forward(request, response);
    }

    public String format(String input)
    {
        StringBuilder sb=new StringBuilder();
        String[] cuv=input.toLowerCase().split(" ");
        for (int i=0;i<cuv.length;i++)
        {
            if (cuv[i].charAt(0)>='a'&&cuv[i].charAt(0)<='z')
            {
                sb.append((char) (cuv[i].charAt(0) - 32)); //a->A
                sb.append(cuv[i].substring(1));
            }
            else sb.append(cuv[i]);
            sb.append(" ");
        }
        return sb.toString();
    }
}
