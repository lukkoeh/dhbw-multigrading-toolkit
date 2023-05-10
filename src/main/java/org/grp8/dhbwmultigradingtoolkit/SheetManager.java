package org.grp8.dhbwmultigradingtoolkit;

import javafx.scene.control.Alert;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.w3c.dom.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Timm Dörr and Lukas Köhler
 * SheetManager is a class which aims to provide an API to handle SheetData in a simple and unified way.
 * An instance of SheetManager has two arrays representing metadata (as HashMap) of the operation (as ArrayList) as well as the data to use.
 * It is used by calling its constructor with the respective paths of the files that shall be parsed and detects their type automatically.
 * Supported file types: XLSX, ODS, CSV
 * Supported encoding on CSV: UTF-8
 */
public class SheetManager {

    private final HashMap<String, String> meta = new HashMap<>();
    private final ArrayList<ArrayList<String>> data = new ArrayList<>(); // example: Number (String) + all other columns as arraylist

    public SheetManager(File datatable, File matrikel) {
        String path = datatable.getPath();
        try {
            String extension = FilenameUtils.getExtension(path);
            switch (extension) {
                case "csv" -> parseCSV(path);
                case "xlsx" -> parseXLSX(path);
                case "ods" -> parseODS(path);
            }
            mergeData(pathmatrikel);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SheetManager(File table) {
        String path = table.getPath();
        try {
            String extension = FilenameUtils.getExtension(path);
            switch (extension) {
                case "csv" -> parseCSV(path);
                case "xlsx" -> parseXLSX(path);
                case "ods" -> parseODS(path);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param path path to .csv file which has to be parsed.
     *             parseODS is a function that takes a path of a .csv file and parses it into the SheetManager data structure. The file needs to be in the UTF-8.
     * @author Timm Dörr and Lukas Köhler
     */
    private void parseCSV(String path) {
        try {
            Path p = Paths.get(path);
            BufferedReader s = Files.newBufferedReader(p, StandardCharsets.UTF_8);
            int i = 0;
            String line = s.readLine();
            while (line != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String[] dataarr = line.split(";");
                this.meta.put(dataarr[0], dataarr[1]);
                line = s.readLine();
                if (i == 9) {
                    s.readLine();
                    line = s.readLine();
                    break;
                }
                i++;
            }
            while (line != null) {
                String[] dataarr = line.split(";");
                ArrayList<String> dataList = new ArrayList<>();
                for (i = 0; i < 6; i++) {
                    if(i >= dataarr.length ){
                        dataList.add("");
                    }else {
                        dataList.add(dataarr[i]);
                    }
                }
                this.data.add(dataList);
                line = s.readLine();
            }
            s.close();
        } catch (FileNotFoundException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("The file was not found. Please check the path.");
            a.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path path to .ods file which has to be parsed.
     *             parseXLSX is a function that takes a path of a .xlsx file and parses it into the SheetManager data structure.
     * @author Timm Dörr and Lukas Köhler
     */
    private void parseXLSX(String path) {
        try {
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            fis.close();
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int i = 1;
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> c = row.cellIterator();
                String[] metas = new String[2];
                int z = 0;
                while (c.hasNext()) {
                    Cell cell = c.next();
                    switch (cell.getCellType()) {
                        case STRING -> metas[z] = cell.getStringCellValue();
                        case NUMERIC -> {
                            double d = cell.getNumericCellValue();
                            metas[z] = Integer.toString((int) d);
                        }
                    }
                    z++;
                }
                this.meta.put(metas[0], metas[1]);
                if (i == 8) {
                    break;
                }
                i++;
            }
            rowIterator.next();
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ArrayList<String> dataArr = new ArrayList<>();
                Iterator<Cell> cellitr = row.cellIterator();
                boolean isEmpty = true;
                while (cellitr.hasNext()) {
                    Cell cell = cellitr.next();
                    if (cell.getCellType() != CellType.BLANK) {
                        isEmpty = false;
                        break;
                    }
                }
                if (!isEmpty) {
                    for(int cellIndex = 0; cellIndex < 6 /*TODO: Falls notwendig: Größe einer Zeile als globale Variable. oder dataArr als echten Array*/; cellIndex++){
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if(cell != null) {
                            switch (cell.getCellType()) {
                                case STRING -> dataArr.add(cell.getStringCellValue());
                                case NUMERIC -> {
                                    double d = cell.getNumericCellValue();
                                    dataArr.add(Integer.toString((int) d));
                                }
                            }
                        }else{
                            dataArr.add("");
                        }
                    }
                    this.data.add(dataArr);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(this.meta.toString() + this.data.toString());
        a.showAndWait();
    }

    /**
     * @param path path to .ods file which has to be parsed.
     *             parseODS is a function that takes a path of a .ods file and parses it into the SheetManager data structure.
     * @author Timm Dörr and Lukas Köhler
     */
    private void parseODS(String path) {
        try {
            File f = new File(path);
            OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.loadDocument(f);
            OdfContentDom content = ods.getContentDom();
            NodeList rowList = content.getElementsByTagName("table:table-row");
            for (int i = 1; i < 9; i++) { //fill meta
                NodeList cellList = rowList.item(i).getChildNodes();
                this.meta.put(cellList.item(0).getFirstChild().getTextContent(), cellList.item(1).getFirstChild().getTextContent());
            }
            for (int i = 11; i < rowList.getLength(); i++) {
                NodeList cellList = rowList.item(i).getChildNodes();

                // Leere Zellen am Ende der Zeilen löschen
                if(cellList.getLength() > 6){
                    for(int emptyNodeIndex = 0; emptyNodeIndex <= cellList.getLength()-6; emptyNodeIndex++){
                        cellList.item(6-emptyNodeIndex).getParentNode().removeChild(cellList.item(6-emptyNodeIndex));
                    }
                }
                ArrayList<String> dataArr = new ArrayList<>();
                boolean isEmpty = true;
                for (int a = 0; a < cellList.getLength(); a++) {
                    String tester = "";
                    Node currentItem = cellList.item(a);
                    if (currentItem.getFirstChild() != null) {
                        isEmpty = false;
                        tester = cellList.item(a).getFirstChild().getTextContent();
                    }

                    dataArr.add(tester);
                }

                // Falls Vor- und Nachname fehlen
                if (!isEmpty) {
                    if(dataArr.size() < 6 && dataArr.get(1).equals("")){
                        dataArr.add(1, "");
                    }
                    this.data.add(dataArr);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(this.meta.toString() + this.data.toString());
        a.showAndWait();
    }

    /**
     * The mergeData function joins the given table with the existing data on the SheetManager object, thus creating a complete
     * Array.
     * @param path path to the table to generate index
     */
    private void mergeData(File path) {
        try{
            MatriculationIndex index = new MatriculationIndex(path);
            for (ArrayList<String> tmp : this.data) {
                if (!Objects.equals(tmp.get(0), "")) {
                    Student s = index.findStudentByNumber(tmp.get(0));
                    tmp.set(1, s.getFirstname());
                    tmp.set(2, s.getLastname());
                }
            }
        } catch (Exception ex){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Es ist ein Fehler beim Verarbeiten der Matrikelnummern aufgetreten. Bitte überprüfen Sie die Matrikeltabelle.");
            a.setHeight(400);
            a.showAndWait();
            ex.printStackTrace();
        }

    }

    public HashMap<String, String> getMeta() {
        System.out.println(this.meta);
        return meta;
    }

    public ArrayList<ArrayList<String>> getData() {
        System.out.println(this.data);
        return data;
    }

}
