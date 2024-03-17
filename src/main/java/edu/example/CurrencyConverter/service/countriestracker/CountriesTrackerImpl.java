package edu.example.CurrencyConverter.service.countriestracker;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class CountriesTrackerImpl implements CountriesTracker{
    private final WebDriver driver;

    public CountriesTrackerImpl() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }

    @Override
    public Map<String, String> getInfoAboutCountry(String countryName) {
        String url = "https://ru.tradingview.com/markets/world-economy/countries/" + countryName + "/";
        driver.get(url);

        Map<String, String> selectorMap = getStringStringHashMap();
        Map<String, String> resultMap = new HashMap<>();

        for (Map.Entry<String, String> entry : selectorMap.entrySet()) {
            String key = entry.getKey();
            String selector = entry.getValue();

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));

            WebElement element = driver.findElement(By.cssSelector(selector));
            resultMap.put(key, element.getText());
        }

        return resultMap;
    }

    private static HashMap<String, String> getStringStringHashMap() {
        HashMap<String, String> selectorMap = new HashMap<>();
        selectorMap.put("Rate", "#js-category-content > div.tv-economy-country-tab > div > div.indicators-CW4SsfTK > div:nth-child(2) > div.stats-eAllN2Yh > div > div.value-QFVvY0dF > div > span.priceWrapper-qWcO4bp9.priceWrapper-eAllN2Yh > span.highlight-maJ2WnzA.highlight-eAllN2Yh.price-qWcO4bp9.price-eAllN2Yh");
        selectorMap.put("Inflation", "#js-category-content > div.tv-economy-country-tab > div > div.indicators-CW4SsfTK > div:nth-child(3) > div.stats-eAllN2Yh > div > div.value-QFVvY0dF > div > span.priceWrapper-qWcO4bp9.priceWrapper-eAllN2Yh > span.highlight-maJ2WnzA.highlight-eAllN2Yh.price-qWcO4bp9.price-eAllN2Yh");
        selectorMap.put("GDP growth", "#js-category-content > div.tv-economy-country-tab > div > div.table-axm4q9Xl.table-aYw2yKvZ.wrap-g8JgzFJu.noTopBorder-g8JgzFJu > div.container-Tv7LSjUz.contentWrap-g8JgzFJu > div.wrapper-Tv7LSjUz > div > div:nth-child(2) > div > div:nth-child(5) > span.valueTableCell-axm4q9Xl.apply-overflow-tooltip.value-dCK2c9ft.apply-overflow-tooltip");
        selectorMap.put("Unemployment", "#js-category-content > div.tv-economy-country-tab > div > div.indicators-CW4SsfTK > div:nth-child(4) > div.stats-eAllN2Yh > div > div.value-QFVvY0dF > div > span.priceWrapper-qWcO4bp9.priceWrapper-eAllN2Yh > span.highlight-maJ2WnzA.highlight-eAllN2Yh.price-qWcO4bp9.price-eAllN2Yh");
        selectorMap.put("State debt", "#js-category-content > div.tv-economy-country-tab > div > div.indicators-CW4SsfTK > div:nth-child(6) > div.stats-eAllN2Yh > div > div.value-QFVvY0dF > div > span.priceWrapper-qWcO4bp9.priceWrapper-eAllN2Yh > span.highlight-maJ2WnzA.highlight-eAllN2Yh.price-qWcO4bp9.price-eAllN2Yh");
        selectorMap.put("Currency", "#js-category-content > div.tv-economy-country-tab > div > div.table-axm4q9Xl.table-aYw2yKvZ.wrap-g8JgzFJu.noTopBorder-g8JgzFJu > div.container-Tv7LSjUz.contentWrap-g8JgzFJu > div.wrapper-Tv7LSjUz > div > div:nth-child(1) > div > div:nth-child(8) > span");
        return selectorMap;
    }
}
