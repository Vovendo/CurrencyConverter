package edu.example.CurrencyConverter.service.countriestracker;

import java.util.Map;

public interface CountriesTracker {
    Map<String, String > getInfoAboutCountry(String countryName);
}
