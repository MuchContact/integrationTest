package appium.tutorial.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.List;

public class DriverHelper {
    private AndroidDriver driver;
    private WebDriverWait driverWait;

    public DriverHelper(AndroidDriver driver, URL driverServerAddress) {
        this.driver = driver;
        int timeoutInSeconds = 5;
        // must wait at least 60 seconds for running on Sauce.
        // waiting for 30 seconds works locally however it fails on Sauce.
        driverWait = new WebDriverWait(driver, timeoutInSeconds);
    }

    public WebElement element(By locator) {
        return driver.findElement(locator);
    }

    public List<WebElement> elements(By locator) {
        return driver.findElements(locator);
    }

    public void clear(String text) {
        sendKeyEvent(123);
        int maxChars = text.length();

        for (int i = 0; i < maxChars; i++) {
            sendKeyEvent(67);
        }

    }

    public void sendKeyEvent(int eventCode) {
        driver.sendKeyEvent(eventCode);
    }

    public WebElement wait(By locator) {
        return driverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void onDestroy() {
        if (driver != null)
            driver.quit();
    }

    public void startApp(String packageName, String activityName, String waitPkg, String waitActivity) {
        driver.startActivity(packageName, activityName, waitPkg, waitActivity);
    }

    public void startApp(String packageName, String activityName) {
        driver.startActivity(packageName, activityName);
    }
}
