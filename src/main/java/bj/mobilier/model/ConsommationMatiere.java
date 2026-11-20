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
@Table(name = "consommations_matieres")
public class ConsommationMatiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "fabrication_id", nullable = false)
    private Fabrication fabrication;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "matiere_id", nullable = false)
    private MatierePremiere matiere;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantite;

    @Column(name = "cout_unitaire_fige", nullable = false, precision = 15, scale = 2)
    private BigDecimal coutUnitaireFige;

    @Column(name = "cout_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal coutTotal;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_consommation", nullable = false)
    private LocalDate dateConsommation;

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

    public MatierePremiere getMatiere() {
        return matiere;
    }

    public void setMatiere(MatierePremiere matiere) {
        this.matiere = matiere;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getCoutUnitaireFige() {
        return coutUnitaireFige;
    }

    public void setCoutUnitaireFige(BigDecimal coutUnitaireFige) {
        this.coutUnitaireFige = coutUnitaireFige;
    }

    public BigDecimal getCoutTotal() {
        return coutTotal;
    }

    public void setCoutTotal(BigDecimal coutTotal) {
        this.coutTotal = coutTotal;
    }

    public LocalDate getDateConsommation() {
        return dateConsommation;
    }

    public void setDateConsommation(LocalDate dateConsommation) {
        this.dateConsommation = dateConsommation;
    }
}
