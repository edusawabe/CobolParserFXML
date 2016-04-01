/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cobolparser;

import java.util.LinkedList;

/**
 *
 * @author eduardo.sawabe
 */
public final class Util {
    public Util(){
    }
    
    public static String getColumnString(String s){
        LinkedList<Integer> lAuxOpen = new LinkedList<>();
        LinkedList<Integer> lAuxClose = new LinkedList<>();
        
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(')
            {
                lAuxOpen.add(new Integer(i));
            }
            if (s.charAt(i) == ')')
            {
                lAuxClose.add(new Integer(i));
            }
        }
        
        if(lAuxClose.size() > 0 && lAuxOpen.size() > 0)
            s = (String) s.subSequence(lAuxOpen.get(lAuxOpen.size()-1)+1, lAuxClose.get(lAuxClose.size()-1));
        
        return s;
    }
    
    public static boolean accptedValue(String s){
        
        switch (s) {
            case "{":
                return false;
            case "}":
                return false;
            case "]":
                return false;
            case "[":
                return false;
            case "%":
                return false;
            case "/":
                return false;
            case "*":
                return false;                
            case ":":
                return false;
            case "AND":
                return false;
            case "OR":
                return false;    
            case "SET":
                return false;  
            case "WHERE":
                return false;  
            case "COUNT":
                return false;                 
            case "RFINB":
                return false;  
            case "=":
                return false;  
            case ",":
                return false;              
            case ".":
                return false;  
            case "(":
                return false;   
            case ")":
                return false;                   
            default:
                if (s.contains("RFINB") || s.isEmpty() || s.contains("-"))
                    return false;
                else
                    return !isNumeric(s);
        }        
    }
    
    private static boolean isNumeric(String str){
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
