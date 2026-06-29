package com.badwallet.payment_service.services;

import com.badwallet.payment_service.entity.Facture;
import com.badwallet.payment_service.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FactureSeederService {

    private final FactureRepository factureRepository;

    public void seed(int numWallets) {
        if (factureRepository.count() > 0) return;

        Random random = new Random();
        String[] unites = {"ISM", "WOYAFAL"};

        // 6 derniers mois
        List<LocalDate> mois = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            mois.add(now.minusMonths(i).withDayOfMonth(1));
        }

        for (int w = 1; w <= numWallets; w++) {
            String walletCode = String.format("WLT-%07d", w);

            for (String unite : unites) {
                for (int m = 0; m < mois.size(); m++) {
                    LocalDate moisDate = mois.get(m);

                    double montant = unite.equals("ISM")
                            ? 150000.0
                            : 5000.0 + random.nextInt(20000);

                    // Les 3 premiers mois payés, les 3 derniers impayés
                    String statut = m < 3 ? "PAYEE" : "IMPAYEE";

                    String reference = String.format("FAC-%s-%d-%d", unite, w, m + 1);

                    if (factureRepository.findByReference(reference).isPresent()) continue;

                    Facture facture = Facture.builder()
                            .reference(reference)
                            .walletCode(walletCode)
                            .unite(unite)
                            .montant(montant)
                            .statut(statut)
                            .mois(moisDate)
                            .build();

                    factureRepository.save(facture);
                }
            }
        }
    }
}