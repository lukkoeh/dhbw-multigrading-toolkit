package org.grp8.dhbwmultigradingtoolkit;

import javafx.scene.control.Alert;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class SheetManager {

    private HashMap<String, String> meta = new HashMap<String, String>();
    private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>(); // example: Number (String) + all other columns as arraylist
    public SheetManager(String path, String pathmatrikel) {
        try {
            String extension = FilenameUtils.getExtension(path);
            switch (extension){
                case "csv":
                    parseCSV(path);
                    break;
                case "xlsx":
                    parseXLSX(path);
                    break;
                case "ods":
                    parseODS(path);
                    break;
            }
            //mergeData(pathmatrikel);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void parseCSV(String path){
        try {
            Path p = Paths.get(path);
            File f = new File(path);
            System.out.println(path);
            BufferedReader s = Files.newBufferedReader(p, StandardCharsets.ISO_8859_1);
            int i = 0;
            String line = s.readLine();
            while (line!=null) {
                if (i == 0) {System.out.println("skippy"); i++; continue;}
                String[] dataarr = line.split(";");
                this.meta.put(dataarr[0], dataarr[1]);
                line = s.readLine();
                if (i == 8) {s.readLine(); s.readLine(); line = s.readLine(); break;}
                i++;
            }
            while (line!= null) {
                String[] dataarr = line.split(";");
                ArrayList<String> dataList = new ArrayList<String>();
                for (i = 0; i<dataarr.length; i++) {
                    dataList.add(dataarr[i]);
                }
                this.data.add(dataList);
                line = s.readLine();
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(this.meta.toString()+ this.data.toString());
            a.showAndWait();
        } catch (FileNotFoundException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("The file was not found. Please check the path.");
            a.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String parseXLSX(String path){
        return "";
    }
    public String parseODS(String path){
        return "";
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
