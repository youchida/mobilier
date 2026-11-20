package bj.mobilier.model;

public enum CategorieChargeFabrication {
    ELECTRICITE("Électricité"),
    TRANSPORT("Transport"),
    LOCATION("Location"),
    OUTILLAGE("Outillage"),
    AUTRE("Autre");

    private final String libelle;

    CategorieChargeFabrication(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
