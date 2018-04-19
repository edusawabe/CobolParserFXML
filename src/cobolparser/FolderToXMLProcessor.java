/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cobolparser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javafx.collections.ObservableList;
import koopa.cobol.parser.ParseResults;
import koopa.cobol.parser.ParsingCoordinator;
import koopa.cobol.sources.SourceFormat;
import koopa.core.data.Token;
import koopa.core.parsers.Parse;
import koopa.core.trees.KoopaTreeBuilder;
import koopa.core.trees.Tree;
import koopa.core.trees.XMLSerializer;
import koopa.core.util.Tuple;

/**
 *
 * @author eduardo.sawabe
 */
public class FolderToXMLProcessor {
    private File folder;
    private MyDocumentBuilder docBuilder;
    private boolean skipExistingFile;
    private DBDriver db2Driver;
    private ObservableList<ProgramsTableLine> olTabelaProgramas;
    private String processResult;
    private ParsingCoordinator coordinator;
    final static Logger logger = Logger.getLogger(FolderToXMLProcessor.class);

    public FolderToXMLProcessor(){
        docBuilder = new MyDocumentBuilder();
        skipExistingFile = false;
        coordinator = new ParsingCoordinator();
        coordinator.setFormat(SourceFormat.FIXED);
        coordinator.setPreprocessing(false);
        coordinator.setBuildTrees(true);
    }


