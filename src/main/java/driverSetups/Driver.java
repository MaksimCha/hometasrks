package driverSetups;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;

import static io.appium.java_client.remote.MobilePlatform.ANDROID;
import static io.appium.java_client.remote.MobilePlatform.IOS;


/**
 * Initialize a driver with test properties
 */
public class Driver extends TestProperties {

    private static AppiumDriver driver;
    private static WebDriverWait wait;
    protected DesiredCapabilities capabilities;

    protected static String AUT;
    protected static String SUT;
    protected static String TEST_PLATFORM;
    protected static String DRIVER;

    /**
     * Initialize driver with appropriate capabilities depending on platform and application
     *
     * @throws Exception
     */
    protected void prepareDriver() throws Exception {
        capabilities = new DesiredCapabilities();
        String browserName;

        AUT = getProperty("aut");
        String t_sut = getProperty("sut");
        SUT = t_sut == null ? null : "http://" + t_sut;
        TEST_PLATFORM = getProperty("platform");
        DRIVER = getProperty("driver");

        // Setup test platform: Android or iOS. Browser also depends on a platform.
        switch (TEST_PLATFORM) {
            case ANDROID:
                capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
                browserName = "Chrome";
                break;
            case IOS:
                browserName = "Safari";
                break;
            default:
                throw new Exception("Unknown mobile platform");
        }
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, TEST_PLATFORM);

        // Setup type of application: mobile, web
        if (AUT != null && SUT == null) {
            // Native
            File app = new File(AUT);
            capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        } else if (SUT != null && AUT == null) {
            // Web
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, browserName);
        } else {
            throw new Exception("Unknown type of mobile app");
        }

        // Init getDriver for local Appium server with capabilities have been set
        if (driver == null) {
            driver = new AppiumDriver(new URL(DRIVER), capabilities);
        }

        // Set an object to handle timeouts
        if (wait == null) {
            wait = new WebDriverWait(getDriver(), 10);
        }
    }

    public AppiumDriver getDriver() throws Exception {
        if (driver == null) prepareDriver();
        return driver;
    }
}
