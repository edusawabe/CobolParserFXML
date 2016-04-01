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
public class TableElement {
    private String ownerName;
    private String tableName;
    private String aliasName; 
    
    public TableElement(){
        ownerName = new String();
        tableName = new String();
        aliasName = new String();
    }

    public TableElement(String o, String t, String a){
        ownerName = o;
        tableName = t;
        aliasName = a;
    }
    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the aliasName
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * @param aliasName the aliasName to set
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * @return the OwnerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param OwnerName the OwnerName to set
     */
    public void setOwnerName(String OwnerName) {
        this.ownerName = OwnerName;
    }
    
    public boolean equals(TableElement t){
        return (ownerName.equals(t.getOwnerName()) && tableName.equals(t.getTableName()) && aliasName.equals(t.getAliasName()));
    }
}
