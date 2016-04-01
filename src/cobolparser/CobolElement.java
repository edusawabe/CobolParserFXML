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
public class CobolElement {
    private String name;
    private LinkedList<String> content;

    public CobolElement(){
        name = new String();
        content = new LinkedList<>();
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
     * @return the content
     */
    public LinkedList<String> getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(LinkedList<String> content) {
        this.content = content;
    }

    public void addContent(String pgmVar){
        boolean add = true;
        for (String contentv : getContent()) {
            if (contentv.equals(pgmVar)){
                add = false;
            }
        }
        if (add){
            pgmVar = pgmVar.replace('\'',' ');
            pgmVar = pgmVar.replace('\n',' ');
            pgmVar = pgmVar.replaceAll(" ","");
            getContent().add(pgmVar);
        }
    }

}
