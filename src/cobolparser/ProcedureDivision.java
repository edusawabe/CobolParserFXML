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
public class ProcedureDivision {
    private LinkedList<CobolElement> tabList;    
    private LinkedList<CobolElement> fieldList;
    private LinkedList<CobolElement> programLinkList; 
    
    public ProcedureDivision(){
        tabList = new LinkedList<CobolElement>();
        fieldList = new LinkedList<CobolElement>();
        programLinkList = new LinkedList<CobolElement>();
    }

    /**
     * @return the tabList
     */
    public LinkedList<CobolElement> getTabList() {
        return tabList;
    }

    /**
     * @return the fieldList
     */
    public LinkedList<CobolElement> getFieldList() {
        return fieldList;
    }

    /**
     * @return the programLinkList
     */
    public LinkedList<CobolElement> getProgramLinkList() {
        return programLinkList;
    }
}
