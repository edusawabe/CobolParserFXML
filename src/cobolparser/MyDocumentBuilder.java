/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cobolparser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author eduardo.sawabe
 */
public class MyDocumentBuilder {

    public MyDocumentBuilder() {
    }

	public CobolProgram buildFile(File file) throws ParserConfigurationException, SAXException, IOException {
		CobolProgram pgm = new CobolProgram();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		docBuilder = dbf.newDocumentBuilder();
		// realiza parse do arquivo
		Document doc = docBuilder.parse(file);
		// otem documento
		Element element = doc.getDocumentElement();
		buildFileWorkigStorage(pgm, element);
		buildFileProcedureDivisionProgramCalls(pgm, element);
		buildProgramCalls(pgm, element);
		buildDB2Elments(pgm, element);

		return pgm;
	}

    private void buildFileWorkigStorage(CobolProgram pgm, Element element) {
        //obtem nome do programa
        Element programID = (Element) element.getElementsByTagName("programName").item(0);
        Element cobolword = (Element) programID.getElementsByTagName("cobolWord").item(0);
        pgm.setProgramName(cobolword.getElementsByTagName("t").item(0).getTextContent());

        //ystem.out.println("Processsing Program: " + cobolword.getElementsByTagName("t").item(0).getTextContent());

        //obtem node da WORKING STAORAGE SECTION
        Element workingStorage = (Element) element.getElementsByTagName("workingStorageSection").item(0);

        //obtem node da WORKING STAORAGE SECTION
        Element dataDivision = (Element) element.getElementsByTagName("dataDivision").item(0);

        //obtem node da PROCEDURE DIVISION
        Element procedureDivision = (Element) element.getElementsByTagName("procedureDivision").item(0);

        //obtem LISTA DE VARIAVEIS da WORKING STAORAGE SECTION
        NodeList wrkList = workingStorage.getElementsByTagName("dataDescriptionEntry");
        for (int i = 0; i < wrkList.getLength(); i++) {
            Element e = (Element) wrkList.item(i);
            Element varName = (Element) e.getElementsByTagName("entryName").item(0);
            if (varName != null) {
                Element valueClause = (Element) e.getElementsByTagName("valueClause").item(0);
                NodeList listName = varName.getElementsByTagName("t");
                if (listName != null) {
                    Element name = (Element) listName.item(0);
                    if (name.getTextContent().contains("WRK") && (valueClause != null)) {
                        CobolElement cobolElement = new CobolElement();
                        cobolElement.setName(varName.getElementsByTagName("t").item(0).getTextContent());
                        Element literal = (Element) valueClause.getElementsByTagName("literal").item(0);
                        if (literal != null) {
                            Element literalValue = (Element) literal.getElementsByTagName("literalValue").item(0);
                            Element alphanumericLiteral = (Element) literalValue.getElementsByTagName("alphanumericLiteral").item(0);
                            if (alphanumericLiteral != null) {
                                if (alphanumericLiteral.getElementsByTagName("t").item(0).getTextContent() != null) {
                                    String pgmVar;
                                    pgmVar = alphanumericLiteral.getElementsByTagName("t").item(0).getTextContent();
                                    pgmVar = pgmVar.replace('\n', ' ');
                                    pgmVar = pgmVar.replace('\'', ' ');
                                    pgmVar = pgmVar.replaceAll(" ", "");
                                    cobolElement.getContent().add(pgmVar);
                                    pgm.getWrkSection().getWrkList().add(cobolElement);
                                }
                            } else { // add Cobol program without alphnumeric name definition
                                pgm.getWrkSection().getWrkList().add(cobolElement);
                            }
                        }
                    }
                }
            }
        }

        //obtem LISTA DE COPY BOOKS da WORKING STAORAGE SECTION
        NodeList copyList = workingStorage.getElementsByTagName("copyStatement");
        for (int i = 0; i < copyList.getLength(); i++) {
            Element e = (Element) copyList.item(i);
            Element alphanumericLiteral = (Element) e.getElementsByTagName("alphanumericLiteral").item(0);
            Element cobolWord = (Element) e.getElementsByTagName("cobolWord").item(0);
            if (alphanumericLiteral != null) {
                String pgmVar;
                pgmVar = alphanumericLiteral.getElementsByTagName("t").item(0).getTextContent();
                pgmVar = pgmVar.replace('\n', ' ');
                pgmVar = pgmVar.replace('\'', ' ');
                pgmVar = pgmVar.replaceAll(" ", "");
                pgm.getWrkSection().addCopy(pgmVar);
            }
            if (cobolWord != null) {
                String pgmVar;
                pgmVar = cobolWord.getElementsByTagName("t").item(0).getTextContent();
                pgmVar = pgmVar.replace('\n', ' ');
                pgmVar = pgmVar.replace('\'', ' ');
                pgmVar = pgmVar.replaceAll(" ", "");
                pgm.getWrkSection().addCopy(pgmVar);
            }
        }

        //obtem LISTA DE INCLUDES da WORKING STAORAGE SECTION
        NodeList sqlList = workingStorage.getElementsByTagName("sqlStatement");
        for (int i = 0; i < sqlList.getLength(); i++) {
            Element e = (Element) sqlList.item(i);
            Element includeStatement = (Element) e.getElementsByTagName("includeStatement").item(0);
            if (includeStatement != null) {
                Element identifier = (Element) includeStatement.getElementsByTagName("identifier").item(0);

                String pgmVar;
                pgmVar = identifier.getElementsByTagName("t").item(0).getTextContent();
                pgmVar = pgmVar.replace('\n', ' ');
                pgmVar = pgmVar.replace('\'', ' ');
                pgmVar = pgmVar.replaceAll(" ", "");

                pgm.getWrkSection().addSql(pgmVar);
            }
        }

      //obtem LISTA DE DATASET NAMES da WORKING STAORAGE SECTION
        NodeList fileList = dataDivision.getElementsByTagName("fdFileDescriptionEntry");
        for (int i = 0; i < fileList.getLength(); i++) {
            Element e = (Element) fileList.item(i);
            Element fileName = (Element) e.getElementsByTagName("fileName").item(0);
            Element cobolWord = (Element) fileName.getElementsByTagName("cobolWord").item(0);
            if (cobolWord != null) {
                String ddName;
                ddName = cobolWord.getElementsByTagName("t").item(0).getTextContent();
                ddName = ddName.replace('\n', ' ');
                ddName = ddName.replace('\'', ' ');
                ddName = ddName.replaceAll(" ", "");
                pgm.getWrkSection().getArqList().add(ddName);
            }
        }
    }

