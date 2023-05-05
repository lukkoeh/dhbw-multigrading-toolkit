package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        SheetManager s = new SheetManager("C:\\Users\\derBoss\\Documents\\Sync\\dhbw\\Projektmanagement\\ProjektSemester2\\MoodleNotenTool\\dhbw-multigrading-toolkit\\parse.csv", "C:\\Users\\derBoss\\Documents\\Sync\\dhbw\\Projektmanagement\\ProjektSemester2\\MoodleNotenTool\\dhbw-multigrading-toolkit\\matrikel.xlsx");
        String[] creds = new String[2];
        {
            creds[0] = "tim.doerr.22@lehre.mosbach.dhbw.de";
            creds[1] = "Neoeffects!22";
        }
        Bot b = new Bot(creds, s);
        b.start();
    }
}