package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;
import java.io.File;

public class App {

    static ATUTestRecorder recorder; // Recorder object

    public static void main(String[] args) {
        WebDriver driver = null;

        try {
            // Create folder for videos if not exists
            File videoDir = new File("test-videos");
            if (!videoDir.exists()) videoDir.mkdir();

            // Start recording
            recorder = new ATUTestRecorder("test-videos", "FormTest_Video", false);
            recorder.start();

            // Setup driver
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();

            driver.get("https://demoqa.com/automation-practice-form");

            // Hide ads and banners
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "document.querySelectorAll('iframe, #fixedban, .Advertisement').forEach(el => el.style.display='none');"
            );

            // Fill in the form
            driver.findElement(By.id("firstName")).sendKeys("Grama");
            driver.findElement(By.id("lastName")).sendKeys("Victoria");
            driver.findElement(By.id("userEmail")).sendKeys("diva.queen@example.com");
            driver.findElement(By.xpath("//label[text()='Female']")).click();
            driver.findElement(By.id("userNumber")).sendKeys("1234567890");

            // Date of Birth
            driver.findElement(By.id("dateOfBirthInput")).click();
            driver.findElement(By.className("react-datepicker__month-select")).sendKeys("May");
            driver.findElement(By.className("react-datepicker__year-select")).sendKeys("2000");
            driver.findElement(By.xpath("//div[contains(@class, 'react-datepicker__day') and text()='15']")).click();

            // Subjects
            WebElement subjectInput = driver.findElement(By.id("subjectsInput"));
            subjectInput.sendKeys("Maths");
            subjectInput.sendKeys(Keys.ENTER);

            // Hobbies
            driver.findElement(By.xpath("//label[text()='Sports']")).click();
            driver.findElement(By.xpath("//label[text()='Reading']")).click();

            // Upload picture
            WebElement upload = driver.findElement(By.id("uploadPicture"));
            upload.sendKeys("/Users/anastasiagrama/Downloads/12499.jpg");

            // Current address
            driver.findElement(By.id("currentAddress")).sendKeys("123 Main Street, New York");

            // State and City
            WebElement state = driver.findElement(By.id("react-select-3-input"));
            state.sendKeys("NCR");
            state.sendKeys(Keys.ENTER);

            WebElement city = driver.findElement(By.id("react-select-4-input"));
            city.sendKeys("Delhi");
            city.sendKeys(Keys.ENTER);

            // Scroll and submit
            WebElement submitButton = driver.findElement(By.id("submit"));
            js.executeScript("arguments[0].scrollIntoView(true);", submitButton);
            submitButton.click();

            // Wait a bit to view result
            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Stop video recording
                if (recorder != null)
                    recorder.stop();
            } catch (ATUTestRecorderException e) {
                e.printStackTrace();
            }

            // Quit driver
            if (driver != null)
                driver.quit();
        }
    }
}

// ssh-ed25519