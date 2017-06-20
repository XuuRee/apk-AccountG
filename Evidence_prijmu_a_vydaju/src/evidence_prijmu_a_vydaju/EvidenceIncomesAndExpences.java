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
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Main class for program.
 * 
 * @author Lukas Suchanek, Michal Iricek Filip Valchar, Peter Garajko
 */
public class EvidenceIncomesAndExpences {

    /**
     * Main method for illustration working program. 
     * Possible command sequence.
     * @param args arguments of program when start
     */
    public static void main(String[] args) {
        try {
            createFile();
        } catch(IOException e) {
            System.err.println("Problem with file read or write: "+e.getMessage());
        }
        Manager man = new Manager();
        EvidenceGUI.startGUI();
    }
    
    /**
     * Private method that create new spreadsheet with opening sheet.
     */
    private static void createFile() throws IOException{
        final Object[][] data = new Object[3][2];
        data[0][0]="PB138 / Moderni znackovaci jazyky a jejich aplikace";
        data[1][0]="Lukas Suchanek, Michal Iricek, Peter Grajko, Filip Valchar";
        data[2][0]="end";
        
        String[] columns = new String[] {"Evidence prijmu a vydaju"};
        TableModel model = new DefaultTableModel(data, columns);
        
        final File file = new File("evidence.ods");
        if(!file.exists()){
            SpreadSheet.createEmpty(model).saveAs(file);
        }
    }
    
}
