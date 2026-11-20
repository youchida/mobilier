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
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "fabrications")
public class Fabrication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "modele_id", nullable = false)
    private ModeleMeuble modele;

    @Column(name = "quantite_prevue", nullable = false)
    private Integer quantitePrevue;

    @Column(name = "quantite_produite")
    private Integer quantiteProduite;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutFabrication statut = StatutFabrication.EN_COURS;

    @Column(length = 500)
    private String observation;

    public boolean isTerminee() {
        return statut == StatutFabrication.TERMINEE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModeleMeuble getModele() {
        return modele;
    }

    public void setModele(ModeleMeuble modele) {
        this.modele = modele;
    }

    public Integer getQuantitePrevue() {
        return quantitePrevue;
    }

    public void setQuantitePrevue(Integer quantitePrevue) {
        this.quantitePrevue = quantitePrevue;
    }

    public Integer getQuantiteProduite() {
        return quantiteProduite;
    }

    public void setQuantiteProduite(Integer quantiteProduite) {
        this.quantiteProduite = quantiteProduite;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public StatutFabrication getStatut() {
        return statut;
    }

    public void setStatut(StatutFabrication statut) {
        this.statut = statut;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
