package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        SheetManager s = new SheetManager("C:\\Users\\lkoehler\\IdeaProjects\\dhbw-multigrading-toolkit\\parse.xlsx", "C:\\Users\\lkoehler\\IdeaProjects\\dhbw-multigrading-toolkit\\matrikel.xlsx");
        String[] creds = new String[2];
        creds[0] = "luk.koehler.22@lehre.mosbach.dhbw.de";
        creds[1] = "iPiQ1lm712H9t";
        Bot b = new Bot(creds, s);
        b.start();
    }
}