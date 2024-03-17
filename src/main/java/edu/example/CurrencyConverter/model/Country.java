package edu.example.CurrencyConverter.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
}
