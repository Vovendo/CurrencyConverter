package edu.example.CurrencyConverter.service.currencyconverter;

public interface CurrencyConverter {
    double convertCurrency(double amount, String fromCurrency, String toCurrency);

    StringBuilder getAllRates();
}
