package bj.mobilier.model;

public enum CategorieMatiere {
    BOIS("Bois"),
    QUINCAILLERIE("Quincaillerie"),
    FINITION("Finition"),
    AUTRE("Autre");

    private final String libelle;

    CategorieMatiere(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
