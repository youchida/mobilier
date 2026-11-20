package bj.mobilier.service;

import bj.mobilier.model.CategorieChargeGenerale;
import bj.mobilier.model.ChargeGenerale;
import bj.mobilier.repository.ChargeGeneraleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChargeGeneraleService {

    private final ChargeGeneraleRepository chargeGeneraleRepository;

    public ChargeGeneraleService(ChargeGeneraleRepository chargeGeneraleRepository) {
        this.chargeGeneraleRepository = chargeGeneraleRepository;
    }

    public List<ChargeGenerale> listerToutes() {
        return chargeGeneraleRepository.findAllByOrderByDateChargeDescIdDesc();
    }

    public ChargeGenerale trouverParId(Long id) {
        return chargeGeneraleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charge générale introuvable."));
    }

    @Transactional
    public ChargeGenerale creer(LocalDate dateCharge, CategorieChargeGenerale categorie, BigDecimal montant, String description) {
        validerChamps(dateCharge, categorie, montant);
        ChargeGenerale charge = new ChargeGenerale();
        charge.setDateCharge(dateCharge);
        charge.setCategorie(categorie);
        charge.setMontant(montant);
        charge.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        return chargeGeneraleRepository.save(charge);
    }

    @Transactional
    public ChargeGenerale modifier(Long id, LocalDate dateCharge, CategorieChargeGenerale categorie, BigDecimal montant, String description) {
        validerChamps(dateCharge, categorie, montant);
        ChargeGenerale charge = trouverParId(id);
        charge.setDateCharge(dateCharge);
        charge.setCategorie(categorie);
        charge.setMontant(montant);
        charge.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        return chargeGeneraleRepository.save(charge);
    }

    @Transactional
    public void supprimer(Long id) {
        ChargeGenerale charge = trouverParId(id);
        chargeGeneraleRepository.delete(charge);
    }

    private void validerChamps(LocalDate dateCharge, CategorieChargeGenerale categorie, BigDecimal montant) {
        if (dateCharge == null) {
            throw new IllegalArgumentException("La date de la charge est obligatoire.");
        }
        if (categorie == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire.");
        }
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant en FCFA doit être strictement supérieur à zéro.");
        }
    }
}
