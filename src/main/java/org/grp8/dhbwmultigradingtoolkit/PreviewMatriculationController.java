package org.grp8.dhbwmultigradingtoolkit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class PreviewMatriculationController implements Initializable {
    @FXML
    private TableView<Student> tablepreviewmatrikel;
    @FXML
    private TableColumn<Student, String> colno;
    @FXML
    private TableColumn<Student, String> colfirstname;
    @FXML
    private TableColumn<Student, String> collastname;

    public static ObservableList<Student> odata = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colno.setCellValueFactory(new PropertyValueFactory<Student, String>("no"));
        colfirstname.setCellValueFactory(new PropertyValueFactory<Student, String>("firstname"));
        collastname.setCellValueFactory(new PropertyValueFactory<Student, String>("lastname"));
        tablepreviewmatrikel.setItems(odata);
    }
}
