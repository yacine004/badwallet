package com.badwallet.payment_service.services;

import java.util.ArrayList;
import com.badwallet.payment_service.entity.Facture;
import com.badwallet.payment_service.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;

    // Factures impayées du mois en cours
    public List<Facture> getFacturesMoisCourant(String walletCode, String unite) {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = debut.plusMonths(1).minusDays(1);

        if (unite != null && !unite.isEmpty()) {
            return factureRepository.findByWalletCodeAndUniteAndStatutAndMoisBetween(
                    walletCode, unite, "IMPAYEE", debut, fin);
        }
        return factureRepository.findByWalletCodeAndStatutAndMoisBetween(
                walletCode, "IMPAYEE", debut, fin);
    }

    // Factures par période
    public List<Facture> getFacturesByPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        return factureRepository.findByWalletCodeAndMoisBetween(walletCode, debut, fin);
    }

    // Payer une facture du mois en cours
    public Facture payerFactureMoisCourant(String walletCode, String unite) {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = debut.plusMonths(1).minusDays(1);

        Optional<Facture> factureOpt = factureRepository
                .findByWalletCodeAndUniteAndStatutAndMois(walletCode, unite, "IMPAYEE", debut);

        if (factureOpt.isEmpty()) {
            throw new RuntimeException("Aucune facture impayée du mois en cours pour " + unite);
        }

        Facture facture = factureOpt.get();
        facture.setStatut("PAYEE");
        return factureRepository.save(facture);
    }

    // Payer des factures spécifiques par référence
    public List<Facture> payerFacturesParReferences(List<String> references) {
        List<Facture> payees = new ArrayList<>();

        for (String ref : references) {
            Facture facture = factureRepository.findByReference(ref)
                    .orElseThrow(() -> new RuntimeException("Facture introuvable : " + ref));

            if (facture.getStatut().equals("PAYEE")) {
                throw new RuntimeException("Facture déjà payée : " + ref);
            }

            facture.setStatut("PAYEE");
            payees.add(factureRepository.save(facture));
        }

        return payees;
    }

    public Optional<Facture> getByReference(String reference) {
        return factureRepository.findByReference(reference);
    }
}