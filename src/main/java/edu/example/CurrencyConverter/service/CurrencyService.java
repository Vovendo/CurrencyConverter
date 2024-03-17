package edu.example.CurrencyConverter.service;

import edu.example.CurrencyConverter.model.Currency;

import java.util.List;

public interface CurrencyService {

    List<Currency> findAll(int page);
}
