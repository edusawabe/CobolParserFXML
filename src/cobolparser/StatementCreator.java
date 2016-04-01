/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cobolparser;

/**
 *
 * @author eduardo.sawabe
 */
public final class StatementCreator {
    
public StatementCreator (){
}

public static String generateInsertProgramStatement(String name, String desc){
    String stmt;
    if (desc.isEmpty()){
        stmt = "INSERT INTO RFINDB.\"PROGRAMA\"(\"CODIGO_PROGRAMA\") VALUES ('" + name + "')";
    } else{
        stmt = "INSERT INTO RFINDB.\"PROGRAMA\"(\"CODIGO_PROGRAMA\", DESC_PROGRAMA) VALUES ('" + name + "', '" + desc + "')";
    }  
    return stmt;
}

}
