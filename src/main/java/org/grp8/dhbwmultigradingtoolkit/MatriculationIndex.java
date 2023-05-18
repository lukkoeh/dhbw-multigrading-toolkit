package org.grp8.dhbwmultigradingtoolkit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * The MatriculationIndex creates an index of all students and their matriculation numbers by using a provided file.
 * It provides Methods to search in the index.
 */
public class MatriculationIndex {
    private final ArrayList<Student> students = new ArrayList<>();
    private static MatriculationIndex INSTANCE;
    /**
     * A class that represents an index of matriculation numbers and the respective names. Provides diverse Methods to interact with
     * the dataset.
     * @param f xlsx input file to generate student index
     */
    private MatriculationIndex(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();
            itr = this.skipRows(itr, 3);
            while (itr.hasNext()) {
                Row r = itr.next();
                Iterator<Cell> citr = r.cellIterator();
                String[] tmp = new String[3];
                int i = 0;
                while (citr.hasNext()){
                    Cell current = citr.next();
                    switch (current.getCellType()) {
                        case STRING -> tmp[i] = current.getStringCellValue().trim();
                        case NUMERIC -> {
                            double d = current.getNumericCellValue();
                            tmp[i] = Integer.toString((int) d).trim();
                        }
                        case BLANK -> {
                            continue;
                        }
                    }
                    i++;
                }
                if (tmp[0] != null && tmp[1] != null && tmp[2] != null) {
                    Student s = new Student(tmp[2], tmp[0], tmp[1]);
                    this.students.add(s);
                }
            }
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the current instance of the MatriculationIndex
     * @param f The file for matriculation index generation
     * @return the instance of the index
     */
    public static MatriculationIndex getInstance(File f){
        if(INSTANCE == null){
            INSTANCE = new MatriculationIndex(f);
        }
        return INSTANCE;
    }

    /**
     * A simple utility method to skip lines in iterators.
     * @param itr The iterator that should be skipped
     * @param x The number of iterations you want to skip
     * @return Returns the iterator given at another position
     */
    public Iterator<Row> skipRows(Iterator<Row> itr, int x) {
        for (int i = 0; i<x; i++) {
            if (itr.hasNext()) {
                itr.next();
            }
        }
        return itr;
    }

    /**
     * A function that finds student data in the index by using its matriculation number
     * @param number the matriculation number
     * @return a Student dataset
     */
    public Student findStudentByNumber(String number) {
        for (Student s : students) {
            if (Objects.equals(s.getNo(), number)) {
                return s;
            }
        }
        System.out.println("Student with number: " + number + " was not found.");
        return null;
    }
    /**
     * A utility to get all Students from this index
     * @return index of students as ArrayList of Student Objects
     */
    public ArrayList<Student> getStudents() {
        return students;
    }
}
