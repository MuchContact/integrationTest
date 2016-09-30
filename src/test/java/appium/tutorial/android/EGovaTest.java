package appium.tutorial.android;

import appium.tutorial.android.util.AppiumTest;
import io.appium.java_client.android.AndroidDriver;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static appium.tutorial.android.util.Helpers.*;
import static org.hamcrest.CoreMatchers.is;

public class EGovaTest extends AppiumTest {

    private DriverHelper vpnDriverHelper;
    private DriverHelper eGovaDriverHelper;

    @Override
    @Before
    public void setUp() throws Exception {
        initializeEGovaDriverHelper();
    }

    private void initializeVpnDriverHelper() throws MalformedURLException {
        DesiredCapabilities capabilities = getDesiredCapabilities();
        String userDir = System.getProperty("user.dir");
        String localApp = "vpn-release.apk";
        String appPath = Paths.get(userDir, localApp).toAbsolutePath().toString();
        capabilities.setCapability("app", appPath);
        URL serverAddress = new URL(String.format("http://127.0.0.1:%s/wd/hub", System.getenv("PORT") == null ? "4725" : System.getenv("PORT")));
        AndroidDriver driver = new AndroidDriver(serverAddress, capabilities);
        vpnDriverHelper = new DriverHelper(driver, serverAddress);
    }

    private DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium-version", "1.1.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android");
        capabilities.setCapability("platformVersion", "4.3");
        capabilities.setCapability("unicodeKeyboard", "True");
        capabilities.setCapability("resetKeyboard", "True");
        return capabilities;
    }

    private void initializeEGovaDriverHelper() throws MalformedURLException {
        DesiredCapabilities capabilities = getDesiredCapabilities();
//        String userDir = System.getProperty("user.dir");
        String path = getClass().getResource("/").getPath().toString();
        String basePath = path.substring(0, path.indexOf("integrationTest", 0));
        String userDir = String.format("%seGovaMobileMain/build/outputs/apk", ba);
        String localApp = "eGovaMobileMain-release.apk";
        String appPath = Paths.get(userDir, localApp).toAbsolutePath().toString();
        capabilities.setCapability("app", appPath);
        capabilities.setCapability("appPackage", "cn.com.egova.cqep");
        capabilities.setCapability("appActivity", "cn.com.egova.egovamobile.EgovaActivity");
        capabilities.setCapability("waitappActivity", "cn.com.egova.egovamobile.EgovaActivity");
        URL serverAddress = new URL(String.format("http://127.0.0.1:%s/wd/hub", System.getenv("PORT") == null ? "4723" : System.getenv("PORT")));
        AndroidDriver driver = new AndroidDriver(serverAddress, capabilities);
        eGovaDriverHelper = new DriverHelper(driver, serverAddress);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        if (vpnDriverHelper != null)
            vpnDriverHelper.onDestroy();
        if (eGovaDriverHelper != null)
            eGovaDriverHelper.onDestroy();
    }

    @Ignore
    public void shouldLoginAndLogout() throws Exception {
        String pkg = "com.sangfor.vpn";
        eGovaDriverHelper.startApp(pkg, "com.sangfor.vpntest.MainActivity");
        WebElement submit = eGovaDriverHelper.element(By.id(String.format("%s:id/btn_login", pkg)));
        submit.click();

        eGovaDriverHelper.wait(for_find("启动成功"));

        String egovaPkg = "cn.com.egova.cqep";
        String activityName = "cn.com.egova.egovamobile.login.LoginActivity";
        eGovaDriverHelper.startApp(egovaPkg, activityName);
        eGovaDriverHelper.wait(By.id(String.format("%s:id/login_btnSubmit", egovaPkg)));

        WebElement login = eGovaDriverHelper.element(By.id(String.format("%s:id/login_btnSubmit", egovaPkg)));
        login.click();

        By quit = By.id(String.format("%s:id/button2", "android"));
        eGovaDriverHelper.wait(quit);
        WebElement appLogout = eGovaDriverHelper.element(quit);
        appLogout.click();

        WebElement vpnLogout = eGovaDriverHelper.element(By.id(String.format("%s:id/btn_logout", pkg)));
        vpnLogout.click();

        eGovaDriverHelper.wait(for_find("退出VPN"));
    }

    @Test
    public void should_login_for_user_one() throws Exception {
        should_login_for_user("许可");
    }

    @Test
    public void should_login_for_user_two() throws Exception {
        should_login_for_user("史大平");
    }

    private void should_login_for_user(String targetUsername) throws Exception {
        String egovaPkg = "cn.com.egova.cqep";
        inputUsernameAndLogin(targetUsername, egovaPkg);

        By homeTabs = By.id(String.format("%s:id/home_tab_item_icon", egovaPkg));
        eGovaDriverHelper.wait(homeTabs);
        List<WebElement> mainMenus = eGovaDriverHelper.elements(homeTabs);
        Assert.assertThat(mainMenus.size(), is(4));
        mainMenus.get(1).click();
        By nativeApps = By.id(String.format("%s:id/native_tab", egovaPkg));
        WebElement nativeBtn = eGovaDriverHelper.element(nativeApps);
        By h5Apps = By.id(String.format("%s:id/h5_tab", egovaPkg));
        WebElement h5Btn = eGovaDriverHelper.element(h5Apps);
        for (int i = 0; i < 2; i++) {
            nativeBtn.click();
            Thread.sleep(1000);

            h5Btn.click();
            Thread.sleep(1000);
        }

        exitApp(egovaPkg, mainMenus);
    }

    private void exitApp(String appId, List<WebElement> mainMenus) {
        mainMenus.get(3).click();

        By exit = By.id(String.format("%s:id/config_item_exit_rlt", appId));
        eGovaDriverHelper.wait(exit);
        eGovaDriverHelper
                .element(exit)
                .click();
    }

    private void inputUsernameAndLogin(String targetUsername, String egovaPkg) {
        By username = By.id(String.format("%s:id/login_txtUsername", egovaPkg));
        eGovaDriverHelper.wait(username);
        WebElement userNameElement = eGovaDriverHelper.element(username);
        String originUserName = userNameElement.getText();
        if (!targetUsername.equals(originUserName)) {
            userNameElement.click();
            eGovaDriverHelper.clear(originUserName);
            userNameElement.sendKeys(targetUsername);
        }
        eGovaDriverHelper.wait(By.id(String.format("%s:id/login_btnSubmit", egovaPkg)));
        WebElement login = eGovaDriverHelper.element(By.id(String.format("%s:id/login_btnSubmit", egovaPkg)));
        login.click();
    }
}
