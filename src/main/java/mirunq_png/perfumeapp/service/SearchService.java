package mirunq_png.perfumeapp.service;

import mirunq_png.perfumeapp.model.Perfume;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchService
{
    public List<Perfume> searchByNote(List<Perfume>all, String noteName)
    {
        if (all ==null || all.isEmpty() || noteName == null)
            throw new RuntimeException();
        List<Perfume>results=new ArrayList<>();
        for (var p:all)
        {
            var notes=p.getNotes();
            for (var n:notes)
                if (n.getName().toLowerCase().contains(noteName.toLowerCase()))
                {
                    results.add(p);
                    break;
                }
        }
        return results;
    }
}
