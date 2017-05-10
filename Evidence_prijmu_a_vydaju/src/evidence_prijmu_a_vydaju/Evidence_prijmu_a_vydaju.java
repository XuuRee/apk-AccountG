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
        //createFile();
        
        Manager man = new Manager();
        
        Payment pay = new Payment();
        Payment pay2 = new Payment();
        
        pay.setAmount(new BigDecimal(20000));
        pay.setType(PaymentType.EXPENSE);
        pay.setDate(LocalDate.now());
        pay.setInfo("Info");
        
        pay2.setAmount(new BigDecimal(30000));
        pay2.setType(PaymentType.INCOME);
        pay2.setDate(LocalDate.now());
        pay2.setInfo("New Info");
        
        //man.startYear(2017);
        //man.registerPayment(pay);
        //man.countPayments(2017);
        man.registerPayment(pay2);
        //man.countPayments();
        //man.endYear(2017);
        
        
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
