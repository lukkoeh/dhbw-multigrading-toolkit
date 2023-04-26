package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        SheetManager s = new SheetManager("C:\\Users\\lkoehler\\IdeaProjects\\dhbw-multigrading-toolkit\\parse.csv", "...");
    }
}