    public void run(){
    	try {
			process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * process the folder and generates xml Files that will be processed to extract program informations
     * @param path Path to the cobol programs
     * @param skipExistingFile skips the files that already have a xml file generated
     */
    private void process() throws IOException{
        String programs = new String();

        CobolProgram pgm;
        int i = 0;
        if (olTabelaProgramas.size() > 0){
			for (int j = 0; j < olTabelaProgramas.size(); j++) {
				File lFile1 = new File(olTabelaProgramas.get(j).getArquivo());
				if ((!lFile1.getName().endsWith(".xml")) && (lFile1.isFile())) {
					File outFile = new File(lFile1.getAbsolutePath() + ".xml");
					try {
						if (outFile.exists() && !skipExistingFile) {
							outFile.createNewFile();
							processFile(lFile1, outFile, coordinator);
							pgm = docBuilder.buildFile(outFile);
							db2Driver.insertUpdateDeleteStatement(
									StatementCreator.generateInsertProgramStatement(pgm.getProgramName(), ""));
							programs = programs + pgm.toString();
							i++;
						}
						if (!outFile.exists()) {
							outFile.createNewFile();
							processFile(lFile1, outFile, coordinator);
							pgm = docBuilder.buildFile(outFile);
							programs = programs + pgm.toString();
							db2Driver.insertUpdateDeleteStatement(
									StatementCreator.generateInsertProgramStatement(pgm.getProgramName(), ""));
							i++;
						}
					} catch (IOException | ParserConfigurationException | SAXException ex) {
						logger.log(Level.ERROR, null, ex);
						olTabelaProgramas.get(j).setStatus("Erro");
					}
				}
				olTabelaProgramas.get(j).setStatus("Processado");
			}
		} else {
            File outFile = new File(folder.getAbsolutePath() + ".xml");
            if (outFile.exists() && !skipExistingFile) {
                outFile.createNewFile();
                processFile(folder, outFile, coordinator);
                try {
					pgm = docBuilder.buildFile(outFile);
	                db2Driver.insertUpdateDeleteStatement(StatementCreator.generateInsertProgramStatement(pgm.getProgramName(), ""));
	                programs = programs + pgm.toString();
                } catch (ParserConfigurationException | SAXException ex) {
                	logger.log(Level.ERROR, null, ex);
				}
            }
            if (!outFile.exists()) {
                outFile.createNewFile();
                processFile(folder, outFile, coordinator);
                try {
					pgm = docBuilder.buildFile(outFile);
	                programs = programs + pgm.toString();
	                db2Driver.insertUpdateDeleteStatement(StatementCreator.generateInsertProgramStatement(pgm.getProgramName(), ""));
                } catch (ParserConfigurationException | SAXException ex) {
                	logger.log(Level.ERROR, null, ex);
				}
            }
        }
        processResult  = programs;
    }

    /**
     * process the file and generates xml File that will be processed to extract program informations
     * @param file - the index of the file to be processed
     */
    public String processAndCreateFile(int file) throws IOException{
        int j = file;
        CobolProgram pgm = new CobolProgram();
        int i = 0;
        boolean resultOK = true;

		File lFile1 = new File(olTabelaProgramas.get(j).getArquivo());
		if ((!lFile1.getName().endsWith(".xml")) && (lFile1.isFile())) {
			File outFile = new File(lFile1.getAbsolutePath() + ".xml");
			try {
				if (outFile.exists() && !skipExistingFile) {
					outFile.createNewFile();
					resultOK = processFile(lFile1, outFile, coordinator);
				}
				if (!outFile.exists()) {
					outFile.createNewFile();
					resultOK = processFile(lFile1, outFile, coordinator);
				}
				if(resultOK){
					try {
						pgm = docBuilder.buildFile(outFile);
					} catch (ParserConfigurationException | SAXException ex) {
						logger.log(Level.ERROR, null, ex);
						olTabelaProgramas.get(j).setStatus("Erro");
				        return "";
					}
					olTabelaProgramas.get(j).setStatus("Processado");
					db2Driver.insertUpdateDeleteStatement(
							StatementCreator.generateInsertProgramStatement(pgm.getProgramName(), ""));
			        return pgm.toString();
				}
				else {
					olTabelaProgramas.get(j).setStatus("Erro");
			        return "";
				}
			} catch (IOException ex) {
				logger.log(Level.ERROR, null, ex);
			}
		}
		return "";
    }

    private boolean processFile(File source, File target, ParsingCoordinator coordinator){
		String targetPath = target.getPath();
		int dot = targetPath.lastIndexOf('.');
		if (dot < 0)
			targetPath = targetPath + ".xml";
		else
			targetPath = targetPath.substring(0, dot) + ".xml";

		target = new File(targetPath);
		// System.out.println("Writing XML to " + target);

		File targetFolder = target.getParentFile();
		if (targetFolder != null && !targetFolder.exists())
			targetFolder.mkdirs();

		ParseResults results = null;

		try {
			results = coordinator.parse(source);
		} catch (IOException ex) {
			logger.log(Level.ERROR, null, ex);
			return false;
		}

		Parse parse = results.getParse();

		if (parse.hasErrors()){
			for (Tuple<Token, String> error : parse.getErrors())
				logger.error("Error: " + error.getFirst() + " " + error.getSecond());
			return false;
		}

		if (parse.hasWarnings()){
			for (Tuple<Token, String> warning : parse.getWarnings())
				logger.info("Warning: " + warning.getFirst() + " " + warning.getSecond());
			return false;
		}

		if (!results.isValidInput()) {
			logger.error("Could not parse " + source);
			return false;
		}

		final Tree ast = results.getParse().getTarget(KoopaTreeBuilder.class).getTree();

		try {
			XMLSerializer.serialize(ast, target);

		} catch (IOException e) {
			logger.error("IOException while writing " + target);
			logger.error(e.getMessage());
			return false;
		}

		return true;
    }

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}



	public boolean isSkipExistingFile() {
		return skipExistingFile;
	}



	public void setSkipExistingFile(boolean skipExistingFile) {
		this.skipExistingFile = skipExistingFile;
	}



	public DBDriver getDb2Driver() {
		return db2Driver;
	}



	public void setDb2Driver(DBDriver db2Driver) {
		this.db2Driver = db2Driver;
	}
	public ObservableList<ProgramsTableLine> getOlTabelaProgramas() {
		return olTabelaProgramas;
	}
	public void setOlTabelaProgramas(ObservableList<ProgramsTableLine> olTabelaProgramas) {
		this.olTabelaProgramas = olTabelaProgramas;
	}


	public String getProcessResult() {
		return processResult;
	}
}
