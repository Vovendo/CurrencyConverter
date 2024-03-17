package edu.example.CurrencyConverter.service;

import edu.example.CurrencyConverter.model.Currency;
import edu.example.CurrencyConverter.repo.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService{
    private final CurrencyRepository currencyRepository;

    @Override
    public List<Currency> findAll(int page) {
        return currencyRepository.findAll(PageRequest.of(page, 5)).getContent();
    }
}
