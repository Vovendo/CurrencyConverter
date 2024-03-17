package edu.example.CurrencyConverter.service;

import edu.example.CurrencyConverter.service.countriestracker.CountriesTracker;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Setter
@RequiredArgsConstructor
public class CountriesTrackerServiceImpl implements CountriesTrackerService{
    private final CountriesTracker countriesTracker;

    @Override
    public Map<String, String> getInfoAboutCountry(String countryName) {
        return countriesTracker.getInfoAboutCountry(countryName);
    }
}
