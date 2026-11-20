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

@Entity
@Table(name = "matieres_premieres")
public class MatierePremiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieMatiere categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UniteMesure unite;

    @Column(name = "stock_actuel", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockActuel = BigDecimal.ZERO;

    @Column(name = "cout_unitaire_moyen", nullable = false, precision = 15, scale = 2)
    private BigDecimal coutUnitaireMoyen = BigDecimal.ZERO;

    @Column(name = "seuil_alerte", nullable = false, precision = 12, scale = 2)
    private BigDecimal seuilAlerte = BigDecimal.ZERO;

    public boolean isStockBas() {
        return stockActuel.compareTo(seuilAlerte) <= 0;
    }

    public BigDecimal getValeurStock() {
        return stockActuel.multiply(coutUnitaireMoyen);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public CategorieMatiere getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieMatiere categorie) {
        this.categorie = categorie;
    }

    public UniteMesure getUnite() {
        return unite;
    }

    public void setUnite(UniteMesure unite) {
        this.unite = unite;
    }

    public BigDecimal getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(BigDecimal stockActuel) {
        this.stockActuel = stockActuel;
    }

    public BigDecimal getCoutUnitaireMoyen() {
        return coutUnitaireMoyen;
    }

    public void setCoutUnitaireMoyen(BigDecimal coutUnitaireMoyen) {
        this.coutUnitaireMoyen = coutUnitaireMoyen;
    }

    public BigDecimal getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(BigDecimal seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
}
