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
        int p = 0;
        ret = programName;
        LinkedList<String> lFuncional = new LinkedList<String>();
        LinkedList<String> lServico = new LinkedList<String>();
        LinkedList<String> lBooksFuncional = new LinkedList<String>();
        LinkedList<String> lBooksServico = new LinkedList<String>();
        String bookEntrada = " ";
        String bookSaida = " ";
        String bookPersistencia = " ";

        for (CobolElement lPgmCall1 : getlPgmCall()) {
            for (int j = 0; j < lPgmCall1.getContent().size(); j++) {
				if (!lPgmCall1.getContent().get(j).equals("FRWK1200")
						&& !lPgmCall1.getContent().get(j).equals("FRWK1999")){
					if(lPgmCall1.getContent().get(j).substring(4, 4).equals("4"))
						lServico.add(lPgmCall1.getContent().get(j));
					if(!lPgmCall1.getContent().get(j).substring(4, 4).equals("4"))
						lFuncional.add(lPgmCall1.getContent().get(j));
                }
            }
        }

        for (String lcopy : wrkSection.getCopyList()) {
			if (!lcopy.equals("I#FRWKGE") && !lcopy.equals("I#FRWKMD") && !lcopy.equals("I#FRWKME")
					&& !lcopy.equals("I#FRWK04") && !lcopy.equals("I#FRWKCI") && !lcopy.equals("I#FRWKDB")
					&& !lcopy.equals("FRWKWAAA") && !lcopy.equals("SQLCA") && !lcopy.contains("RFINB")
					&& !lcopy.equals("I#FRWKLI")){

				switch (lcopy.substring(7, 8)) {
				case "E":
					bookEntrada = lcopy;
					break;
				case "S":
					bookSaida = lcopy;
					break;
				case "W":
					bookPersistencia = lcopy;
					break;
				case "C":
					lBooksServico.add(lcopy);
					break;
				default:
					lBooksFuncional.add(lcopy);
					break;
				}

				if (k == 0) {
	                ret = ret + ";" + lcopy;
	            } else {
	                ret = ret + "\n        \t" + lcopy;
	            }
	            k++;
            }
        }

        ret = programName;
        k = 0;

        while(p < lBooksFuncional.size() || p < lBooksServico.size() || p < lFuncional.size() || p < lServico.size()){
        	if(k > 0){
        		ret = ret + "\n";
        	}

        	if(p < lFuncional.size()){
        		ret = ret + ";" + lFuncional.get(p);
        	}
        	else
        		ret = ret + ";" + " ";

        	if(p < lServico.size()){
        		ret = ret + ";" + lServico.get(p);
        	}
        	else
        		ret = ret + ";" + " ";

        	if(k == 0){
        		ret = ret + ";" + bookEntrada + ";" + bookSaida;
        	}

        	if(p < lBooksFuncional.size()){
        		ret = ret + ";" + lBooksFuncional.get(p);
        	}
        	else
        		ret = ret + ";" + " ";

        	if(k == 0){
        		ret = ret + ";" + bookPersistencia;
        	}

        	if(p < lBooksServico.size()){
        		ret = ret + ";" + lBooksServico.get(p);
        	}
        	else
        		ret = ret + ";" + " ";

        	k++;
        	p++;
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
