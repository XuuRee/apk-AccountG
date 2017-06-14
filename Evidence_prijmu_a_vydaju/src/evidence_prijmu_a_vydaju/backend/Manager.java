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
 * Manager is main class for editing file 'evidence'. Public methods that are
 * approachable: startYear, endYear, registerPayment and countPayments.
 * 
 * @author Lukas Suchanek, Michal Iricek, Filip Valchar, Peter Garajko
 */
public class Manager {
    
    /**
     * Method create new sheet in spreadsheet with given year and add rows 
     * 'income', 'expense' and 'sum'. Save all changes in document.
     * 
     * @param year given integer that create new sheet year
     * @return true if sheet with new year was created, false otherwise
     * @throws java.io.IOException
     */
    public boolean startYear(int year) throws IOException {
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
        if (checkIfYearExist(spreadSheet, year)) {
            System.err.println("Evidence for year: " + year + " already started");
            return false;
        }             
        
        Sheet newSheet = spreadSheet.addSheet(year + "");
        addHeading(newSheet);
        saveFile(newSheet);
        return true;
    }
    
    /**
     * Method print items 'income', 'expanse' and 'sum'. To the last row 
     * insert 'end' mark. Save all changes in document.
     * 
     * @param year given integer that ends sheet year
     * @return true if sheet has been closed, false otherwise 
     * @throws java.io.IOException 
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
     * @return true if payment was added to the document, false otherwise 
     */
    public String registerPayment(Payment payment) throws IOException {
        File file = new File("evidence.ods");
        int year = Year.now().getValue();
        
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
        if (!checkIfYearExist(spreadSheet, year)) {
            System.err.println("Evidence for this year wasn't started");
            return "NOTEXIST";
        }
        
        Sheet sheet = SpreadSheet.createFromFile(file).getSheet(String.valueOf(year));
        
        if (!checkIfYearContinue(sheet)) {
            System.err.println("Evidence for this year is already in the end");
            return "ENDED";
        }

        sheet.ensureRowCount(sheet.getRowCount() + 1);
        
        int row = sheet.getRowCount();
        if(payment.getType() == PaymentType.EXPENSE){
            sheet.getCellAt("B" + row).setValue(payment.getAmount().multiply(new BigDecimal(-1.0)));
        }else{
        sheet.getCellAt("B" + row).setValue(payment.getAmount());
        }
        sheet.getCellAt("A" + row).setValue(payment.getId());
        sheet.getCellAt("C" + row).setValue(payment.getDate());
        sheet.getCellAt("D" + row).setValue(payment.getInfo());

        recalculateSummary(sheet, payment);
        saveFile(sheet);
        return "OK";
    }
    
    /**
     * Method take given year and print actual balance sheet.
     * 
     * @return true if balance sheet is printed, false otherwise
     * @throws java.io.IOException
     * @throws evidence_prijmu_a_vydaju.backend.YearException throw if year does not exist
     */
    public String countPayments() throws IOException{
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
//        if (!checkIfYearExist(spreadSheet, year)) {
//            System.err.println("Year " + year + " wasn't started");
//            throw new YearException("year does not exist");
//        }
        
        Sheet sheet = spreadSheet.getSheet(spreadSheet.getSheetCount()-1);
//        System.out.println("bilance: " + sheet.getCellAt("B3").getTextValue());
        return "bilance: " + sheet.getCellAt("B3").getTextValue();
    }
    
    /**
     * Private method that adds items 'income', 'expense' and 'sum' to the 
     * sheet. Also name columns for given payments ('id', 'amound', 'type', 
     * 'date' and 'info'). 
     * 
     * @param sheet given sheet that we need prepare for process    
     */
    private static void addHeading(Sheet sheet) throws IOException {
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
        sheet.getCellAt("C4").setValue("date");
        sheet.getCellAt("D4").setValue("info");
    }
    
    /**
     * Private method that recalculate summary in the sheet. Modify the 
     * sheet and set values to the cells.
     * 
     * @param sheet given sheet for update
     * @param payment last payment given to the sheet
     */
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
    
    /**
     * Method check if year already exist in the spreadsheet.
     * 
     * @param spreadSheet given spreadsheet in which we want to check
     * @param year year that we want to check
     * @return true if year exist, false otherwise
     */
    private static boolean checkIfYearExist(SpreadSheet spreadSheet, int year) {
        return spreadSheet.getSheet(String.valueOf(year)) != null;
    }
    
    /**
     * Method check if the given year continues or not.
     * 
     * @param sheet given sheet with year 
     * @return true if year is actual, false otherwise
     */
    private static boolean checkIfYearContinue(Sheet sheet) {
        String last = sheet.getCellAt("A" + sheet.getRowCount()).getTextValue(); 
        return !last.equals("end");
    }

    /**
     * Save changes in the open file.
     * 
     * @param sheet sheet that we need to save
     */
    private static void saveFile(Sheet sheet) throws IOException {
        File newFile = new File("evidence.ods");
        sheet.getSpreadSheet().saveAs(newFile);
    }
    
}
