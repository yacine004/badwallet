package com.badwallet.badwallet_api.services;

import com.badwallet.badwallet_api.entity.Wallet;
import com.badwallet.badwallet_api.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(Wallet wallet) {
        if (walletRepository.existsByPhoneNumber(wallet.getPhoneNumber())) {
            throw new RuntimeException("Un wallet existe déjà avec ce numéro : " + wallet.getPhoneNumber());
        }
        if (walletRepository.existsByCode(wallet.getCode())) {
            throw new RuntimeException("Un wallet existe déjà avec ce code : " + wallet.getCode());
        }
        return walletRepository.save(wallet);
    }

    public Page<Wallet> getAllWallets(int page, int size) {
        return walletRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
    }

    public Wallet getWalletByPhone(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Wallet introuvable : " + phoneNumber));
    }

    public Double getBalance(String phoneNumber) {
        return getWalletByPhone(phoneNumber).getBalance();
    }
}