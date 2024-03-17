package edu.example.CurrencyConverter.repo;

import edu.example.CurrencyConverter.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
