package edu.example.CurrencyConverter.service;

import edu.example.CurrencyConverter.model.Country;

import java.util.List;

public interface CountryService {
    List<Country> findAll(int page);
}
