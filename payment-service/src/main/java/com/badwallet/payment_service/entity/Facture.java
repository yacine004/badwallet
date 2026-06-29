package com.badwallet.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "factures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    private String walletCode;

    private String unite; // ISM ou WOYAFAL

    private Double montant;

    private String statut; // PAYEE ou IMPAYEE

    private LocalDate mois; // premier jour du mois concerné
}