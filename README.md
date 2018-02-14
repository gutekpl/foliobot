# FolioBot
You know why you are here.

Otherwise you won't find anything interesting here and it's better to leave, you can go here: [https://google.com/search?q=folio]

# Requirements
* Windows (not tested in other systems)
* JDK 8
* Maven 3
* Chrome web browser (tested with v.62)

# How To 
1. Few Selenium selectors rely on english labels - make sure your language is set to EN.
2. Download Chrome driver from [https://sites.google.com/a/chromium.org/chromedriver/downloads] and copy to: `/src/test/resources/chromedriver.exe` (tested with v.2.35)
3. Edit configuration file `/src/test/resources/foliobot.properties`
4. Run `mvn clean test` and go for coffee