    private void buildDB2Elments(CobolProgram pgm, Element element) {
        buildDB2ElmentsSelectCursor(pgm, element);
        buildDB2ElmentsUpdate(pgm, element);
        buildDB2ElmentsInsert(pgm, element);
        buildDB2ElmentsDelete(pgm, element);
    }

    private void buildDB2ElmentsSelectCursor(CobolProgram pgm, Element element) {
        LinkedList<TableElement> ltable = new LinkedList<>();

        //obtem LISTA DE chamadas SQL
        NodeList declareList = element.getElementsByTagName("sqlStatement");
        for (int i = 0; i < declareList.getLength(); i++) {
            ltable.clear();
            Element e = (Element) declareList.item(i);
            Element selectStatement = (Element) e.getElementsByTagName("selectStatement").item(0);
            if (selectStatement != null) {
                //obtem nome das tabelas
                NodeList elementsT = selectStatement.getElementsByTagName("t");
                boolean fromStatement = false;
                boolean whereStatement = false;
                int fromIndex = 0;
                int whereIndex = 0;

                for (int u = 0; u < elementsT.getLength(); u++) {
                    Element eT = (Element) elementsT.item(u);
                    if (eT.getTextContent().contains("FROM")) {
                        fromIndex = u;
                    }
                    if (eT.getTextContent().contains("WHERE")) {
                        whereIndex = u;
                        break;
                    }
                }
                if (whereIndex == 0) {
                    whereIndex = elementsT.getLength();
                }

                for (int u = fromIndex; u < whereIndex; u++) {
                    Element eT = (Element) elementsT.item(u);
                    if (eT.getTextContent().contains("FROM")) {
                        fromStatement = true;
                    }
                    if (eT.getTextContent().contains("WHERE") && fromStatement) {
                        fromStatement = false;
                        whereStatement = true;
                    }
                    if (fromStatement) {
                        if (!eT.getTextContent().contains("FROM")) {
                            String tableName = new String();
                            String owner = new String();
                            String alias = new String();
                            String str = eT.getTextContent();
                            str = str.replaceAll("\t", " ");
                            str = str.replaceAll("\n", " ");
                            str = str.replaceAll(" ", "");
                            if (!str.isEmpty()) {
                                if (str.equals("DB2PRD")) {
                                    owner = str;
                                    //indice do nome da tabela
                                    int k = u + 1;
                                    //get table name
                                    while (k < whereIndex) {
                                        String str2 = ((Element) elementsT.item(k)).getTextContent();
                                        str2 = str2.replaceAll("\t", " ");
                                        str2 = str2.replaceAll("\n", " ");
                                        str2 = str2.replaceAll(" ", "");
                                        if (!str2.isEmpty()) {
                                            if (str2.equals(",")) {
                                                u = k;
                                                break;
                                            }
                                            if (tableName.isEmpty()) {
                                                if (!str2.equals(".")) {
                                                    tableName = str2;
                                                }
                                            } else {
                                                alias = str2;
                                                pgm.getWrkSection().addTable(owner, tableName, alias);
                                                ltable.add(new TableElement(owner, tableName, alias));
                                            }
                                        }
                                        k++;
                                    }
                                    pgm.getWrkSection().addTable(owner, tableName, alias);
                                    ltable.add(new TableElement(owner, tableName, alias));
                                }
                            }
                        }
                    }
                }

                // Get Collumns Names
                Element columnsNode = (Element) selectStatement.getElementsByTagName("unknown").item(0);
                NodeList columnsList = columnsNode.getElementsByTagName("t");
                String column = new String();
                String tmpColumn;
                boolean beginColumnName = true;
                boolean variableClause = false;
                boolean firstAdd = true;

                for (int j = 0; j < columnsList.getLength(); j++) {
                    Element columnNode = (Element) columnsList.item(j);
                    tmpColumn = columnNode.getTextContent().replace('\t', ' ');
                    tmpColumn = tmpColumn.replace('\n', ' ');
                    tmpColumn = tmpColumn.replaceAll(" ", "");
                    if (tmpColumn.isEmpty()) {
                        if (!beginColumnName && !column.isEmpty()) {
                            pgm.getWrkSection().addColumn(column, ltable);
                            firstAdd = false;
                            beginColumnName = true;
                            column = "";
                        }
                    } else {
                        if (tmpColumn.contains(":")) {
                            variableClause = true;
                        }
                        if (!variableClause) {
                            if (beginColumnName) {
                                if (tmpColumn.contains(",")) {
                                    column = tmpColumn.replaceAll(",", "");
                                    beginColumnName = false;
                                } else {
                                    if (firstAdd) {
                                        column = tmpColumn;
                                        beginColumnName = false;
                                    } else {
                                        beginColumnName = true;
                                        tmpColumn = "";
                                    }
                                }
                            } else {
                                if (tmpColumn.contains(",")) {
                                    beginColumnName = true;
                                    pgm.getWrkSection().addColumn(column, ltable);
                                    firstAdd = false;
                                    column = "";
                                } else {
                                    if (!tmpColumn.contains("-")) {
                                        column = column + tmpColumn;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!column.isEmpty()) {
                    pgm.getWrkSection().addColumn(column, ltable);
                }
            }
        }
    }

    private void buildDB2ElmentsUpdate(CobolProgram pgm, Element element) {
        LinkedList<TableElement> ltable = new LinkedList<>();

        //obtem LISTA DE chamadas SQL
        NodeList declareList = element.getElementsByTagName("sqlStatement");
        for (int i = 0; i < declareList.getLength(); i++) {
            ltable.clear();
            Element e = (Element) declareList.item(i);
            Element updateStatement = (Element) e.getElementsByTagName("updateStatement").item(0);
            if (updateStatement != null) {
                //obtem nome das tabelas
                NodeList elementsT = updateStatement.getElementsByTagName("t");
                String owner = "";
                String tableName = "";
                String alias = "";
                int db2prdIndex = 0;
                int setIndex = 0;

                for (int u = 0; u < elementsT.getLength(); u++) {
                    Element eT = (Element) elementsT.item(u);
                    if (eT.getTextContent().equals("DB2PRD")) {
                        db2prdIndex = u;
                    }
                    if (eT.getTextContent().equals("SET")) {
                        setIndex = u;
                        break;
                    }
                }

                owner = ((Element) elementsT.item(db2prdIndex)).getTextContent();
                tableName = ((Element) elementsT.item(db2prdIndex + 2)).getTextContent();
                ltable.add(new TableElement(owner, tableName, alias));
                pgm.getWrkSection().addTable(owner, tableName, alias);

                // Get Collumns Names
                for (int u = setIndex; u < elementsT.getLength(); u++) {
                    Element eT = (Element) elementsT.item(u);
                    String columnToAdd = eT.getTextContent();
                    columnToAdd = columnToAdd.replace('\n', ' ');
                    columnToAdd = columnToAdd.replace('\t', ' ');
                    columnToAdd = columnToAdd.replaceAll(" ", "");
                    pgm.getWrkSection().addColumn(columnToAdd, ltable);
                }
            }
        }
    }

    private void buildDB2ElmentsInsert(CobolProgram pgm, Element element) {
        LinkedList<TableElement> ltable = new LinkedList<>();

        //obtem LISTA DE chamadas SQL
        NodeList declareList = element.getElementsByTagName("sqlStatement");
        for (int i = 0; i < declareList.getLength(); i++) {
            ltable.clear();
            Element e = (Element) declareList.item(i);
            Element insertStatement = (Element) e.getElementsByTagName("insertStatement").item(0);
            if (insertStatement != null) {
                //obtem nome das tabelas
                NodeList elementsT = insertStatement.getElementsByTagName("t");
                String owner = "";
                String tableName = "";
                String alias = "";
                int db2prdIndex = 0;
                int beginColumnn = 0;
                int endColumns = 0;

                for (int u = 0; u < elementsT.getLength(); u++) {
                    Element eT = (Element) elementsT.item(u);
                    if (eT.getTextContent().equals("DB2PRD")) {
                        db2prdIndex = u;
                    }
                    if (eT.getTextContent().equals("(")) {
                        beginColumnn = u;
                    }
                    if (eT.getTextContent().equals(")")) {
                        endColumns = u;
                        break;
                    }
                }

                owner = ((Element) elementsT.item(db2prdIndex)).getTextContent();
                tableName = ((Element) elementsT.item(db2prdIndex + 2)).getTextContent();
                ltable.add(new TableElement(owner, tableName, alias));
                pgm.getWrkSection().addTable(owner, tableName, alias);

                // Get Collumns Names
                for (int u = beginColumnn; u < endColumns; u++) {
                    Element eT = (Element) elementsT.item(u);
                    String columnToAdd = eT.getTextContent();
                    columnToAdd = columnToAdd.replace('\n', ' ');
                    columnToAdd = columnToAdd.replace('\t', ' ');
                    columnToAdd = columnToAdd.replaceAll(" ", "");
                    pgm.getWrkSection().addColumn(columnToAdd, ltable);
                }
            }
        }
    }

    private void buildDB2ElmentsDelete(CobolProgram pgm, Element element) {
        LinkedList<TableElement> ltable = new LinkedList<>();

        //obtem LISTA DE chamadas SQL
        NodeList declareList = element.getElementsByTagName("sqlStatement");
        for (int i = 0; i < declareList.getLength(); i++) {
            ltable.clear();
            Element e = (Element) declareList.item(i);
            Element deleteStatement = (Element) e.getElementsByTagName("deleteStatement").item(0);
            if (deleteStatement != null) {
                //obtem nome das tabelas
                NodeList elementsT = deleteStatement.getElementsByTagName("t");
                String owner = "";
                String tableName = "";
                String alias = "";
                int db2prdIndex = 0;
                int beginFROM = 0;
                int beginWHERE = 0;

                for (int u = 0; u < elementsT.getLength(); u++) {
                    Element eT = (Element) elementsT.item(u);
                    if (eT.getTextContent().equals("FROM")) {
                        beginFROM = u;
                    }
                    if (eT.getTextContent().equals("WHERE")) {
                        beginWHERE = u;
                        break;
                    }
                    if (eT.getTextContent().equals("DB2PRD")) {
                        db2prdIndex = u;
                    }
                }

                owner = ((Element) elementsT.item(db2prdIndex)).getTextContent();
                tableName = ((Element) elementsT.item(db2prdIndex + 2)).getTextContent();
                ltable.add(new TableElement(owner, tableName, alias));
                pgm.getWrkSection().addTable(owner, tableName, alias);

                // Get Collumns Names
                for (int u = beginWHERE; u < elementsT.getLength(); u++) {
                    Element eT = (Element) elementsT.item(u);
                    String columnToAdd = eT.getTextContent();
                    columnToAdd = columnToAdd.replace('\n', ' ');
                    columnToAdd = columnToAdd.replace('\t', ' ');
                    columnToAdd = columnToAdd.replaceAll(" ", "");
                    pgm.getWrkSection().addColumn(columnToAdd, ltable);
                }
            }
        }
    }

    private void buildFileProcedureDivisionProgramCalls(CobolProgram pgm, Element element) {
        //obtem node da PROCEDURE DIVISION
        Element procedureDivision = (Element) element.getElementsByTagName("procedureDivision").item(0);

        //lista de comandos da procedure
        NodeList execCicsList = procedureDivision.getElementsByTagName("execCICSStatement");
        for (int i = 0; i < execCicsList.getLength(); i++) {
            Element e = (Element) execCicsList.item(i);
            Element cicsStatement = (Element) e.getElementsByTagName("cicsStatement").item(0);
            Element command = (Element) cicsStatement.getElementsByTagName("command").item(0);
            if (command != null) {
                Element commandtext = (Element) command.getElementsByTagName("t").item(0);
                if (commandtext.getTextContent().equals("LINK")) {
                    Element option = (Element) cicsStatement.getElementsByTagName("option").item(0);
                    if (option != null) {
                        Element nameElement = (Element) option.getElementsByTagName("name").item(0);
                        if (nameElement.getElementsByTagName("t").item(0).getTextContent().equals("PROGRAM")) {
                            Element value = (Element) cicsStatement.getElementsByTagName("value").item(0);
                            if (value != null) {
                                Element valueText = (Element) value.getElementsByTagName("t").item(0);
                                if (valueText.getTextContent().contains("WRK-")) {
                                    String toAdd = value.getTextContent().replace('\n', ' ');
                                    toAdd = toAdd.replace('\'', ' ');
                                    toAdd = toAdd.replaceAll(" ", "");
                                    pgm.addProgramCall(toAdd);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildProgramCalls(CobolProgram pgm, Element element) {
        LinkedList<CobolElement> lpgm = pgm.getlPgmCall();
        for (CobolElement pgmToAdd : lpgm) {
            String pgmVar = pgmToAdd.getName();
            if (pgm.getWrkSection().getElement(pgmVar) != null) {
                if (pgm.getWrkSection().getElement(pgmVar).getContent().size() > 0) {
                    for (int i = 0; i < pgm.getWrkSection().getElement(pgmVar).getContent().size(); i++) {
                        pgmToAdd.addContent(pgm.getWrkSection().getElement(pgmVar).getContent().get(i));
                    }
                }
                LinkedList<String> lpgmName = getAlphaNumericMoveVar(element, pgmVar);
                for (String pgmNametoAdd : lpgmName) {
                    pgmToAdd.addContent(pgmNametoAdd);
                }
            }
        }
    }

    private LinkedList<String> getAlphaNumericMoveVar(Element element, String var) {
        NodeList moveList = element.getElementsByTagName("moveStatement");
        LinkedList<String> lret = new LinkedList<>();
        for (int i = 0; i < moveList.getLength(); i++) {
            Element node = (Element) moveList.item(i);
            Element identifier = (Element) node.getElementsByTagName("identifier").item(0);
            Element cobolWord = (Element) identifier.getElementsByTagName("cobolWord").item(0);
            if (cobolWord != null){
                Element varName = (Element) cobolWord.getElementsByTagName("t").item(0);

                if (varName.getTextContent().equals(var)) {
                    Element literal = (Element) node.getElementsByTagName("literal").item(0);
                    if (literal != null) {
                        Element literalValue = (Element) literal.getElementsByTagName("literalValue").item(0);
                        Element alphanumericLiteral = (Element) literalValue.getElementsByTagName("alphanumericLiteral").item(0);
                        String varValue;
                        if (alphanumericLiteral != null) {
                            Element aplhaValue = (Element) alphanumericLiteral.getElementsByTagName("t").item(0);
                            varValue = aplhaValue.getTextContent();
                            varValue = varValue.replace('\n', ' ');
                            varValue = varValue.replace('\'', ' ');
                            varValue = varValue.replaceAll(" ", "");

                            lret.add(varValue);
                        }
                    }
                }
            }
        }
        return lret;
    }
}
