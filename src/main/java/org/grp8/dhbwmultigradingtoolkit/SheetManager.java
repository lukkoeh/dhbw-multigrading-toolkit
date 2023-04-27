package org.grp8.dhbwmultigradingtoolkit;

import javafx.scene.control.Alert;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Timm Dörr and Lukas Köhler
 * SheetManager is a class which aims to provide an API to handle SheetData in a simple and unified way.
 * An instance of SheetManager has two arrays representing metadata of the operation as well as the data to use.
 * It is used by calling its constructor with the respective paths of the files that shall be parsed and detects their type automatically.
 * Supported file types: XLSX, ODS, CSV
 * Supported encoding on CSV: UTF-8
 */
public class SheetManager {

    private final HashMap<String, String> meta = new HashMap<String, String>();
    private final ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>(); // example: Number (String) + all other columns as arraylist

    public SheetManager(String path) {
        try {
            String extension = FilenameUtils.getExtension(path);
            switch (extension) {
                case "csv" -> parseCSV(path);
                case "xlsx" -> parseXLSX(path);
                case "ods" -> parseODS(path);
            }
            //mergeData(pathmatrikel);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void parseCSV(String path){
        try {
            Path p = Paths.get(path);
            System.out.println(path);
            BufferedReader s = Files.newBufferedReader(p, StandardCharsets.ISO_8859_1);
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
                if (i == 8) {
                    s.readLine();
                    s.readLine();
                    line = s.readLine();
                    break;
                }
                i++;
            }
            while (line != null) {
                String[] dataarr = line.split(";");
                ArrayList<String> dataList = new ArrayList<String>();
                for (i = 0; i < dataarr.length; i++) {
                    dataList.add(dataarr[i]);
                }
                this.data.add(dataList);
                line = s.readLine();
            }
            s.close();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(this.meta.toString() + this.data.toString());
            a.showAndWait();
        } catch (FileNotFoundException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("The file was not found. Please check the path.");
            a.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void parseXLSX(String path){
        try{
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            fis.close();
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();
            int i = 1;
            itr.next();
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> c = row.cellIterator();
                String[] metas = new String[2];
                int z = 0;
                while (c.hasNext()) {
                    Cell cell = c.next();
                    switch (cell.getCellType()) {
                        case STRING -> metas[z] = cell.getStringCellValue();
                        case NUMERIC -> {
                            Double d = cell.getNumericCellValue();
                            metas[z] = Integer.toString(d.intValue());
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
            itr.next();
            itr.next();
            while (itr.hasNext()) {
                Row row = itr.next();
                ArrayList<String> dataarr = new ArrayList<String>();
                Iterator<Cell> cellitr = row.cellIterator();
                boolean isEmpty = true;
                while (cellitr.hasNext()) {
                    Cell cell = cellitr.next();
                    if (cell.getCellType() != CellType.BLANK) {
                        isEmpty = false;
                        break;
                    }
                }
                cellitr = row.cellIterator();
                if (!isEmpty) {
                    while (cellitr.hasNext()) {
                        Cell cell = cellitr.next();
                        switch (cell.getCellType()) {
                            case STRING -> dataarr.add(cell.getStringCellValue());
                            case NUMERIC -> {
                                Double d = cell.getNumericCellValue();
                                dataarr.add(Integer.toString(d.intValue()));
                            }
                        }
                    }
                    this.data.add(dataarr);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param path path to .ods file which has to be parsed.
     *             parseODS is a function that takes a path of a .ods file and parses it into the SheetManager data structure.
     * @author Timm Dörr and Lukas Köhler
     */
    public void parseODS(String path) {
        try {
            File f = new File(path);
            OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.loadDocument(f);
            OdfContentDom content = ods.getContentDom();
            System.out.println(content.getFirstChild());
            /*int rowcount = s.getSheet(0).getRowCount();
            int colcount = s.getSheet(0).getColumnCount();
            Sheet sheet = s.getSheet(0);
            for (int i = 1; i<9; i++) {
                String[] metas = new String[2];
                for (int a = 0; a<2; a++) {
                    metas[a] = sheet.getCellAt(a, i).toString();
                }
                this.meta.put(metas[0], metas[1]);
            }
            for (int i = 11; i< rowcount; i++) {
                ArrayList<String> dataarr = new ArrayList<String>();
                for (int a = 0; a < 6; a++) {
                    dataarr.add(sheet.getCellAt(a, i).toString());
                }
                this.data.add(dataarr);
            }*/
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mergeData(String path) {
        try {
            File f = new File(path);

        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("There was an error while merging the data.");
            a.showAndWait();
        }
    }
}