package mirunq_png.perfumeapp;

import java.sql.Connection;
import java.sql.SQLException;

import mirunq_png.perfumeapp.model.Note;
import mirunq_png.perfumeapp.model.NoteLayer;
import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.db.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        Perfume toy2 = new Perfume("Moschino", "Toy 2 Bubblegum", 100);

        toy2.addNote(new Note("Candied Fruits", NoteLayer.TOP));
        toy2.addNote(new Note("Bitter Orange", NoteLayer.TOP));
        toy2.addNote(new Note("Lemon", NoteLayer.TOP));

        toy2.addNote(new Note("Bubblegum", NoteLayer.HEART));

        toy2.addNote(new Note("Musk", NoteLayer.BASE));

        System.out.println("--- Perfume Application Initialized ---");
        toy2.displayProfile();

        try
        {
            Connection con=DatabaseConnection.getInstance().getConnection();
            if (con!=null&&!con.isClosed()) {
                System.out.println("Succesfully connected to database!");
            }
        } catch (SQLException e)
        {
            System.err.println("Database error "+ e.getMessage());
            e.printStackTrace();
        }

    }
}
