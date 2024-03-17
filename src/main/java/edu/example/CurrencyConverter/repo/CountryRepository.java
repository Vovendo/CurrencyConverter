package edu.example.CurrencyConverter.repo;

import edu.example.CurrencyConverter.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
