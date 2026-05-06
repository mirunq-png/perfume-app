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
    public List<Perfume> searchByNote(List<Perfume>all, String searchedNote)
    {
        if (all ==null || all.isEmpty() || searchedNote == null)
            throw new RuntimeException();
        List<Perfume>results=new ArrayList<>();
        for (var p:all)
        {
            var notes=p.getNotes();
            for (var n:notes)
                if (n.getName().matches("(?i).*\\b" + searchedNote + "\\b.*")) // ?i flag makes the whole expression case-insensitive; logic: .* - wild card, allows anything; \\b - word boundary
                { // => 'vanilla' will work for both bourbon vanilla and vanilla absolute
                    results.add(p);
                    break;
                }
        }
        return results;
    }
}
