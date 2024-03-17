package edu.example.CurrencyConverter.service;

import edu.example.CurrencyConverter.service.currencyconverter.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
public class CurrencyConverterServiceImpl implements CurrencyConverterService{

    public final CurrencyConverter currencyConverter;

    @Override
    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        return currencyConverter.convertCurrency(amount, fromCurrency, toCurrency);
    }

    @Override
    public StringBuilder getAllRates() {
        return currencyConverter.getAllRates();
    }
}
