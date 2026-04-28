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
        System.out.println("--- Perfume Application Initializing ---");
        DatabaseConnection conn = null;
        try
        {
            conn = DatabaseConnection.getInstance();
            if (conn.getConnection() != null && !conn.getConnection().isClosed())
                System.out.println("Successfully connected to database!");
        } catch (SQLException e)
        {
            System.err.println("Database connection failed. Exiting...");
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        PerfumeRepository repository = new PerfumeRepository(conn);
        LayeringService service = new LayeringService();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running)
        {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. View all perfumes");
            System.out.println("2. Search by note");
            System.out.println("3. Get layering recommendations");
            System.out.println("4. Add a new perfume");
            System.out.println("5. Disable a perfume");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice)
            {
                case "1":
                    System.out.println("\n--- All Perfumes ---");
                    List<Perfume> allPerfumes = repository.getAllPerfumes();
                    if (allPerfumes.isEmpty())
                        System.out.println("No perfumes found in the database.");
                    else
                        for (Perfume p : allPerfumes)
                        {
                            System.out.println(p);
                            System.out.println("-".repeat(40));
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
                        for (Perfume p : foundPerfumes)
                        {
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
                    System.out.println("\n--- Add a New Perfume ---");
                    System.out.print("Enter perfume name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter brand name: ");
                    String brandName = scanner.nextLine();
                    System.out.print("Enter volume (ml): ");
                    int ml = 100;
                    try
                    {
                        ml = Integer.parseInt(scanner.nextLine());
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("Invalid number, defaulting to 100ml.");
                    }
                    System.out.print("Enter type (e.g., EDP, BM) or leave blank: ");
                    String typeInput = scanner.nextLine().toUpperCase();
                    Type newType = null;
                    try
                    {
                        if (!typeInput.isEmpty())
                            newType = Type.valueOf(typeInput);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.out.println("Unknown type, setting to null.");
                    }
                    int brandId = repository.addBrand(brandName);
                    if (brandId != -1)
                    {
                        int newPerfumeId = repository.addPerfume(newName, brandId, ml, newType);
                        if (newPerfumeId != -1)
                        {
                            System.out.println("Basic details saved! Now let's add the composition.");
                            while (true)
                            {
                                System.out.print("\nEnter a note ingredient (or type 'done' to finish notes): ");
                                String noteName = scanner.nextLine();
                                if (noteName.equalsIgnoreCase("done"))
                                    break;
                                System.out.print("Enter note layer (TOP, HEART, BASE): ");
                                String layerStr = scanner.nextLine().toUpperCase();
                                try
                                {
                                    NoteLayer layer = NoteLayer.valueOf(layerStr);
                                    repository.addNoteToPerfume(newPerfumeId, noteName, layer);
                                    System.out.println("  -> Added note: " + noteName + " (" + layer + ")");
                                } catch (IllegalArgumentException e)
                                {
                                    System.out.println("  -> Invalid layer. Skipping this note.");
                                }
                            }
                            while (true)
                            {
                                System.out.print("\nEnter a season (SPRING, SUMMER, AUTUMN, WINTER) or type 'done': ");
                                String seasonStr = scanner.nextLine().toUpperCase();
                                if (seasonStr.equalsIgnoreCase("done"))
                                    break;
                                try
                                {
                                    Season.valueOf(seasonStr);
                                    repository.addSeasonToPerfume(newPerfumeId, seasonStr);
                                    System.out.println("  -> Added season: " + seasonStr);
                                } catch (IllegalArgumentException e)
                                {
                                    System.out.println("  -> Invalid season. Please use SPRING, SUMMER, AUTUMN, or WINTER.");
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            System.out.print("\nEnter a rating (0.0 - 10.0) or press Enter to skip: ");
                            String ratingInput = scanner.nextLine();
                            if (!ratingInput.isBlank())
                            {
                                try
                                {
                                    float rating = Float.parseFloat(ratingInput);
                                    if (rating >= 0 && rating <= 10)
                                        repository.addRatingToPerfume(newPerfumeId, rating);
                                    else
                                        System.out.println("Rating out of range (0-10), skipping.");
                                } catch (NumberFormatException e)
                                {
                                    System.out.println("Invalid number, skipping rating.");
                                }
                            }
                            System.out.println("\nSuccessfully finished building '" + newName + "'!");
                        } else
                            System.out.println("Failed to add perfume to the database.");
                    }
                    break;
                case "5":
                    System.out.println("\n--- Disable a Perfume ---");
                    System.out.print("Enter the name of the perfume you want to disable: ");
                    String disableName = scanner.nextLine();
                    int idToDisable = repository.getPerfumeIdByName(disableName);
                    if (idToDisable == -1)
                        System.out.println("Could not find an active perfume named '" + disableName + "'.");
                    else
                    {
                        boolean success = repository.disablePerfume(idToDisable);
                        if (success)
                            System.out.println("Successfully disabled '" + disableName + "'. It will no longer appear in searches.");
                        else
                            System.out.println("Failed to disable the perfume.");
                    }
                    break;
                case "0":
                    System.out.println("Cleaning up resources...");
                    if (conn != null)
                        conn.closeConnection();
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
