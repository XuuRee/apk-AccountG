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
import java.time.Year;
import java.util.Objects;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author xiricek
 */
public class Payment {
    
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private PaymentType type;
    private String info;
    
    public Payment() throws IOException{
        File file = new File("evidence.ods");
        int year = Year.now().getValue();
        Sheet sheet = SpreadSheet.createFromFile(file).getSheet(String.valueOf(year));
        Integer number = sheet.getRowCount() - 3;
        this.id = number.longValue();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Payment other = (Payment) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return  type  + ": " + "amount=" + amount + ", date=" + date + ", info=" + info + '}';
    }  
}
