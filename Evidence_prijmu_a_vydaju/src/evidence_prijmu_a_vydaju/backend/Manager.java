/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju.backend;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
    public String startYear(int year) throws IOException {
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        
        if (checkIfYearExist(spreadSheet, year)) {
            System.err.println("Evidence for year: " + year + " already started");
            return "STARTED";
        }
        if(checkIfYearContinue(spreadSheet.getSheet(spreadSheet.getSheetCount()-1))){
            return "PREVIOUS";
        }
        
        Sheet newSheet = spreadSheet.addSheet(year + "");
        addHeading(newSheet);
        saveFile(newSheet);
        createXmlYear(year);
        return "OK";
    }
    
    /**
     * Method print items 'income', 'expense' and 'sum'. To the last row 
     * insert 'end' mark. Save all changes in document.
     * 
     * @return true if sheet has been closed, false otherwise 
     * @throws java.io.IOException 
     */
    public String endYear() throws IOException {
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        int year = actualYear();
        if (!checkIfYearExist(spreadSheet, year)) {
            return  "NOTSTARTED";
        }
        
        Sheet sheet = spreadSheet.getSheet(String.valueOf(year));
        
        BigDecimal income = (BigDecimal) sheet.getCellAt("B1").getValue();
        BigDecimal expense = (BigDecimal) sheet.getCellAt("B2").getValue();
        BigDecimal sum = (BigDecimal) sheet.getCellAt("B3").getValue();

        
        String string ="year: " + year + "\nsum of income: " + income + "\nsum of expenses: " + expense
                + "\nbilance: " + sum;     
        int row = sheet.getRowCount() + 1;
        sheet.ensureRowCount(row);
        sheet.getCellAt("A" + row).setValue("end");
        
        saveFile(sheet);
        return string;
    }
    
    /**
     * Method write payment to the document and recalculate items 'income', 
     * 'expanse' and 'sum'. All changes save to the file.
     * 
     * @param payment given payment with all details
     * @return true if payment was added to the document, false otherwise
     * @throws IOException
     */
    public String registerPayment(Payment payment) throws IOException {
        File file = new File("evidence.ods");        
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        int year = Integer.parseInt(spreadSheet.getSheet(spreadSheet
                .getSheetCount()-1).getName());
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
        createXmlPayment(payment);
        recalculateSummary(sheet, payment);
        saveFile(sheet);
        return "OK";
    }
    
    /**
     * Method take given year and print actual balance sheet.
     * 
     * @return balance of current year
     * @throws java.io.IOException
     */
    public String countPayments() throws IOException{
        try{
            Integer.parseInt(getActualSheet().getName());
        }catch(NumberFormatException ex){
            return "ERROR";
        }
        return "Balance: " + getActualSheet().getCellAt("B3").getTextValue() + "\n";
    }
    
    /**
     * Method get string of incomes during current year 
     * 
     * @return incomes of current year
     * @throws IOException 
     */
    public String countIncomes() throws IOException{        
        return "Incomes: " + getActualSheet().getCellAt("B1").getTextValue() + "\n";
    }
    
    /**
     * Method get string of expense during current year 
     * 
     * @return expense of current year
     * @throws IOException 
     */
    public String countExpense() throws IOException{        
        return "Expences: " + getActualSheet().getCellAt("B2").getTextValue() + "\n";
    }
    
    /**
     * Prepare string for output from expense, incomes and payments
     * @return string for output
     * @throws IOException 
     */
    public String countsOutput() throws IOException,ArrayIndexOutOfBoundsException{
        String payColor = "green";
        String payments = getActualSheet().getCellAt("B3").getTextValue();        
        
        
        if(payments.contains("-")){
            payColor = "red";
        }
        return "<html>" + countIncomes()+"<br/>"+countExpense() +"<br/>"
                + "<font color="+payColor+">"+countPayments()+"</font></html>";
           
    }
    
    public List<Payment> getAllPayments() throws IOException {
        List<Payment> payments = new ArrayList<>();
        Sheet sheet = getActualSheet();
        if (sheet.getCellAt("A" + sheet.getRowCount()).getTextValue().equals("end")) {
            return payments;
        }
        for (int i = 5; i < sheet.getRowCount()+1; i++) {
            payments.add(new Payment());
            payments.get(i-5).setAmount((BigDecimal) sheet.getCellAt("B"+i).getValue());
            payments.get(i-5).setDate(LocalDate.parse(sheet.getCellAt("C"+i).getTextValue()));
            payments.get(i-5).setInfo(sheet.getCellAt("D"+i).getTextValue());
            if (payments.get(i-5).getAmount().doubleValue() >= 0.0) {
                payments.get(i-5).setType(PaymentType.INCOME);
            } else {
                payments.get(i-5).setType(PaymentType.EXPENSE);
            }
        }
        return payments;
    }
    
    /**
     * Method get actual year of evidence
     * @return number of year
     * @throws IOException when problem with sheet
     */
    private int actualYear() throws IOException{
        try {
            return Integer.parseInt(getActualSheet().getName());
        } catch (NumberFormatException e) {
            System.err.println("You have to start year first");
            return 0;
        }
     }
    
    /**
     * Method get sheet for current year
     * @return sheet which contains information about current year
     * @throws IOException 
     */
    private Sheet getActualSheet() throws IOException{
        File file = new File("evidence.ods");
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(file);
        return spreadSheet.getSheet(spreadSheet.getSheetCount()-1);
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
        Sheet sheet = spreadSheet.getSheet(String.valueOf(year));
        return sheet != null;
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
    
    /**
     * create xml year element in xml document 
     * @param year for creating element
     */
    private void createXmlYear(int year){
        Document doc = initialize();
        Element parent = doc.getDocumentElement();
        Element docYear = doc.createElement("year");
        docYear.setAttribute("yid", Integer.toString(year));
        parent.appendChild(docYear);
        transformToXml(doc);
    }
    /**
     * create xml payment element in xml document 
     * @param payment for creating element
     */
    private void createXmlPayment(Payment payment){
        Document doc = initialize();
        NodeList list = doc.getElementsByTagName("year");
        Element parent = (Element)list.item(list.getLength()-1);
        Element paymentElem = doc.createElement("payment");
        paymentElem.setAttribute("pid", payment.getId().toString());
        String type = "expence";
        if (payment.getType() == PaymentType.INCOME){
           type = "income";
        }
        Element typeXml = doc.createElement("type");
        Element dateXml = doc.createElement("date");
        Element infoXml = doc.createElement("info");
        Element ammountXml = doc.createElement("ammount");
        typeXml.appendChild(doc.createTextNode(type));
        dateXml.appendChild(doc.createTextNode(payment.getDate().toString()));
        infoXml.appendChild(doc.createTextNode(payment.getInfo()));
        ammountXml.appendChild(doc.createTextNode(payment.getAmount().toString()));
        paymentElem.appendChild(typeXml);
        paymentElem.appendChild(dateXml);
        paymentElem.appendChild(infoXml);
        paymentElem.appendChild(ammountXml);
        parent.appendChild(paymentElem);  
        transformToXml(doc);
    }
    
    /**
     * Method initialize xml document
     */
    private Document initialize() {
        Document doc = null;
        try {
            File file = new File("./evidence.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Parser failed parse file:"+e.getMessage());
        }
        return doc;
    }
    
    /**
     * Method transform document back to output file
     * @param doc document for transformation
     */
    private void transformToXml(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc),new StreamResult(new File("./evidence.xml")));
        } catch (TransformerException e) {
            System.err.println("Transformation failed");
        }
    }
}
