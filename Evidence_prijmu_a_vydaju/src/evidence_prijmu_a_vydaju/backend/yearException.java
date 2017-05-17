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
public class yearException extends Exception{
    public yearException(){
        super(); 
    }
    public yearException(String message){
        super(message);
    }
    public yearException(String message, Throwable cause){
        super(message, cause);
    }
    public yearException(Throwable cause){
        super(cause); 
    }
}
