package bj.mobilier.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "charges_fabrications")
public class ChargeFabrication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "fabrication_id", nullable = false)
    private Fabrication fabrication;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieChargeFabrication categorie;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(length = 300)
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_saisie", nullable = false)
    private LocalDate dateSaisie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fabrication getFabrication() {
        return fabrication;
    }

    public void setFabrication(Fabrication fabrication) {
        this.fabrication = fabrication;
    }

    public CategorieChargeFabrication getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieChargeFabrication categorie) {
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

    public LocalDate getDateSaisie() {
        return dateSaisie;
    }

    public void setDateSaisie(LocalDate dateSaisie) {
        this.dateSaisie = dateSaisie;
    }
}
