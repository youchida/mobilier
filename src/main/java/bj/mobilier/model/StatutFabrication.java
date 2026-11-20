package bj.mobilier.model;

public enum StatutFabrication {
    EN_COURS("En cours"),
    TERMINEE("Terminée");

    private final String libelle;

    StatutFabrication(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
