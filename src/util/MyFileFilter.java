/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author eduardo.sawabe
 */
public class MyFileFilter implements FileFilter{
    private final String[] okFileExtensions =
    new String[] {""};

    @Override
    public boolean accept(File pathname) {
        //for (String extension : okFileExtensions) {
        if (pathname.getName().toLowerCase().endsWith(".xml")) {
            return false;
        }
        //}
        return true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
