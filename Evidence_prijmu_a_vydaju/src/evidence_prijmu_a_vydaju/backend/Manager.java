/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju.backend;

import java.io.File;
import java.io.IOException;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author Lenovo
 */
public class Manager {
    /**
     * 
     * @param year 
     */
    public void startYear(int year) throws IOException{
        File file = new File("evidence.ods");
        SpreadSheet sheet = SpreadSheet.createFromFile(file);
        Sheet newSheet = sheet.addSheet(year+"");
        addSums(newSheet);
        File newFile = new File("evidence.ods");
        OOUtils.open(newSheet.getSpreadSheet().saveAs(newFile));
        
    }
    /**
     * 
     * @param year 
     */
    public void endYear(int year){
    
    }
    /**
     * 
     * @param payment 
     */
    public void registerPayment(Payment payment){
    
    }
    /**
     * 
     * @param year 
     */
    public void countPayments(int year){
    
    }
    
    private static void addSums(Sheet sheet) throws IOException{
        sheet.ensureRowCount(100);
        sheet.ensureColumnCount(100);
        sheet.getCellAt("A1").setValue("income");
        sheet.getCellAt("A2").setValue("expense");
        
    }
}
