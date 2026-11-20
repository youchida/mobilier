package bj.mobilier.model;

public enum CategorieChargeGenerale {
    ELECTRICITE("Électricité"),
    EAU("Eau"),
    LOYER("Loyer"),
    TRANSPORT("Transport"),
    AUTRE("Autre");

    private final String libelle;

    CategorieChargeGenerale(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
