package org.grp8.dhbwmultigradingtoolkit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.*;


public class Bot {
    private String starturl;
    private WebDriver botwindow;
    private String[] credentials = new String[2];
    private HashMap<String, String> metainformation;
    private ArrayList<ArrayList<String>> dataarray;
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

    private String getCurrentName() {
        WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ByCssSelector("div[data-region = \"user-info\"] > h4 > a")));
        String[] lines = w.getText().split("\\r?\\n");
        String[] name = lines[0].split(" ");
        ArrayList<String> names = new ArrayList<>(Arrays.asList(name));
        names.remove(0);
        return String.join(" ", names);
    }
    public ArrayList<String> getRowByName(String name) {
        for (int i = 0; i<this.dataarray.size(); i++) {
            String tmp = String.join(" ", this.dataarray.get(i).get(1), this.dataarray.get(i).get(2));
            if (Objects.equals(name, tmp)) {
                return this.dataarray.get(i);
            }
        }
        throw new NoSuchElementException("Dataset was not found");
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

    public void clickSaveNext() throws InterruptedException {
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

    public void prepareGrading() throws InterruptedException {
        WebElement w = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_quickgrading")));;
        if (w.getAttribute("checked") == null) {
            w.click();
        }
        WebElement q = new WebDriverWait(botwindow, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(new By.ById("id_perpage")));
        Select se = new Select(q);
        se.selectByVisibleText("Alle");
        List<WebElement> nodelist = botwindow.findElements(new By.ByClassName("unselectedrow"));
        for (int i = 0; i< nodelist.size(); i++) {
            String name = nodelist.get(i).findElement(new By.ByClassName("c2")).getText();
            System.out.println(name);
        }
    }
}
