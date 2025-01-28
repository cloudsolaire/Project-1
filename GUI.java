/* Name: Jessica Hernandez
   Course: CNT 4714 - Spring 2025
   Assignment title: Project 1 - An Event-driven Enterprise Simulation
   Date: Sunday, January 26, 2025 
 */
import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class GUI implements ActionListener{
    //CSV Reader
    private List<Item> itemList;
    private csvReader reader;

    //GUI shopping cart variables
    private int itemCount = 1;
    private int cartCount = 0;
    private double subTotal = 0.00;
    private boolean cartStatus = false;
    private boolean isCheckedOut = false;
    private Item foundItem;
    private int userQty; //Input user quantity
    private int discPercent; //Discount percentage based on items
    private double discountPrice; //Discount after percentage is calculated
    private double currentPrice; //Temp variable to add/sub to subtotal based on ADD or REMOVE
    DecimalFormat decFormat = new DecimalFormat("0.00");

    //GUI window
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 650;
    private JFrame window = new JFrame("Nile.com - Spring 2025");

    //GUI invoice window
    private JFrame invoiceWindow = new JFrame("Nile.com - FINAL INVOICE");
    private JPanel invoicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JTextArea invoiceLabel = new JTextArea();

    //GUI sections 
    private JPanel top = new JPanel();
    private JPanel middle = new JPanel();
    private JPanel bottom = new JPanel();

    //GUI top panel fields
    private static final int FIELD_WIDTH = 40;
    private JLabel itemIDTag = new JLabel("Enter item ID for item #" + itemCount + ":"); 
    private JTextField entryIDField = new JTextField(FIELD_WIDTH);
    private JLabel itemQtyTag = new JLabel("Enter quantity for item #" + itemCount + ":");
    private JTextField entryQtyField = new JTextField(FIELD_WIDTH);
    private JLabel itemDetailsTag = new JLabel("Details for Item #" + itemCount + ":"); 
    private JTextField itemInfoStr = new JTextField(FIELD_WIDTH);
    private JLabel subTotalTag = new JLabel("Current Subtotal for " + cartCount + " item(s):");
    private JTextField subTotalStr = new JTextField(FIELD_WIDTH); 

    //GUI top subpanels
    private JPanel itemIDPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JPanel itemQtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JPanel itemDetailsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JPanel subTotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    //GUI middle panel
    private JLabel cartStatLabel = new JLabel("Your Shopping Cart " + cartStatus);
    private DefaultTableModel cartModel = new DefaultTableModel(10, 1){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    private JTable cartTable = new JTable(cartModel);
    private JScrollPane scrollPane = new JScrollPane(cartTable);
    private boolean isItemAdded = false;

    //GUI bottom user controls button variables
    private JButton searchButton = new JButton("Search For Item #" + itemCount); 
    private JButton addButton = new JButton("Add Item #" + itemCount + " To Cart");
    private JButton removeButton = new JButton("Delete Last Item Added To Cart");
    private JButton checkOutButton = new JButton("Check Out");
    private JButton emptyButton = new JButton("Empty Cart - Start A New Order");
    private JButton exitButton = new JButton("Exit (Close App)");

    public GUI(){
        //CSV Reader
        try {
            // Load items from the CSV file
            reader = new csvReader("inventory.csv");
            itemList = reader.getItems();
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        //Create a window for GUI
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setResizable(false);
        window.setLocationRelativeTo(null); //Center window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        //Create an invoice window for GUI
        invoiceWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        invoiceWindow.setResizable(false);
        invoiceWindow.setLocationRelativeTo(null);
        invoiceWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        invoiceWindow.setLayout(new BorderLayout());

        //Invoice string Label
        invoiceLabel.setEditable(false);
        invoiceLabel.setOpaque(false);
        invoiceLabel.setLineWrap(false);
        invoiceLabel.setWrapStyleWord(true);
        
        //Invoice panel
        invoicePanel.add(new JScrollPane(invoiceLabel));

        //Top panel
        top.setBackground(Color.BLACK);
        top.setPreferredSize(new Dimension(window.getWidth(), 200));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        
        //Top panel selections
        itemIDPanel.add(itemIDTag);
        itemIDTag.setForeground(Color.YELLOW);
        itemIDPanel.add(entryIDField);
        itemIDPanel.setBackground(Color.BLACK);
        top.add(itemIDPanel);
        itemQtyPanel.add(itemQtyTag);
        itemQtyTag.setForeground(Color.YELLOW);
        itemQtyPanel.add(entryQtyField);
        itemQtyPanel.setBackground(Color.BLACK);
        top.add(itemQtyPanel);
        itemDetailsPanel.add(itemDetailsTag);
        itemDetailsTag.setForeground(Color.RED);
        itemDetailsPanel.add(itemInfoStr);
        itemInfoStr.setEditable(false);
        itemDetailsPanel.setBackground(Color.BLACK);
        top.add(itemDetailsPanel);
        subTotalPanel.add(subTotalTag);
        subTotalTag.setForeground(Color.BLUE);
        subTotalPanel.add(subTotalStr);
        subTotalStr.setEditable(false);
        subTotalPanel.setBackground(Color.BLACK);
        top.add(subTotalPanel);

        //Middle panel
        middle.setBackground(Color.GRAY);
        middle.setPreferredSize(new Dimension(window.getWidth(), 200));
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        cartStatLabel.setForeground(Color.RED);
        cartStatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        middle.add(cartStatLabel);
        findCartStatus(cartStatus, itemCount);
        //Middle panel shopping cart sections
        cartModel.setColumnIdentifiers(new String[]{""});
        for (int i = 0; i < 9; i++){
            cartModel.setValueAt(null, i, 0);
        }
        cartTable.setRowSelectionAllowed(false);
        cartTable.setColumnSelectionAllowed(false);
        cartTable.setCellSelectionEnabled(false);
        cartTable.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                        boolean hasFocus, int row, int column){
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0){
                    cell.setBackground(Color.WHITE);
                }
                else {
                    cell.setBackground(Color.LIGHT_GRAY);
                }
                return cell;
            }
        });
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        middle.add(scrollPane);

        //Bottom panel
        bottom.setBackground(Color.BLUE);
        bottom.setPreferredSize(new Dimension(window.getWidth(), 200));
        //Bottom panel selections
        searchButton.addActionListener(this);
        bottom.add(searchButton);
        addButton.addActionListener(this);
        addButton.setEnabled(false);
        bottom.add(addButton);
        removeButton.addActionListener(this);
        removeButton.setEnabled(false);
        bottom.add(removeButton);
        checkOutButton.addActionListener(this);
        checkOutButton.setEnabled(false);
        bottom.add(checkOutButton);
        emptyButton.addActionListener(this);
        bottom.add(emptyButton);
        exitButton.addActionListener(this);
        bottom.add(exitButton);

        //Add panels to frame
        window.add(top, BorderLayout.NORTH);
        window.add(middle, BorderLayout.CENTER);
        window.add(bottom, BorderLayout.SOUTH);

        //Make window visible
        window.setVisible(true);
        invoiceWindow.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        //Bottom panel buttons
        //Search Button
        if (e.getSource() == searchButton){
            //User item ID lookup
            String userIn1 = entryIDField.getText();
            foundItem = searchItem(userIn1);
            if (foundItem == null){
                JOptionPane.showMessageDialog(window, "Item ID " + userIn1 + " is not on file");
            }
            //User item quantity lookup
            String userIn2 = entryQtyField.getText();
            userQty = Integer.parseInt(userIn2);
            Item foundItemQty = searchQty(foundItem, userQty);
            if (foundItemQty == null){
                JOptionPane.showMessageDialog(window, "Sorry... that item is out of stock, please try another");
                entryIDField.setText(null);
                entryQtyField.setText(null);
            }
            //If both item ID and item quantity are not null
            if (foundItem != null && foundItemQty != null){
                discPercent = findPercentage(foundItem, userQty); //Finds the percentage discount based on quantity
                discountPrice = findDiscountPrice(foundItem, userQty, discPercent); //Finds price with discount
                String itemDetails = displayItemDetails(foundItem, userQty, discPercent, discountPrice);
                itemInfoStr.setText(itemDetails);
                addButton.setEnabled(true); //Enables add button if item and quantity is not null
            }
        }
        //Add Button
        else if (e.getSource() == addButton) {
            isItemAdded = false;
            //Adds items to cart if row is empty
            for (int row = 0; row < cartModel.getRowCount(); row += 2) {
                if (cartModel.getValueAt(row, 0) == null) {
                    String itemDetails = displayCartItem(foundItem, userQty, discountPrice);
                    cartModel.setValueAt(itemDetails, row, 0);
                    isItemAdded = true;
                    itemCount++;
                    cartCount++;
                    cartStatus = true;
                    updateItemCount(itemCount, cartCount);
                    findCartStatus(cartStatus, cartCount);
                    subTotalTag.setText("Current Subtotal for " + cartCount + " item(s):");
                    break;
                }
            }
            //If cart is full of items
            if (!isItemAdded) {
                JOptionPane.showMessageDialog(window, "Cart is full.");
                entryIDField.setEnabled(false);
                entryIDField.setVisible(false);
                entryQtyField.setEnabled(false);
                entryQtyField.setVisible(false);
                searchButton.setEnabled(false);
                addButton.setEnabled(false);
            }
            else{
                checkOutButton.setEnabled(true);
                removeButton.setEnabled(true);
                currentPrice = discountPrice;
                subTotal += discountPrice;
                subTotalStr.setText("$" + subTotal); // Update subtotal display
                Item newQty = updateQuantity(foundItem, userQty);
                entryIDField.setText(null);
                entryQtyField.setText(null);
                addButton.setEnabled(false);
            }
            resetCurItem();
        }
        
        //Delete Last Item Added To Cart Button
        else if (e.getSource() == removeButton) {
            for (int row = cartModel.getRowCount() - 1; row >= 0; row--) {
                Object value = cartModel.getValueAt(row, 0); // Retrieve value at (row, 0)
                if (value == null && row == 1){
                    removeButton.setEnabled(false);
                }
                if (value != null) { // Check if the value is not null
                    //Extracts current row values to find discount price, user quantity for item
                    //  and ID to reset to orginal values and subtotal
                    String itemDetails = value.toString(); // Convert the value to a string
                    double itemPrice = extractPriceFromDetails(itemDetails); //Parse price from details
                    int removeQty = extractQuantityFromDetails(itemDetails); //Parse quantity from details
                    String removeID = extractItemFromDetails(itemDetails); //Parse ID from details
                    Item updateItem = findItemByID(removeID);
                    updateItem = resetQuantity(updateItem,removeQty);

                    //Subtract last item discount price from subtotal
                    subTotal -= itemPrice; // Update subtotal
                    if ((subTotal - itemPrice) < 0){ //NEED TO FIX TO ROUND TO 2 DECIMAL PLACES^^
                        subTotal = 0.00;
                    }
                    subTotalStr.setText("$" + decFormat.format(subTotal));
                    
                    //Updates num of cart items, search items, cart current status
                    cartModel.setValueAt(null, row, 0); // Clear the row
                    cartCount--;
                    itemCount--;
                    cartStatus = true;
                    updateItemCount(itemCount, cartCount);
                    findCartStatus(cartStatus, cartCount);
                    subTotalTag.setText("Current Subtotal for " + cartCount + " item(s):");
                    entryIDField.setText(null);
                    entryQtyField.setText(null);
                    break;
                }
            }
        }
        
        //Check Out Button
        else if (e.getSource() == checkOutButton){
            isCheckedOut = true;
            searchButton.setEnabled(false);
            addButton.setEnabled(false);
            removeButton.setEnabled(false);
            checkOutButton.setEnabled(false);
            entryIDField.setVisible(false);
            entryQtyField.setVisible(false);
            //LAUNCH INVOICE WINDOW
            String invoiceStr = createInvoice(cartModel, cartCount, subTotal);

            invoiceLabel.setText(invoiceStr);
            invoicePanel.add(invoiceLabel);
            invoiceWindow.add(invoicePanel);

            //Write cart to transactions file
            csvWriter writer = new csvWriter(cartModel, "transactions.csv");
            writer.writeCartToCSV();
            
            invoiceWindow.setVisible(true);

        }

        //Empty Cart - Start A New Order Button 
        else if (e.getSource() == emptyButton){
            itemCount = 1;
            cartCount = 0;
            updateItemCount(itemCount, cartCount);
            cartStatus = false;
            findCartStatus(cartStatus, cartCount);
            subTotalTag.setText("Current Subtotal for " + cartCount + " item(s):");
            resetCurItem();
            entryIDField.setVisible(true);
            entryIDField.setText(null);
            entryQtyField.setVisible(true);
            entryQtyField.setText(null);
            itemInfoStr.setText(null);
            addButton.setEnabled(false);
            removeButton.setEnabled(false);
            checkOutButton.setEnabled(false);
            subTotal = 0.00;
            subTotalStr.setText(null);

            for (int row = 0; row < cartModel.getRowCount(); row += 2) {
                Object value = cartModel.getValueAt(row, 0); // Retrieve value at (row, 0)
                if (cartModel.getValueAt(row, 0) != null) {
                    cartModel.setValueAt(null, row, 0);
                    isItemAdded = false;
                    //Extracts current row values to find discount price, user quantity for item
                    //  and ID to reset to orginal values and subtotal
                    String itemDetails = value.toString(); // Convert the value to a string
                    int removeQty = extractQuantityFromDetails(itemDetails); //Parse quantity from details
                    String removeID = extractItemFromDetails(itemDetails); //Parse ID from details
                    Item updateItem = findItemByID(removeID);
                    updateItem = resetQuantity(updateItem,removeQty);

                }
            }
            if (isCheckedOut == true){
                new GUI();
                System.out.println("Restarting...");
            }
        }
        //Exit (Close App) button
        else if (e.getSource() == exitButton){
            System.exit(0);
        }
    }
    private Item searchItem(String userInput){
        userInput = userInput.trim();

        for (Item curItem: itemList){
            if (curItem.getItemID().equalsIgnoreCase(userInput)){
                return curItem;
            }
        }
        return null;
    }

    //Checks to see if user input quantity is out of bounds
    private Item searchQty(Item curItem, int userQty){
        if (curItem.getInStock() == false || userQty <= 0){
            return null;
        }
        if (userQty > curItem.getQuantity()){
            JOptionPane.showMessageDialog(window,"Insufficient stock. Only " + curItem.getQuantity() + " on hand. Please reduce the quantity.");
            entryQtyField.setText(null);
        }
        
        return curItem;
    }

    //Returns the discount percentage of the current item based on user quantity
    private int findPercentage(Item foundItem, int userQty){
        int tempDiscount = 0;
        if (userQty >= 1 && userQty <= 4){
            return tempDiscount;
        }
        if (userQty >=5 && userQty <= 9){
            tempDiscount = 10;
        }
        if (userQty >= 10 && userQty <= 14){
            tempDiscount = 15;
        }
        if (userQty >= 15){
            tempDiscount = 20;
        }
        return tempDiscount;
    }

    //Returns the item discount based on the item discount from findPercentage()
    private double findDiscountPrice(Item foundItem, int userQty, int discPercent){
        double tempPrice = foundItem.getPrice();
        double tempPercent = discPercent / 100.0;
        tempPrice *= userQty;
        tempPrice = tempPrice - (tempPrice * tempPercent);

        tempPrice = Math.round(tempPrice * 100) / 100.0;
        return tempPrice;
    }
    
    //Returns string with item details based on user's current item and quantity
    private String displayItemDetails(Item curItem, int userQty, int discPercent, double discountPrice){
        String tempString = curItem.getItemID() + " " + curItem.getItemDescription() + " $" 
                            + curItem.getPrice() + " " + userQty + " " 
                            + discPercent + "% "+ "$" + decFormat.format(discountPrice);
        return tempString;
    }

    private String displayCartItem(Item curItem, int userQty, double discountPrice){
        String cartString = "Item " + itemCount + " - SKU: " + curItem.getItemID() + ", Desc: " 
                 + curItem.getItemDescription() + ", Price Ea. $" + curItem.getPrice()
                 + ", Qty: " + userQty + ", Total: $" + decFormat.format(discountPrice);
        
        return cartString;
    }

    //Updates the itemCount variable in user entry menu
    private void updateItemCount(int itemCount, int cartCount){
        itemIDTag.setText("Enter item ID for item #" + itemCount + ":");
        itemQtyTag.setText("Enter quantity for item #" + itemCount + ":");
        itemDetailsTag.setText("Details for Item #" + itemCount + ":");
        searchButton.setText("Search For Item #" + itemCount);
        addButton.setText("Add Item #" + itemCount + " To Cart");
    }

    //Updates the cart status based on number of items added to cart
    private void findCartStatus(boolean cartStatus, int itemCount){
        if (cartStatus == true){
            cartStatLabel.setText("Your Shopping Cart Currently Contains " 
                                  + itemCount + " Item(s)");
        }
        else{
            cartStatLabel.setText("Your Shopping Cart Is Currently Empty");
        }
    }

    //Resets the user's current item variables
    private void resetCurItem(){
        foundItem = null;
        userQty = 0;
        discPercent = 0;
        discountPrice = 0;
    }

    //Updates the item quantity after the user selects to add item to stock
    private Item updateQuantity(Item curItem, int userQty){
        int tempQty = curItem.getQuantity();
    
        tempQty -= userQty;
        if (tempQty >= 0){
            curItem.setQuantity(tempQty);
        }
        if (tempQty == 0){
            curItem.setStock(false);
        }
        return curItem;
    }

    //Returns discount price of previously added item
    //  to subtract price from subtotal
    private double extractPriceFromDetails(String itemDetails) {
        if (itemDetails == null || itemDetails.isEmpty()) {
            return 0.00; // Return 0.00 for invalid details
        }
        try {
            String[] parts = itemDetails.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("Total: $")) {
                    String priceStr = part.trim().substring(8); // Extract number after "Total: $"
                    return Double.parseDouble(priceStr);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(); //Debugging
        }
        return 0.00; // Return 0.00 if no valid price is found
    }

    private int extractQuantityFromDetails(String itemDetails){
        if (itemDetails == null || itemDetails.isEmpty()) {
            return 0; //Return 0 for invalid details
        }
        try {
            String[] parts = itemDetails.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("Qty:")) {
                    String qtyStr = part.trim().substring(5); // Extract number after "Qty: "
                    return Integer.parseInt(qtyStr);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(); //Debugging
        }
        return 0;
    }

    //Extracts itemID from last item removed from cart
    private String extractItemFromDetails(String itemDetails) {
        if (itemDetails == null || itemDetails.isEmpty()) {
            return null; // Return 0 for invalid details
        }

        try {
            String[] parts = itemDetails.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("Item")) {
                    String idStr = part.trim().substring(14); // Extract number after "SKU: "
                    return idStr; // Convert to integer
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing item ID: " + e.getMessage());
        }
        return null; // Return 0 if SKU is not found
    }
    private Item findItemByID(String removeID){
        for (Item item : itemList){
            if (item.getItemID().equalsIgnoreCase(removeID)){
                return item;
            }
        }
        return null;
    }
    //Restores an item's quantity if user selects removeButton
    private Item resetQuantity(Item updateItem, int removeQty){
        if (updateItem == null){
            System.err.println("updateItem is null!");
            return null;
        }
        updateItem.setQuantity(updateItem.getQuantity() + removeQty);
        return updateItem;
    }
    private String createInvoice(DefaultTableModel cartList, int numCartItems, double subTotal){
        String newline = System.lineSeparator();
        String date = "Date: " + csvWriter.findDateTime() + newline; 
        String lineItems = "Number of line items: " + numCartItems + newline;
        String lineTitles = "Item# / ID / Title / Price / Qty / Disc % / Subtotal:" + newline;

        //Create list of items in cart
        String invoiceItems = "";
        int numItems = 1;
        for (int row = 0; row < cartModel.getRowCount(); row ++) {
            Object value = cartModel.getValueAt(row, 0);
                if (value != null) {
                    String cartItems = value.toString();
                    String parsedCart = invoiceCartHelper(cartItems);
                    invoiceItems += numItems + ". " + parsedCart + newline;
                    numItems++;
                }
        }

        String orderSubtotal = "Order subtotal: $" + decFormat.format(subTotal)+ newline;
        int tax = 6;
        String taxRate = tax + "%"+ newline;
        double taxAmt = subTotal * ((double)tax / 100);
        String taxAmtStr = "Tax amount: $" + decFormat.format(taxAmt) + newline;
        double total = subTotal + taxAmt;
        String totalStr = "ORDER TOTAL: " + decFormat.format(total) + newline;
        String curtesyStr = "Thanks for shopping at Nile.com!"+ newline;

        String finalInvStr = date + newline + lineItems + newline + lineTitles + newline + invoiceItems +
                             newline + orderSubtotal + newline + taxRate + newline + taxAmtStr + newline + 
                             totalStr + newline + curtesyStr;
        
        System.out.println(finalInvStr);

        return finalInvStr;
    }
    private String invoiceCartHelper(String itemStr){
        String itemSku = csvWriter.extractValue(itemStr, "SKU:");
        String description = csvWriter.extractValue(itemStr, "Desc:");
        String itemPrice = csvWriter.extractValue(itemStr, "Price Ea. \\$");
        String quantity = csvWriter.extractValue(itemStr, "Qty:");
        String discount = csvWriter.findDiscountStr(quantity);
        String disPrice = csvWriter.extractValue(itemStr, "Total: \\$");
        
        return itemSku + " " + description  + " $" + itemPrice + " " + quantity + " " + discount + "% " + " $" + disPrice;
    }
    public static void main(String[] args) {
        new GUI();
    }
}