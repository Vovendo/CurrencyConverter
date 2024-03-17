package edu.example.CurrencyConverter.service;

import edu.example.CurrencyConverter.model.Country;
import edu.example.CurrencyConverter.repo.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService{
    private final CountryRepository countryRepository;

    @Override
    public List<Country> findAll(int page) {
        return countryRepository.findAll(PageRequest.of(page, 5)).getContent();
    }
}
