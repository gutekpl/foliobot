package save.your.precious.time.pages;

import org.openqa.selenium.WebDriver;

/**
 * Created in free time by gutekpl 2018
 */
public abstract class Page {

    protected WebDriver webDriver;

    public Page(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
