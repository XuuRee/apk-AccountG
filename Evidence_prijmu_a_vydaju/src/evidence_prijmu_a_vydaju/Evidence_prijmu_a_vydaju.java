/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju;

import evidence_prijmu_a_vydaju.backend.Manager;
import evidence_prijmu_a_vydaju.backend.Payment;
import evidence_prijmu_a_vydaju.backend.PaymentType;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        Payment pay = new Payment();
        
        pay.setId(new Long(1));
        pay.setAmount(new BigDecimal(153324));
        pay.setType(PaymentType.INCOME);
        pay.setDate(LocalDate.now());
        pay.setInfo("Info");
        
        //man.startYear(2019);
        man.registerPayment(pay);
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
