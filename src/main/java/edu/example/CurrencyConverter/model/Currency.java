package edu.example.CurrencyConverter.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String fullName;
}
