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

/**
 * A custom controller to handle the preview-window of Matriculation
 */
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

    /**
     * A function to create cellFactories to populate the TableView.
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colno.setCellValueFactory(new PropertyValueFactory<Student, String>("no"));
        colfirstname.setCellValueFactory(new PropertyValueFactory<Student, String>("firstname"));
        collastname.setCellValueFactory(new PropertyValueFactory<Student, String>("lastname"));
        tablepreviewmatrikel.setItems(odata);
    }
}
