/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju.backend;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author Lenovo
 */
public class Manager {
    private static final String[] tableHeader = new String[] { "id", "amount", "type", "date", "info" };
    
    /**
     * 
     * @param year 
     */
    public void startYear(int year) throws IOException{
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        Sheet newSheet = spreadSheet.addSheet(year+"");
        addSums(newSheet);
        File newFile = new File("evidence.ods");
        newSheet.getSpreadSheet().saveAs(newFile);
        
    }
    /**
     * 
     * @param year 
     */
    public void endYear(int year) throws IOException{
        File file = new File("evidence.ods");
        Sheet sheet = SpreadSheet.createFromFile(file).getSheet(String.valueOf(year));
        int income;
        int expense;
        int sum;
        income = Integer.parseInt(sheet.getCellAt("B1").getTextValue());
        expense = Integer.parseInt(sheet.getCellAt("B2").getTextValue());
        sum = Integer.parseInt(sheet.getCellAt("B3").getTextValue());
        
        System.out.println("sum of income: "+income);
        System.out.println("sum of expenses: "+expense);
        System.out.println("bilance: "+sum);
    }
    /**
     * 
     * @param payment 
     */
    public void registerPayment(Payment payment) throws IOException{
        File file = new File("evidence.ods");
        int year = Year.now().getValue();
        Sheet sheet = SpreadSheet.createFromFile(file).getSheet(String.valueOf(year));
        sheet.getCellAt("A6").setValue(payment.getAmount());
        sheet.getCellAt("B6").setValue(payment.getAmount());
        sheet.getCellAt("C6").setValue(payment.getType());
        sheet.getCellAt("D6").setValue(payment.getDate());
        sheet.getCellAt("E6").setValue(payment.getInfo());
        File newFile = new File("evidence.ods");
        sheet.getSpreadSheet().saveAs(newFile);
    }
    /**
     * 
     */
    public void countPayments() throws IOException{
        File file = new File("evidence.ods");
        int year = Year.now().getValue();
        Sheet sheet = SpreadSheet.createFromFile(file).getSheet(String.valueOf(year));
        int sum;
        sum = Integer.parseInt(sheet.getCellAt("B3").getTextValue());
        System.out.println("bilance: "+sum);
    }
    
    private static void addSums(Sheet sheet) throws IOException{
        sheet.ensureRowCount(100);
        sheet.ensureColumnCount(10);
        sheet.getCellAt("A1").setValue("income");
        sheet.getCellAt("A2").setValue("expense");
        sheet.getCellAt("A3").setValue("suma");
        sheet.getCellAt("B1").setValue(0.0);
        sheet.getCellAt("B2").setValue(0.0);
        sheet.getCellAt("B3").setValue(0.0);
        sheet.getCellAt("A4").setValue("id");
        sheet.getCellAt("B4").setValue("amount");
        sheet.getCellAt("C4").setValue("type");
        sheet.getCellAt("D4").setValue("date");
        sheet.getCellAt("E4").setValue("info");
        
    }
    
    /*
    private static void createTable() {
        
    }
    */
}
