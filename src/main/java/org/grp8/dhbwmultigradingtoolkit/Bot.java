package org.grp8.dhbwmultigradingtoolkit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;


import java.time.Duration;
import java.util.*;


public class Bot {
    private String starturl;
    private final WebDriver botwindow;
    private String[] credentials = new String[2];
    private final HashMap<String, String> metainformation;
    private final ArrayList<ArrayList<String>> dataarray;
    public Bot(String[] credentials, SheetManager s) {
        this.metainformation = s.getMeta();
        this.dataarray = s.getData();
        this.botwindow = new ChromeDriver();
        this.credentials = credentials;
    }

    public void start() {
        try {
            this.starturl = this.metainformation.get("Abgabeelement") + "&action=grading";
            botwindow.get(starturl);
            if (Objects.equals(botwindow.getCurrentUrl(), "https://moodle.mosbach.dhbw.de/login/index.php")) {
                boolean loggedin = handleLogin();
                if (!loggedin) {
                    System.out.println("Login failed");
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

            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
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

    public void insertGrade(String grade) {
        WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_grade")));
        w.clear();
        w.sendKeys(grade);
    }

    public void insertComment(String comment) {
        try {
            WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(new By.ByCssSelector("iframe")));
            botwindow.switchTo().frame(w);
            WebElement a = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ByCssSelector("#tinymce")));
            a.click();
            a.clear();
            a.sendKeys(comment);
            String handle = botwindow.getWindowHandle();
            botwindow.switchTo().window(handle);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    public void clickSaveNext() {
        try {
            WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ByCssSelector("button[name=\"saveandshownext\"]")));
            w.click();
            Thread.sleep(200);
            WebElement q = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ByCssSelector("button[data-action=\"cancel\"]")));
            q.click();
            Thread.sleep(200);
        } catch(Exception ex) {
            System.out.println("brumm");
        }
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
