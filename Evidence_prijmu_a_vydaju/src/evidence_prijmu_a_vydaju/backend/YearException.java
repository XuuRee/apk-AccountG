/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evidence_prijmu_a_vydaju.backend;

/**
 *
 * @author lsuchanek
 */
public class YearException extends Exception {
    public YearException(){
        super(); 
    }
    public YearException(String message){
        super(message);
    }
    public YearException(String message, Throwable cause){
        super(message, cause);
    }
    public YearException(Throwable cause){
        super(cause); 
    }
}
