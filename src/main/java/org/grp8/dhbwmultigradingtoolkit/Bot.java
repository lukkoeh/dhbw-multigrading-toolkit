package org.grp8.dhbwmultigradingtoolkit;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;


import java.time.Duration;
import java.util.*;

/**
 * The Bot class handles all selenium-operations. It is initializied with a SheetManager as well as credentials for moodle
 * It gets its data from said SheetManager and the credentials, all other operations are handled by itself.
 */
public class Bot {
    private String starturl;
    private WebDriver botwindow;
    private String[] credentials;
    private final HashMap<String, String> metainformation;
    private final ArrayList<ArrayList<String>> dataarray;

    /**
     * This function initializes the bot using the necessary data provides by its parameters.
     * @param credentials an array of strings representing the moodle-credentials used to log into moodle
     * @param s requires an instance of SheetManager, which was already provided with necessary files.
     */
    public Bot(String[] credentials, SheetManager s) {
        this.metainformation = s.getMeta();
        this.dataarray = s.getData();
        ChromeOptions c = new ChromeOptions();
        c.addArguments("headless");
        this.botwindow = new ChromeDriver(c);
        this.credentials = credentials;
    }

    /**
     * This function starts the bot, it uses the botwindow initialized in the Bot-Class constructor and tries to fill out
     * the data of the SheetManager class into Moodle.
     * @return returns either a boolean if the Bot failed OR recusively calls itself to repeat execution
     */
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

    /**
     * The stop() Function closes the botwindow to also quit the chromedriver process.
     */
    public void stop(){
        botwindow.quit();
    }

    /**
     * A function to log into moodle, gets repeated every time the login fails (see return)
     * @return returns a boolean to determine if a login attempt was successful.
     */
    private boolean handleLogin() {
        WebElement usernamefield = botwindow.findElement(new By.ById("username"));
        WebElement passwordfield = botwindow.findElement(new By.ById("password"));
        WebElement loginbutton = botwindow.findElement(new By.ById("loginbtn"));
        usernamefield.sendKeys(this.credentials[0]);
        passwordfield.sendKeys(this.credentials[1]);
        loginbutton.click();
        return Objects.equals(botwindow.getCurrentUrl(), this.starturl);
    }

    /**
     * A function to convert a moodle-provided name string including the course into a usable firstname, lastname construction.
     * Neccessary to use it with SheetManager's data.
     * @param unformattedName The unformatted Moodle string
     * @return the converted String ("firstname lastname").
     */
    private String getFormattedStudentName(String unformattedName) {
        String[] name = unformattedName.split(" ");
        ArrayList<String> names = new ArrayList<>(Arrays.asList(name));
        names.remove(0);
        return String.join(" ", names);
    }

    /**
     * A function to find the respective entry in the dataarray for a specific name construction.
     * Throws an exception if a row was not found.
     * @param name takes a converted name from getFormattedStudentName() in "firstname lastname" structure.
     * @return returns an Arraylist of Strings containing the data for a specific name
     */
    public ArrayList<String> getRowByName(String name) {
        for (ArrayList<String> strings : this.dataarray) {
            String tmp = String.join(" ", strings.get(1), strings.get(2));
            if (Objects.equals(name, tmp)) {
                return strings;
            }
        }
        throw new NoSuchElementException("Dataset was not found: " + name);
    }

    /**
     * Using the fastgrading mechanism of moodle requires activation. We make sure that it is enabled. Also, we set the limiter
     * to "all" to show ALL Students.
     */
    public void prepareGrading() {
        WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_quickgrading")));
        if (w.getAttribute("checked") == null) {
            w.click();
        }
        WebElement q = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_perpage")));
        Select se = new Select(q);
        se.selectByVisibleText("Alle");
    }
}
