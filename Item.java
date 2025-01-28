/* Name: Jessica Hernandez
   Course: CNT 4714 - Spring 2025
   Assignment title: Project 1 - An Event-driven Enterprise Simulation
   Date: Sunday, January 26, 2025 
 */
public class Item {
    private String itemID;
    private String itemDescription;
    private boolean inStock;
    private int quantity;
    private double price;

    public Item(String itemID, String itemDescription, Boolean inStock, int quantity, double price) {
        this.itemID = itemID;
        this.itemDescription = itemDescription;
        this.inStock = inStock;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters
    public String getItemID() {
        return itemID;
    }
    public String getItemDescription() {
        return itemDescription;
    }
    public boolean getInStock() {
        return inStock;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getPrice() {
        return price;
    }
    
    //Setters
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public void setStock(boolean inStock){
        this.inStock = inStock;
    }

}

