import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

public class csvWriter {
    private final DefaultTableModel cartModel;
    private final String fileName;

    public csvWriter(DefaultTableModel cartModel, String fileName){
        this.cartModel = cartModel;
        this.fileName = fileName;
    }

    public void writeCartToCSV(){
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(fileName, true))){
            for(int row = 0; row < cartModel.getRowCount(); row++){
                Object value = cartModel.getValueAt(row, 0);
                if (value != null){
                    String cartItemDetails = value.toString();
                    String[] parsedItems = parsedCartDetails(cartItemDetails);
                    buffWriter.write(String.join(", ", parsedItems));
                    buffWriter.newLine();
                }
            }
            
        } 
        catch (IOException e) {
            System.err.println("Error writing cart to transaction.csv" + e.getMessage());
        }
    }

    private String[] parsedCartDetails(String cartItemDetails){
        String trasactionID = findTransactionID();
        String itemStr = extractValue(cartItemDetails, "SKU:");
        String descStr = extractValue(cartItemDetails, "Desc:");
        String itemPriceStr = extractValue(cartItemDetails, "Price Ea. \\$");
        String qtyStr = extractValue(cartItemDetails, "Qty:");
        String discountStr = findDiscountStr(qtyStr);
        String totalStr = extractValue(cartItemDetails, "Total: \\$");
        String cartDateTimeStr = findDateTime();

        return new String[]{trasactionID, itemStr, descStr, itemPriceStr, qtyStr,
                            discountStr,  "$" + totalStr, cartDateTimeStr};
    }

    private String findTransactionID(){
        LocalDateTime dateTimeObj = LocalDateTime.now();
        DateTimeFormatter modDateTime = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        String newDateTime = dateTimeObj.format(modDateTime);
        return newDateTime;
    }

    public static String extractValue(String cartItemDetails, String key){
        String[] parts = cartItemDetails.split(key);
        if (parts.length > 1){
            String curValue = parts[1].split(",")[0].trim();
            return curValue;
        }
        return "";
    }

    public static String findDiscountStr(String quantity){
        int tempQty = Integer.parseInt(quantity);
        if (tempQty >= 5 && tempQty <= 9){
            return "0.1";
        }
        if (tempQty >= 10 && tempQty <= 14){
            return "0.15";
        }
        if (tempQty >= 15){
            return "0.2";
        }
        return "0.0";
    }

    public static String findDateTime(){
        LocalDateTime dateTimeObj = LocalDateTime.now();
        ZoneId timeZoneID = ZoneId.systemDefault();
        ZonedDateTime timeZone = dateTimeObj.atZone(timeZoneID);
        
        DateTimeFormatter modDateTime = DateTimeFormatter.ofPattern("MMMM dd, yyyy, hh:mm:ss a z");
        String newDateTime = timeZone.format(modDateTime);
        
        return newDateTime;
    }
}
