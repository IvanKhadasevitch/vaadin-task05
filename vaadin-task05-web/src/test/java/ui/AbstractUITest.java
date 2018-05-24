package ui;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class AbstractUITest {
    protected WebDriver driver;
    protected static final String BASE_URL = "http://localhost:8080";

    public AbstractUITest() {
        // Optional, if not specified, WebDriver will search your path for
        // chromedriver.exe
//        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        System.setProperty("webdriver.chrome.driver", "D:\\java-libraries\\chromedriver_win32\\chromedriver.exe");
    }

    @Before
    public void initDriver() throws InterruptedException {
        driver = new ChromeDriver();
        // Maximizes the current window if it is not already maximized
        driver.manage().window().maximize();
        // if element not found immediately - driver will wait 12 second
        // at every searching for an element
        driver.manage().timeouts().implicitlyWait(12, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws InterruptedException {
        Thread.sleep(1000); // Let the user actually see something!
        driver.quit();
    }
}
