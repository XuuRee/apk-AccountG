/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju;

import java.io.File;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author xiricek
 */
public class Evidence_prijmu_a_vydaju {

    /**
     * 
     * @param args
     */
     
    public static void main(String[] args) throws IOException{
        createFile();
    }
    
    private static void createFile() throws IOException{
        final Object[][] data = new Object[0][0];
        
        String[] columns = new String[] {};
        TableModel model = new DefaultTableModel(data,columns);
        
        final File file = new File("evidence.ods");
        SpreadSheet.createEmpty(model).saveAs(file);
    }
    
    private static void addSums() throws IOException{
        File file = new File("evidence.ods");
        final Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);
        sheet.getCellAt("A1").setValue("Neco");
    }
    
}
