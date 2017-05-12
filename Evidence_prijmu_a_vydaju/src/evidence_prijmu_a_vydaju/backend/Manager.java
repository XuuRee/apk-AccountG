/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju.backend;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Year;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author Lenovo
 */
public class Manager {
    
    /**
     * Method create new sheet in spreadsheet with given year and add rows 
     * 'income', 'expanse' and 'sum'. Save all changes in document.
     * 
     * @param year given integer that create new sheet year
     * @return true if sheet with new year is in the file, false otherwise
     */
    public boolean startYear(int year) throws IOException {
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
        if (checkIfYearExist(spreadSheet, year)) {
            System.err.println("Evidence for year: " + year + " already started");
            return false;
        }             
        
        Sheet newSheet = spreadSheet.addSheet(year+"");
        addSums(newSheet);
        saveFile(newSheet);
        return true;
    }
    
    /**
     * Method print items 'income', 'expanse' and 'sum'. To the last row 
     * insert 'end' mark. Save all changes in document.
     * 
     * @param year given integer that create new sheet year
     * @return true if sheet was close, false otherwise 
     */
    public boolean endYear(int year) throws IOException {
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
        if (!checkIfYearExist(spreadSheet, year)) {
            System.err.println("Year " + year + " wasn't started");
            return false;
        }
        
        Sheet sheet = spreadSheet.getSheet(String.valueOf(year));
        
        BigDecimal income = (BigDecimal) sheet.getCellAt("B1").getValue();
        BigDecimal expense = (BigDecimal) sheet.getCellAt("B2").getValue();
        BigDecimal sum = (BigDecimal) sheet.getCellAt("B3").getValue();

        System.out.println("sum of income: " + income);
        System.out.println("sum of expenses: " + expense);
        System.out.println("bilance: " + sum);
            
        int row = sheet.getRowCount() + 1;
        sheet.ensureRowCount(row);
        sheet.getCellAt("A" + row).setValue("end");
        
        saveFile(sheet);
        return true;
    }
    
    /**
     * Method write payment to the document and recalculate items 'income', 
     * 'expanse' and 'sum'. All changes save to the file.
     * 
     * @param payment given payment with all details
     * @return true if payment was add to the document, false otherwise 
     */
    public boolean registerPayment(Payment payment) throws IOException {
        File file = new File("evidence.ods");
        int year = Year.now().getValue();
        
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
        if (!checkIfYearExist(spreadSheet, year)) {
            System.err.println("Evidence for this year wasn't started");
            return false;
        }
        
        Sheet sheet = SpreadSheet.createFromFile(file).getSheet(String.valueOf(year));
        
        if (!checkIfYearContinue(sheet)) {
            System.err.println("Evidence for this year is already in the end");
            return false;
        }

        sheet.ensureRowCount(sheet.getRowCount() + 1);

        int row = sheet.getRowCount();
        sheet.getCellAt("A" + row).setValue(payment.getId());
        sheet.getCellAt("B" + row).setValue(payment.getAmount());
        sheet.getCellAt("C" + row).setValue(payment.getType());
        sheet.getCellAt("D" + row).setValue(payment.getDate());
        sheet.getCellAt("E" + row).setValue(payment.getInfo());

        recalculateSummary(sheet, payment);
        saveFile(sheet);
        return true;
    }
    
    /**
     * 
     */
    public void countPayments(int year) throws IOException {
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        if (checkIfYearExist(spreadSheet, year)) {
            Sheet sheet = spreadSheet.getSheet(String.valueOf(year));
            System.out.println("bilance: " + sheet.getCellAt("B3").getTextValue());
        } else{
            System.err.println("Year " + year + " wasn't started");
        }
    }
    
    private static void addSums(Sheet sheet) throws IOException {
        sheet.ensureRowCount(4);
        sheet.ensureColumnCount(10);
        sheet.getCellAt("A1").setValue("income");
        sheet.getCellAt("A2").setValue("expense");
        sheet.getCellAt("A3").setValue("sum");
        sheet.getCellAt("B1").setValue(0.0);
        sheet.getCellAt("B2").setValue(0.0);
        sheet.getCellAt("B3").setValue(0.0);
        sheet.getCellAt("A4").setValue("id");
        sheet.getCellAt("B4").setValue("amount");
        sheet.getCellAt("C4").setValue("type");
        sheet.getCellAt("D4").setValue("date");
        sheet.getCellAt("E4").setValue("info");
    }
    
    private static void recalculateSummary(Sheet sheet, Payment payment) {
        BigDecimal income =(BigDecimal) sheet.getCellAt("B1").getValue();
        BigDecimal expense = (BigDecimal) sheet.getCellAt("B2").getValue();
        BigDecimal sum = (BigDecimal) sheet.getCellAt("B3").getValue();
        
        if (payment.getType() == PaymentType.INCOME){
            sum = sum.add(payment.getAmount());
            income = income.add(payment.getAmount());
        } else if (payment.getType() == PaymentType.EXPENSE){
            sum = sum.subtract(payment.getAmount());
            expense = expense.add(payment.getAmount());
        }
        
        sheet.getCellAt("B1").setValue(income);
        sheet.getCellAt("B2").setValue(expense);
        sheet.getCellAt("B3").setValue(sum);
    }
    
    private static boolean checkIfYearExist(SpreadSheet spreadSheet, int year) {
        return spreadSheet.getSheet(String.valueOf(year)) != null;
    }
    
    private static boolean checkIfYearContinue(Sheet sheet) {
        String last = sheet.getCellAt("A" + sheet.getRowCount()).getTextValue(); 
        return !last.equals("end");
    }

    private static void saveFile(Sheet sheet) throws IOException {
        File newFile = new File("evidence.ods");
        sheet.getSpreadSheet().saveAs(newFile);
    }
    
}
