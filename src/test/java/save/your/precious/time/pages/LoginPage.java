package save.your.precious.time.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import save.your.precious.time.Timeouts;

/**
 * Created in free time by gutekpl 2018
 */
public class LoginPage extends Page {

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(name = "_submit")
    private WebElement submitButton;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    public PresencePage fillPassAndSubmit(String password) {
        new WebDriverWait(webDriver, Timeouts.TIMEOUT_SECONDS)
                .until(ExpectedConditions.elementToBeClickable(passwordInput));
        passwordInput.sendKeys(password);
        submitButton.click();
        return PageFactory.initElements(webDriver, PresencePage.class);
    }
}
