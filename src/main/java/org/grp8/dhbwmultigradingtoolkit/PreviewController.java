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

public class PreviewController implements Initializable {
    @FXML
    private TableView<PreviewGrade> tablepreviewdata;

    @FXML
    private TableColumn<PreviewGrade, String> colno;

    @FXML
    private TableColumn<PreviewGrade, String> colfirstname;
    @FXML
    private TableColumn<PreviewGrade, String> collastname;
    @FXML
    private TableColumn<PreviewGrade, String> colpoints;
    @FXML
    private TableColumn<PreviewGrade, String> colpercent;
    @FXML
    private TableColumn<PreviewGrade, String> colfeedback;

    public static ObservableList<PreviewGrade> odata = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colno.setCellValueFactory(new PropertyValueFactory<PreviewGrade, String>("matriculation"));
        colfirstname.setCellValueFactory(new PropertyValueFactory<PreviewGrade, String>("firstname"));
        collastname.setCellValueFactory(new PropertyValueFactory<PreviewGrade, String>("lastname"));
        colpoints.setCellValueFactory(new PropertyValueFactory<PreviewGrade, String>("points"));
        colpercent.setCellValueFactory(new PropertyValueFactory<PreviewGrade, String>("percent"));
        colfeedback.setCellValueFactory(new PropertyValueFactory<PreviewGrade, String>("feedback"));
        tablepreviewdata.setItems(odata);
    }
}
