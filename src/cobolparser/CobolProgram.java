/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cobolparser;

import java.util.LinkedList;

/**
 * @author eduardo.sawabe
 */
public class CobolProgram {
    private String programName;
    private WorkingStorage wrkSection;
    private ProcedureDivision prdDivision;
    private LinkedList<CobolElement> lPgmCall;
    
    public CobolProgram(){
        programName = new String();
        wrkSection = new WorkingStorage();
        prdDivision = new ProcedureDivision();
        lPgmCall = new LinkedList<>();
    }

    public void addProgramCall(String pgmVar){
        boolean add = true;
        pgmVar = pgmVar.replace('\'', ' ');
        pgmVar = pgmVar.replace('\n', ' ');
        pgmVar = pgmVar.replaceAll(" ", "");
        
        for (CobolElement lPgmCall1 : getlPgmCall()) {
            if (lPgmCall1.getName().equals(pgmVar)){
                add = false;
            }
        }
        if (add){
            CobolElement e = new CobolElement();
            e.setName(pgmVar);
            getlPgmCall().add(e);
        }
    }
    
    @Override
    public String toString(){
        String ret;
        int k = 0;
        ret = programName;
        
        for (CobolElement lPgmCall1 : getlPgmCall()) {            
            for (int j = 0; j < lPgmCall1.getContent().size(); j++) {
                if (k == 0)
                    ret = ret + "\t" + lPgmCall1.getContent().get(j);
                else
                    ret = ret + "\n        \t" + lPgmCall1.getContent().get(j);
                k++;
            }
        }    
        
        for (String lcopy : wrkSection.getCopyList()) {
            if (k == 0) {
                ret = ret + "\t" + lcopy;
            } else {
                ret = ret + "\n        \t" + lcopy;
            }
            k++;
        }
            
        return ret;
    }
    
    public String toStringTotal(){
        String ret;
        
        ret = "Nome Programa = " + programName + "\n";
        ret = ret + " - VARIAVEIS: " + "\n";
        for(int i = 0; i < wrkSection.getWrkList().size();i++){
            if (wrkSection.getWrkList().get(i).getContent().size() > 0) 
                ret = ret + "   " + wrkSection.getWrkList().get(i).getName() + " - " + wrkSection.getWrkList().get(i).getContent().get(0) +"\n";
            else
                ret = ret + "   " + wrkSection.getWrkList().get(i).getName() + " - " + "" +"\n";
        }
        
        ret = ret + " - COPYs: " + "\n";
        for(int i = 0; i < wrkSection.getCopyList().size();i++){
            ret = ret + "   " + wrkSection.getCopyList().get(i) +"\n";
        }
        
        ret = ret + " - INCLUDEs: " + "\n";
        for(int i = 0; i < wrkSection.getSqlList().size();i++){
            ret = ret + "   " + wrkSection.getSqlList().get(i) +"\n";
        }
        
        ret = ret + " - Program Calls: " + "\n";
        for (CobolElement lPgmCall1 : getlPgmCall()) {
            for (int j = 0; j < lPgmCall1.getContent().size(); j++) {
                ret = ret + "   " + lPgmCall1.getName() + " - " + lPgmCall1.getContent().get(j) + " \n";
            }
        }
        
        ret = ret + " - Columns: " + "\n";
        for(int i = 0; i < wrkSection.getCollumsList().size(); i++){
            ret = ret + "   " +  wrkSection.getCollumsList().get(i).getName() + " - " + wrkSection.getCollumsList().get(i).getTable() +" \n";
        }
        
        ret = ret + " - Tables: " + "\n";
        for(int i = 0; i < wrkSection.getTableList().size(); i++){
            ret = ret + "   " +  wrkSection.getTableList().get(i).getTableName() + " \n";
        }        
        return ret;
    }
    
    /**
     * @return the programName
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * @return the wrkSection
     */
    public WorkingStorage getWrkSection() {
        return wrkSection;
    }

    /**
     * @return the prdDivision
     */
    public ProcedureDivision getPrdDivision() {
        return prdDivision;
    }

    /**
     * @param programName the programName to set
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    /**
     * @return the lPgmCall
     */
    public LinkedList<CobolElement> getlPgmCall() {
        return lPgmCall;
    }

    /**
     * @param lPgmCall the lPgmCall to set
     */
    public void setlPgmCall(LinkedList<CobolElement> lPgmCall) {
        this.lPgmCall = lPgmCall;
    }
    
    
}
