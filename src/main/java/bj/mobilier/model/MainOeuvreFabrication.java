package bj.mobilier.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "main_oeuvre_fabrications")
public class MainOeuvreFabrication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "fabrication_id", nullable = false)
    private Fabrication fabrication;

    @Column(name = "nom_ouvrier", nullable = false, length = 120)
    private String nomOuvrier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal duree;

    @Column(name = "unite_duree", nullable = false, length = 20)
    private String uniteDuree;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

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

    public String getNomOuvrier() {
        return nomOuvrier;
    }

    public void setNomOuvrier(String nomOuvrier) {
        this.nomOuvrier = nomOuvrier;
    }

    public BigDecimal getDuree() {
        return duree;
    }

    public void setDuree(BigDecimal duree) {
        this.duree = duree;
    }

    public String getUniteDuree() {
        return uniteDuree;
    }

    public void setUniteDuree(String uniteDuree) {
        this.uniteDuree = uniteDuree;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateSaisie() {
        return dateSaisie;
    }

    public void setDateSaisie(LocalDate dateSaisie) {
        this.dateSaisie = dateSaisie;
    }
}
