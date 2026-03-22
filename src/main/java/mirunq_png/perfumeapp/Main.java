package mirunq_png.perfumeapp;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import mirunq_png.perfumeapp.model.*;
import mirunq_png.perfumeapp.db.*;
import mirunq_png.perfumeapp.service.*;

public class Main
{
    public static void main(String[] args)
    {
//        Perfume toy2 = new Perfume("Moschino", "Toy 2 Bubblegum", 100, Type.EDP);
//
//        toy2.addNote(new Note("Candied Fruits", NoteLayer.TOP));
//        toy2.addNote(new Note("Bitter Orange", NoteLayer.TOP));
//        toy2.addNote(new Note("Lemon", NoteLayer.TOP));
//
//        toy2.addNote(new Note("Bubblegum", NoteLayer.HEART));
//
//        toy2.addNote(new Note("Musk", NoteLayer.BASE));
//        toy2.addSeason(Season.SUMMER);
//        toy2.addSeason(Season.SPRING);
//        System.out.println(toy2);
        System.out.println("--- Perfume Application Initializing ---");
        DatabaseConnection dbCon = null;
        try {
            // Checklist: Initialize DatabaseConnection
            dbCon = DatabaseConnection.getInstance();
            if (dbCon.getConnection() != null && !dbCon.getConnection().isClosed())
                System.out.println("Successfully connected to database!");
        } catch (SQLException e)
        {
            System.err.println("Database connection failed. Exiting...");
            e.printStackTrace();
        }

        PerfumeRepository repository = new PerfumeRepository(dbCon);
        LayeringService service = new LayeringService();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running)
        {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. View all perfumes");
            System.out.println("2. Search by note");
            System.out.println("3. Get layering recommendations");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice)
            {
                case "1":
                    System.out.println("\n--- All Perfumes ---");
                    List<Perfume> allPerfumes = repository.getAllPerfumes();
                    if (allPerfumes.isEmpty()) {
                        System.out.println("No perfumes found in the database.");
                    } else {
                        for (Perfume p : allPerfumes) {
                            System.out.println(p);
                            System.out.println("-".repeat(40));
                        }
                    }
                    break;

                case "2":
                    System.out.print("\nEnter note to search (e.g., VANILLA, MUSK): ");
                    String searchNote = scanner.nextLine();
                    List<Perfume> foundPerfumes = repository.searchByNote(searchNote);
                    if (foundPerfumes.isEmpty())
                        System.out.println("No perfumes found containing the note: " + searchNote);
                    else
                    {
                        System.out.println("\n--- Search Results ---");
                        for (Perfume p : foundPerfumes) {
                            System.out.println(p.toString().split("\n")[0]);
                            System.out.println("-".repeat(40));
                        }
                    }
                    break;

                case "3":
                    System.out.print("\nEnter the NAME of your base perfume: ");
                    String baseName = scanner.nextLine();
                    int baseId = repository.getPerfumeIdByName(baseName);
                    if (baseId == -1)
                        System.out.println("No perfume found with the name: '" + baseName + "'");
                    else
                    {
                        Perfume basePerfume = repository.getPerfumeById(baseId);
                        List<Perfume> catalog = repository.getAllPerfumes();
                        int maxx = catalog.size() - 1;

                        if (maxx <= 0)
                            System.out.println("Your database is too lonely! Add more perfumes to get layering suggestions.");
                        else
                        {
                            System.out.print("How many recommendations would you like? (Max available: " + maxx + "): ");
                            int limit = 3;

                            try {
                                limit = Integer.parseInt(scanner.nextLine());

                                if (limit > maxx)
                                {
                                    System.out.println("You only have " + maxx + " other perfumes. Setting limit to " + maxx + ".");
                                    limit = maxx;
                                }
                                else if (limit <= 0)
                                {
                                    System.out.println("Limit must be at least 1. Defaulting to 1.");
                                    limit = 1;
                                }
                            } catch (NumberFormatException e)
                            {
                                System.out.println("Invalid format. Defaulting to 3.");
                            }

                            System.out.println("\nFinding top " + limit + " matches for: " + basePerfume.getName());

                            List<Perfume> recommendations = service.getRecommendations(basePerfume, catalog, limit);

                            System.out.println("\n--- Top Layering Recommendations ---");
                            for (int i = 0; i < recommendations.size(); i++)
                            {
                                Perfume rec = recommendations.get(i);
                                System.out.println("#" + (i + 1) + ": " + rec.getName() + " by " + rec.getBrand());
                                System.out.println("Score: " + String.format("%.2f", service.calculateScore(basePerfume, rec)));
                                System.out.println("Why: " + service.getExplanation(basePerfume, rec));
                                System.out.println("-".repeat(40));
                            }
                        }
                    }
                    break;

                case "4":
                    System.out.println("Cleaning up resources...");
                    if (dbCon != null)
                        dbCon.closeConnection();
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }
}
