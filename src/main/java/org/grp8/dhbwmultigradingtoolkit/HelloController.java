package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private TextField txt_user;
    @FXML
    private PasswordField txt_password;

    @FXML
    protected void onHelloButtonClick() {
        SheetManager s = new SheetManager("C:\\Users\\lkoehler\\IdeaProjects\\dhbw-multigrading-toolkit\\parse.csv", "C:\\Users\\lkoehler\\IdeaProjects\\dhbw-multigrading-toolkit\\matrikel.xlsx");
        String[] creds = new String[2];
        {
            creds[0] = txt_user.getText();
            creds[1] = txt_password.getText();
        }
        Bot b = new Bot(creds, s);
        b.start();
    }
}