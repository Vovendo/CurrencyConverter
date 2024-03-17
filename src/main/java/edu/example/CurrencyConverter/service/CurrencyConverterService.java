package edu.example.CurrencyConverter.service;

public interface CurrencyConverterService {
    double convertCurrency(double amount, String fromCurrency, String toCurrency);

    StringBuilder getAllRates();
}
