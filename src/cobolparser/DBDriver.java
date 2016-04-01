/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cobolparser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eduardo.sawabe
 */
public class DBDriver {
    private boolean process;
    public DBDriver(){
    }
    
    private Connection conn;
    
    public Connection openConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            conn = null;
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/RFINDB", "RFINDB", "123456");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean insertUpdateDeleteStatement(String stmt){
        if (!process)
            return true;
        boolean result = false;
        PreparedStatement pstmt;
        try {
            pstmt=  conn.prepareStatement(stmt);
            result = pstmt.execute();
            if (result)
                conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DBDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public ResultSet getResult(String stmt){
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(stmt);
            return pstmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the process
     */
    public boolean isProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(boolean process) {
        this.process = process;
    }
    
}
