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
public class ColumnElement {
    private String name;
    private String table;
    private String tableAlias;
    
    public ColumnElement(){
        name = new String();
        table = new String();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the tableAlias
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * @param tableAlias the tableAlias to set
     */
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }
    
    public boolean equals(ColumnElement c){
        boolean equal = true;
        if((!name.isEmpty() && !name.equals(c.getName())))
            equal = false;
        if ((!table.isEmpty() && !table.equals(c.getTable())))   
             equal = false;
        
        return equal;
    }
}
