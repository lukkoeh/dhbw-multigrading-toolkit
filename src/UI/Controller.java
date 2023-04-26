package ??;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

//logic
public class Controller implements Initializable
 {
    @FXML
    private Label label;

    private FXMLBspModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new FXMLBspModel();
    }

    public void manageButton() {
        label.setText(model.getHello());
    }
}
