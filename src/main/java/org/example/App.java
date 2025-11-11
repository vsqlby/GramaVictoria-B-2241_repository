package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {

    static ATUTestRecorder recorder; // Recorder object

    public static void main(String[] args) {
        WebDriver driver = null;

        try {
            // ✅ Ensure test-videos folder exists
            File videoDir = new File("test-videos");
            if (!videoDir.exists()) videoDir.mkdir();

            // ✅ Generate unique timestamp for filenames
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // ✅ Only record locally (GitHub Actions has no screen)
            if (System.getenv("GITHUB_ACTIONS") == null) {
                try {
                    recorder = new ATUTestRecorder("test-videos", "FormTest_" + timestamp, false);
                    recorder.start();
                    System.out.println("🎥 Video recording started...");
                } catch (ATUTestRecorderException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Running on GitHub Actions — video recording skipped.");
            }

            // ✅ Setup WebDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless=new"); // Headless mode for CI
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();

            driver.get("https://demoqa.com/automation-practice-form");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelectorAll('iframe, #fixedban, .Advertisement').forEach(el => el.style.display='none');");

            // ✅ Fill form
            driver.findElement(By.id("firstName")).sendKeys("Grama");
            driver.findElement(By.id("lastName")).sendKeys("Victoria");
            driver.findElement(By.id("userEmail")).sendKeys("diva.queen@example.com");
            driver.findElement(By.xpath("//label[text()='Female']")).click();
            driver.findElement(By.id("userNumber")).sendKeys("1234567890");

            WebElement dateInput = Utils.explicitWait(driver,
                    org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.id("dateOfBirthInput")), 10);
            Utils.scrollToElementStatic(driver, dateInput);
            js.executeScript("arguments[0].click();", dateInput);

            WebElement monthSelect = Utils.explicitWait(driver,
                    org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.className("react-datepicker__month-select")), 10);
            monthSelect.sendKeys("May");

            WebElement yearSelect = Utils.explicitWait(driver,
                    org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.className("react-datepicker__year-select")), 10);
            yearSelect.sendKeys("2000");

            WebElement daySelect = Utils.explicitWait(driver,
                    org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'react-datepicker__day') and text()='15']")), 10);
            daySelect.click();

            WebElement subjectInput = driver.findElement(By.id("subjectsInput"));
            subjectInput.sendKeys("Maths");
            subjectInput.sendKeys(Keys.ENTER);

            driver.findElement(By.xpath("//label[text()='Sports']")).click();
            driver.findElement(By.xpath("//label[text()='Reading']")).click();

            WebElement upload = driver.findElement(By.id("uploadPicture"));
            upload.sendKeys("/Users/anastasiagrama/Downloads/12499.jpg");

            driver.findElement(By.id("currentAddress")).sendKeys("123 Main Street, New York");

            WebElement state = driver.findElement(By.id("react-select-3-input"));
            state.sendKeys("NCR");
            state.sendKeys(Keys.ENTER);
            WebElement city = driver.findElement(By.id("react-select-4-input"));
            city.sendKeys("Delhi");
            city.sendKeys(Keys.ENTER);

            WebElement submitButton = driver.findElement(By.id("submit"));
            Utils.scrollToElementStatic(driver, submitButton);
            js.executeScript("arguments[0].click();", submitButton);

            Thread.sleep(3000);

            System.out.println("✅ Test completed successfully!");

        } catch (Exception e) {
            e.printStackTrace();

            // ✅ Capture screenshot on failure
            try {
                if (e.getMessage() != null) System.out.println("❌ Error: " + e.getMessage());
                if (Files.notExists(new File("test-videos").toPath()))
                    new File("test-videos").mkdirs();

                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File destFile = new File("test-videos/error_" + timestamp + ".png");
                Files.copy(screenshot.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("📸 Screenshot saved: " + destFile.getAbsolutePath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        } finally {
            // ✅ Stop recorder safely
            try {
                if (recorder != null) recorder.stop();
                System.out.println("🎬 Recording stopped.");
            } catch (ATUTestRecorderException e) {
                e.printStackTrace();
            }

            // ✅ Quit driver safely
            if (driver != null) {
                driver.quit();
                System.out.println("🧹 Browser closed.");
            }
        }
    }
}
