package mirunq_png.perfumeapp.model;

import java.util.ArrayList;
import java.util.List;

public class Perfume {
    private String brand;
    private String name;
    private int ml;
    private List<Note> notes;

    public Perfume(String brand, String name, int ml) {
        this.brand = brand;
        this.name = name;
        this.notes = new ArrayList<>();
        this.ml=ml;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }

    public void displayProfile() {
        System.out.println("Perfume: " + brand + " - " + name + " (" + ml + "ml)");
        System.out.println("Composition:");
        for (Note note : notes) {
            System.out.println("  • " + note);
        }
    }

    public String getName() { return name; }
    public String getBrand() { return brand; }
    public List<Note> getNotes() { return notes; }
}
