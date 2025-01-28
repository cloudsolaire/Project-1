/* Name: Jessica Hernandez
   Course: CNT 4714 - Spring 2025
   Assignment title: Project 1 - An Event-driven Enterprise Simulation
   Date: Sunday, January 26, 2025 
 */
import java.io.*;
import java.util.*;

public class csvReader {
    private final List<Item> items;

    // Constructor to read the CSV and load items
    public csvReader(String filePath) throws IOException {
        items = new ArrayList<>();
        loadCSV(filePath);
    }

    // Method to read and parse the CSV file
    private void loadCSV(String filePath) throws IOException {
        try (BufferedReader bReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bReader.readLine()) != null) {
                String[] parts = line.split(","); // Assuming CSV values are comma-separated
                if (parts.length != 5) {
                    System.err.println("Invalid line: " + line);
                    continue;
                }

                try {
                    String itemID = parts[0].trim();
                    String itemDescription = parts[1].trim();
                    boolean inStock = Boolean.parseBoolean(parts[2].trim());
                    int quantity = Integer.parseInt(parts[3].trim());
                    double price = Double.parseDouble(parts[4].trim());

                    // Add new item to the list
                    items.add(new Item(itemID, itemDescription, inStock, quantity, price));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
        }
    }

    // Method to retrieve the list of items
    public List<Item> getItems() {
        return items;
    }
}

