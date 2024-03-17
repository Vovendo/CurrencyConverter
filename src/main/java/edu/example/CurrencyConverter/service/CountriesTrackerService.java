package edu.example.CurrencyConverter.service;

import java.util.Map;

public interface CountriesTrackerService {
    Map<String, String> getInfoAboutCountry(String countryName);
}
