package edu.example.CurrencyConverter.service.currencyconverter;

import edu.example.CurrencyConverter.model.Currency;
import edu.example.CurrencyConverter.repo.CurrencyRepository;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Setter
public class CurrencyConverterImpl implements CurrencyConverter {
    private final CurrencyRepository currencyRepository;

    @Value("${api-key}")
    private String API_KEY;

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        double result = -1;
        try {
            String apiUrl = "https://openexchangerates.org/api/latest.json?app_id=" + API_KEY;
            HttpResponse<JsonNode> response = Unirest.get(apiUrl).header("Accept", "application/json").asJson();

            int status = response.getStatus();
            JsonNode responseBody = response.getBody();

            if (status == 200) {
                JSONObject rates = responseBody.getObject().getJSONObject("rates");

                double rateFrom = rates.getDouble(fromCurrency.toUpperCase());
                double rateTo = rates.getDouble(toCurrency.toUpperCase());

                result =  (amount / rateFrom) * rateTo;
            } else {
                System.err.println("Error: " + response.getStatusText());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StringBuilder getAllRates() {
        try {
            String apiUrl = "https://openexchangerates.org/api/latest.json?app_id=" + API_KEY;
            HttpResponse<JsonNode> response = Unirest.get(apiUrl).header("Accept", "application/json").asJson();

            int status = response.getStatus();
            JsonNode responseBody = response.getBody();

            if (status == 200) {
                JSONObject rates = responseBody.getObject().getJSONObject("rates");
                StringBuilder result = new StringBuilder();
                List<Currency> currencies = currencyRepository.findAll();
                for (Currency currency : currencies) {
                    if (!currency.getName().equals("USD")) {
                        result
                                .append("• ")
                                .append(currency.getName())
                                .append(" - ")
                                .append(rates.getDouble(currency.getName()))
                                .append(" (")
                                .append(currency.getFullName())
                                .append(") ").append("\n");
                    }
                }
                return result;
            } else {
                System.err.println("Error: " + response.getStatusText());
                return new StringBuilder("Error: " + response.getStatusText());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
