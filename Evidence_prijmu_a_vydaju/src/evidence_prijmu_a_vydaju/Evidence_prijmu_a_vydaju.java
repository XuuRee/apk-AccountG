/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju;

import evidence_prijmu_a_vydaju.backend.Manager;
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
        
        Manager man = new Manager();
        
        man.startYear(2017);
        man.countPayments();
        //man.endYear(2020);
        
        
    }
    
    private static void createFile() throws IOException{
        final Object[][] data = new Object[2][2];
        data[0][0]=new String("income");
        data[1][0]=new String("expense");
        
        String[] columns = new String[] {"prehled"};
        TableModel model = new DefaultTableModel(data,columns);
        
        final File file = new File("evidence.ods");
        SpreadSheet.createEmpty(model).saveAs(file);
    }
    
    
    
}
