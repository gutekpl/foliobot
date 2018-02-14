package save.your.precious.time.pages;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;
import static save.your.precious.time.Timeouts.NON_AJAX_JS_WAIT_MILLISECONDS;
import static save.your.precious.time.Timeouts.TIMEOUT_SECONDS;

/**
 * Created in free time by gutekpl 2018
 */
public class PresencePage extends Page {

    @FindBy(xpath = "//button[text()='SAVE']")
    private WebElement saveButton;

    @FindBy(xpath = "//button[text()='EDIT']")
    private WebElement editButton;

    @FindBy(className = "loading")
    private WebElement loadingIndicator;

    // Date picker

    @FindBy(xpath = "//*[@id='refDate']//input")
    private WebElement datePicker;

    @FindBy(className = "ui-datepicker-prev")
    private WebElement prevMonthButton;

    @FindBy(xpath = "(//*[@id='refDate']//a[text()='1'])[1]")
    private WebElement firstDayOfMonth;

    @FindBy(xpath = "//p-calendar/following-sibling::div//i")
    private WebElement nextDayButton;

    // Periods panel

    @FindBy(xpath = "//button[contains(text(), 'Insert period')]")
    private WebElement insertPeriodButton;

    @FindBy(xpath = "(//*[@id='periodFrom'])[1]")
    private WebElement morningPeriodFrom;

    @FindBy(xpath = "(//*[@id='periodTo'])[1]")
    private WebElement morningPeriodTo;

    @FindBy(xpath = "(//*[@id='periodFrom'])[2]")
    private WebElement afternoonPeriodFrom;

    @FindBy(xpath = "(//*[@id='periodTo'])[2]")
    private WebElement afternoonPeriodTo;

    // Efforts panel

    @FindBy(xpath = "//button[contains(text(), 'Insert effort')]")
    private WebElement insertEffortButton;

    @FindBy(id = "project")
    private WebElement projectSelect;


    public PresencePage(WebDriver webDriver) {
        super(webDriver);
    }

    public PresencePage waitUntilPageReloaded() {
        //make sure that second wait is executed AFTER "loading..." indicator
        try {
            new FluentWait(webDriver)
                    .withTimeout(NON_AJAX_JS_WAIT_MILLISECONDS, MILLISECONDS)
                    .ignoring(NoSuchElementException.class)
                    .until(visibilityOf(loadingIndicator));
        } catch (TimeoutException e) {
            //it's fine, page was reloaded faster than we started polling for "loading..." indicator
        }

        new FluentWait(webDriver)
                .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .until(visibilityOfAllElements(asList(datePicker, nextDayButton)));
        return this;
    }

    public PresencePage goToPreviousMonth() {
        System.out.println("Jumping to the previous month.");
        datePicker.click();
        prevMonthButton.click();
        return this;
    }

    public PresencePage goToFirstDayOfCurrentMonth() {
        System.out.println("Jumping to the first day of month");
        datePicker.click();
        firstDayOfMonth.click();
        return waitUntilPageReloaded();
    }

    public boolean isCurrentDateWorkingDay() {
        // true when we can add new period or edit the daily report saved in past
        boolean workingDay = insertPeriodButton.isEnabled() || editButton.isEnabled();

        if (!workingDay) {
            System.out.println("Non-working day: " + getCurrentDate());
        }
        return workingDay;
    }

    public boolean isCurrentDateNotTheFirstDayOfMonth() throws ParseException {
        String currentDateStr = getCurrentDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(currentDateStr));
        boolean notFirstDayOfMonth = 1 != cal.get(Calendar.DAY_OF_MONTH);
        if (!notFirstDayOfMonth) {
            System.out.println("Reached first day of next month: " + currentDateStr);
        }
        return notFirstDayOfMonth;
    }

    private String getCurrentDate() {
        return datePicker.getAttribute("ng-reflect-value");
    }

    private boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public PresencePage fillMorningPeriod(String periodFrom, String periodTo) {
        System.out.println("Filling " + getCurrentDate() + " morning from " + periodFrom + " to " + periodTo);
        return fillPeriod(morningPeriodFrom, morningPeriodTo, periodFrom, periodTo);
    }

    public PresencePage fillAfternoonPeriod(String periodFrom, String periodTo) {
        System.out.println("Filling " + getCurrentDate() + " afternoon from " + periodFrom + " to " + periodTo);
        return fillPeriod(afternoonPeriodFrom, afternoonPeriodTo, periodFrom, periodTo);
    }

    public PresencePage fillPeriod(WebElement elementFrom, WebElement elementTo, String valueFrom, String valueTo) {
        if (!isDisplayed(elementFrom)) {
            insertPeriodButton.click();
        }

        elementFrom.clear();
        elementFrom.sendKeys(valueFrom);

        elementTo.clear();
        elementTo.sendKeys(valueTo);
        return this;
    }

    public PresencePage submit() {
        System.out.println("Submitting report for " + getCurrentDate());
        saveButton.click();
        return waitUntilPageReloaded();
    }

    public PresencePage goToNextDay() {
        System.out.println("Jumping to the next day, current one was: " + getCurrentDate());
        nextDayButton.click();
        return waitUntilPageReloaded();
    }

    public PresencePage clickEditButtonIfPresent() {
        if (editButton.isDisplayed() && editButton.isEnabled()) {
            editButton.click();
        }
        return waitUntilPageReloaded();
    }

    public PresencePage fillEffort(String projectId) {
        System.out.println("Filling effort panel");
        if (!isDisplayed(projectSelect)) {
            insertEffortButton.click();
        }
        if (isNotBlank(projectId)) {
            new Select(projectSelect).selectByValue(projectId);
        }
        return this;
    }
}
