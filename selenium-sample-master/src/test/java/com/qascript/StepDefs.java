package com.qascript;

import java.net.URL;
import java.util.HashMap;

import com.browserstack.local.Local;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class StepDefs {
    WebDriver driver;
    public static final String username = System.getenv("BROWSERSTACK_USERNAME");
    public static final String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
    public static final String URL = "https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";

    @Given("Launch website")
    public void launchQAScript() {
        System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://www.selenium.dev/");

    }

    @Then("Verify webpage is displayed to user")
    public void qascriptPageIsDisplayedToUser() {

        String strTitle = driver.getTitle();
        Assert.assertEquals(strTitle, "SeleniumHQ Browser Automation");
        driver.quit();
    }

    @Given("Launch website in BS")
    public static void launchWebsiteInBS() throws Exception {
        
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "93");
        caps.setCapability("build", "BStack Build Xena Automation");
        caps.setCapability("browserstack.debug", "true");
        caps.setCapability("project", "Browserstack Demo");
        caps.setCapability("name", "BStack-[Java] Demo");
        Local bsLocal = new Local();
        HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
        bsLocalArgs.put("key", "sWkwdJeqRzF4qypGSvWt");
        bsLocal.start(bsLocalArgs);
        System.out.println(bsLocal.isRunning());

        WebDriver driver = new RemoteWebDriver(new URL(URL), caps);
        driver.get("http://production.xena.local/module");
        driver.manage().window().maximize();
        driver.findElement(By.id("real-login-name")).sendKeys("autotest");
        driver.findElement(By.id("real-password")).sendKeys("Zeus2017!");
        driver.findElement(By.xpath("//button")).click();
        String actualUrl = "http://production.xena.local/module/Administration";
        String expectedUrl = driver.getCurrentUrl();
        if (expectedUrl.equals(actualUrl)) {
            System.out.println("Login Successfull");
        } else {
            System.out.println("Login Failed");
        }

        // Setting the status of test as 'passed' or 'failed' based on the condition; if
        // title of the web page contains 'BrowserStack'
        WebDriverWait wait = new WebDriverWait(driver, 5);
        try {
            wait.until(ExpectedConditions.urlContains("/module/Administration"));
            markTestStatus("passed", "Yeah, Login success to 'XenaUI'!", driver);
        } catch (Exception e) {
            markTestStatus("failed", "No, you does not login to 'XenaUI'!", driver);
        }
        System.out.println(driver.getTitle());
        driver.quit();

    }
    public static void markTestStatus(String status, String reason, WebDriver driver) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \""
                + status + "\", \"reason\": \"" + reason + "\"}}");
    }
}
