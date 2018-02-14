package save.your.precious.time;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import save.your.precious.time.pages.LoginPage;
import save.your.precious.time.pages.PresencePage;

import java.text.ParseException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created in free time by gutekpl 2018
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource("classpath:foliobot.properties")
public class FolioBotTest {

    private WebDriver webDriver;

    @Value("${url}")
    private String url;

    @Value("${password}")
    private String password;

    @Value("${fillPreviousMonth}")
    private boolean fillPreviousMonth;

    // Project ID

    @Value("${projectId}")
    private String projectId;

    // Period hours

    @Value("${morningPeriodFrom}")
    private String morningPeriodFrom;

    @Value("${morningPeriodTo}")
    private String morningPeriodTo;

    @Value("${afternoonPeriodFrom}")
    private String afternoonPeriodFrom;

    @Value("${afternoonPeriodTo}")
    private String afternoonPeriodTo;

    @Configuration
    static class Config {
    }

    @Before
    public void init() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        webDriver = new ChromeDriver();

        // Make sure that non-Ajax JavaScripts are executed
        webDriver.manage().timeouts().implicitlyWait(Timeouts.NON_AJAX_JS_WAIT_MILLISECONDS, MILLISECONDS);
        webDriver.manage().timeouts().setScriptTimeout(Timeouts.NON_AJAX_JS_WAIT_MILLISECONDS, MILLISECONDS);
    }

    @Test
    public void saveYourPreciousTime() throws InterruptedException, ParseException {

        PresencePage presencePage = login(url, password);

        if (fillPreviousMonth) {
            presencePage.goToPreviousMonth();
        }

        presencePage.goToFirstDayOfCurrentMonth();

        do {
            fillDailyReportAndGoToNextDay(presencePage);
        } while (presencePage.isCurrentDateNotTheFirstDayOfMonth());

        System.out.println("Report finished. Verify logged days, submit report and go for coffee...");
    }

    private PresencePage login(String url, String password) {

        webDriver.get(url);

        if (webDriver.getCurrentUrl().contains("login")) {
            System.out.println("Logging in...");
            return PageFactory.initElements(webDriver, LoginPage.class)
                    .fillPassAndSubmit(password)
                    .waitUntilPageReloaded();
        } else {
            System.out.println("You were already logged in.");
            return PageFactory.initElements(webDriver, PresencePage.class)
                    .waitUntilPageReloaded();
        }
    }

    private void fillDailyReportAndGoToNextDay(PresencePage presencePage) {

        if (!presencePage.isCurrentDateWorkingDay()) {
            presencePage.goToNextDay();
            return;
        }

        presencePage.clickEditButtonIfPresent()
                .fillMorningPeriod(morningPeriodFrom, morningPeriodTo)
                .fillAfternoonPeriod(afternoonPeriodFrom, afternoonPeriodTo)
                .fillEffort(projectId)
                .submit()
                .goToNextDay();
    }

    @After
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
