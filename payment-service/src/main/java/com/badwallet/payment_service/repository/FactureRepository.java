package com.badwallet.payment_service.repository;

import com.badwallet.payment_service.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByWalletCodeAndMoisBetween(String walletCode, LocalDate debut, LocalDate fin);

    List<Facture> findByWalletCodeAndUniteAndMoisBetween(String walletCode, String unite, LocalDate debut, LocalDate fin);

    Optional<Facture> findByReference(String reference);

    List<Facture> findByWalletCodeAndStatutAndMoisBetween(String walletCode, String statut, LocalDate debut, LocalDate fin);

    List<Facture> findByWalletCodeAndUniteAndStatutAndMoisBetween(String walletCode, String unite, String statut, LocalDate debut, LocalDate fin);

    Optional<Facture> findByWalletCodeAndUniteAndStatutAndMois(String walletCode, String unite, String statut, LocalDate mois);
}