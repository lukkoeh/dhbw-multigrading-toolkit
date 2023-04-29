package org.grp8.dhbwmultigradingtoolkit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


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
            this.starturl = this.metainformation.get("Moodleraumname: ");
            botwindow.get(starturl);
            if (Objects.equals(botwindow.getCurrentUrl(), "https://moodle.mosbach.dhbw.de/login/index.php")) {
                boolean loggedin = handleLogin();
                if (loggedin == false) {
                    System.out.println("Login failed");
                } else {
                    System.out.println("Login successful");
                }
            }
            botwindow.close();
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
}
