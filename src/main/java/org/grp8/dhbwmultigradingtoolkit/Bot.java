package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;


import java.time.Duration;
import java.util.*;


public class Bot {
    private String starturl;
    private WebDriver botwindow;
    private String[] credentials = new String[2];
    private final HashMap<String, String> metainformation;
    private final ArrayList<ArrayList<String>> dataarray;
    public Bot(String[] credentials, SheetManager s) {
        this.metainformation = s.getMeta();
        this.dataarray = s.getData();
        ChromeOptions c = new ChromeOptions();
        c.addArguments("headless");
        this.botwindow = new ChromeDriver(c);
        this.credentials = credentials;
    }

    public boolean start() {
        try {
            this.starturl = this.metainformation.get("Abgabeelement") + "&action=grading";
            botwindow.get(starturl);
            if (Objects.equals(botwindow.getCurrentUrl(), "https://moodle.mosbach.dhbw.de/login/index.php")) {
                boolean loggedin = handleLogin();
                if (!loggedin) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Beim Einloggen in Moodle ist ein Fehler aufgetreten. Bitte geben Sie ihre Zugangsdaten erneut ein.");
                    a.showAndWait();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("relogin.fxml"));
                    loader.load();
                    Controller c = loader.getController();
                    botwindow.quit();
                    c.openLoginPage();
                    ChromeOptions opt = new ChromeOptions();
                    opt.addArguments("headless");
                    botwindow = new ChromeDriver(opt);
                    return start();
                } else {
                    System.out.println("Login successful");
                }
            }
            if (Objects.equals(botwindow.getCurrentUrl(), this.starturl)) {
                prepareGrading();
                // get data (DOM elements, Sheet data) of all students in moodle table
                List<WebElement> nodelist = botwindow.findElements(new By.ByClassName("unselectedrow"));
                for (WebElement currentTableRow : nodelist) {
                    // Zeile im Sheet (CSV, XLSX, ODS) basierend auf Namen in der Tabelle (Moodle) holen
                    String rawName = currentTableRow.findElement(new By.ByClassName("c2")).getText();
                    String formattedName = getFormattedStudentName(rawName);
                    ArrayList<String> row = getRowByName(formattedName);

                    // Bewertung (Prozentpunkte) eintragen
                    WebElement gradeInputCell = currentTableRow.findElement(new By.ByClassName("c4"));
                    WebElement gradeInputField = gradeInputCell.findElement(By.xpath(".//input"));

                    gradeInputField.clear();
                    // TODO: Evtl. Prozentpunkte errechnen, aktuell wird die Prozentpunkte-Spalte aus der Tabellenvorlage verwendet
                    gradeInputField.sendKeys(row.get(4));


                    // Feedback eintragen
                    WebElement feedbackInputCell = currentTableRow.findElement(new By.ByClassName("c9"));
                    WebElement feedbackInputField = feedbackInputCell.findElement(By.xpath(".//textarea"));
                    feedbackInputField.clear();
                    feedbackInputField.sendKeys("<p>" + row.get(5) + "</p>");

                }
                // Änderungen speichern
                WebElement btnSaveGrades = botwindow.findElement(new By.ById("id_savequickgrades"));
                btnSaveGrades.click();
                WebElement successMessage = botwindow.findElement(new By.ByClassName("alert-success"));
                if( successMessage != null){
                    System.out.println("found element");
                    return true;
                }

            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("didn't find element");
        return false;
    }

    public void stop(){
        botwindow.quit();
    }
    private boolean handleLogin() {
        WebElement usernamefield = botwindow.findElement(new By.ById("username"));
        WebElement passwordfield = botwindow.findElement(new By.ById("password"));
        WebElement loginbutton = botwindow.findElement(new By.ById("loginbtn"));
        usernamefield.sendKeys(this.credentials[0]);
        passwordfield.sendKeys(this.credentials[1]);
        loginbutton.click();
        return Objects.equals(botwindow.getCurrentUrl(), this.starturl);
    }

    private String getFormattedStudentName(String unformattedName) {
        String[] name = unformattedName.split(" ");
        ArrayList<String> names = new ArrayList<>(Arrays.asList(name));
        names.remove(0);
        return String.join(" ", names);
    }
    public ArrayList<String> getRowByName(String name) {
        for (ArrayList<String> strings : this.dataarray) {
            String tmp = String.join(" ", strings.get(1), strings.get(2));
            if (Objects.equals(name, tmp)) {
                return strings;
            }
        }
        throw new NoSuchElementException("Dataset was not found: " + name);
    }

    public void prepareGrading() {
        WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_quickgrading")));
        if (w.getAttribute("checked") == null) {
            w.click();
        }
        WebElement q = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_perpage")));
        Select se = new Select(q);
        se.selectByVisibleText("Alle");
        // TODO: evtl. Teilnehmer benachrichtigen an
    }
}
