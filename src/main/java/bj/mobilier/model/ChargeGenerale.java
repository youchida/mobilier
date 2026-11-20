package bj.mobilier.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "charges_generales")
public class ChargeGenerale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_charge", nullable = false)
    private LocalDate dateCharge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieChargeGenerale categorie;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(length = 300)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateCharge() {
        return dateCharge;
    }

    public void setDateCharge(LocalDate dateCharge) {
        this.dateCharge = dateCharge;
    }

    public CategorieChargeGenerale getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieChargeGenerale categorie) {
        this.categorie = categorie;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
