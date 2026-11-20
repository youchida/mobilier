package bj.mobilier.model;

public enum RoleUtilisateur {
    ADMIN("Administrateur"),
    AGENT("Agent");

    private final String libelle;

    RoleUtilisateur(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
