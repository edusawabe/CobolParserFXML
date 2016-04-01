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
public class WorkingStorage {
    private LinkedList<String> copyList;
    private LinkedList<String> sqlList;
    private LinkedList<CobolElement> wrkList;
    private LinkedList<TableElement> tableList;
    private LinkedList<ColumnElement> collumsList;

    public WorkingStorage(){
        copyList = new LinkedList<>();
        sqlList = new LinkedList<>();
        wrkList = new LinkedList<>();
        tableList = new LinkedList<>();
        collumsList = new LinkedList<>();
    }

    /**
     * @return the copyList
     */
    public LinkedList<String> getCopyList() {
        return copyList;
    }

    /**
     * @return the sqlList
     */
    public LinkedList<String> getSqlList() {
        return sqlList;
    }

    /**
     * @return the wrkList
     */
    public LinkedList<CobolElement> getWrkList() {
        return wrkList;
    }

    public String getWrkProgramName(String name){
        String pgmName;
        for(int i = 0; i < wrkList.size(); i++){
            if(wrkList.get(i).getName().equals(name)){
                return wrkList.get(i).getContent().get(0);
            }
        }
        return null;
    }
    
    public CobolElement getElement(String name){
        for (int i = 0; i < wrkList.size(); i++) {
            if(wrkList.get(i).getName().equals(name)){
                return wrkList.get(i);
            }
        }
        return null;
    }

    /**
     * @return the tableList
     */
    public LinkedList<TableElement> getTableList() {
        return tableList;
    }

    /**
     * @return the collumsList
     */
    public LinkedList<ColumnElement> getCollumsList() {
        return collumsList;
    }
    
    public void addColumn(String c, LinkedList<TableElement> lTable) {
        c = Util.getColumnString(c);
        ColumnElement column = new ColumnElement();
        
        if (Util.accptedValue(c)){
            if (c.contains(".")) {
                String saux[] = c.split("\\.");
                c = saux[1];
                column.setName(c);
                for (TableElement lTableItem : lTable) {
                    if (lTableItem.getAliasName().equals(saux[0])) {
                        column.setTable(lTableItem.getTableName());
                    }
                }
            } else {
                column.setName(c);
                column.setTable(lTable.get(0).getTableName());
            }

            boolean add = true;

            if (c.contains("*")) {
                add = false;
            }

            for (ColumnElement collumsList : getCollumsList()) {
                if (collumsList.equals(column)) {
                    add = false;
                }
            }
            if (add) {
                getCollumsList().add(column);
            }  
        }
    }
    /**
     * @param o = Table Owner
     * @param n = Table Name
     * @param a = Table Alias 
     * @return void
     */    
    public void addTable(String o, String n, String a) {
        boolean add = true;
        TableElement toAdd = new TableElement();
        toAdd.setOwnerName(o);
        toAdd.setAliasName(a);
        toAdd.setTableName(n);
        
        for (TableElement tableItem : tableList) {
            if(tableItem.equals(toAdd))
                add = false;
        }
        if (add){
            tableList.add(toAdd);
        }    
    }
    
    public void addCopy(String cpy){
        boolean add = true;
        for (String cpyItem : copyList) {
            if(cpyItem.equals(cpy))
                add = false;
        }
        if (add){
            copyList.add(cpy);
        }
    }
    
    public void addSql(String sql){
        boolean add = true;
        for (String sqlItem : sqlList) {
            if(sqlItem.equals(sql))
                add = false;
        }
        if (add){
            copyList.add(sql);
        }
    }
}
