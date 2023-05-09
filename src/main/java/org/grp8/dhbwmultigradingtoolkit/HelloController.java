package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField txt_user;
    @FXML
    private TextField txt_password;

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