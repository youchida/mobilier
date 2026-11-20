package bj.mobilier.model;

public enum UniteMesure {
    PIECE("pièce"),
    PLANCHE("planche"),
    METRE("mètre"),
    KILOGRAMME("kilogramme"),
    LITRE("litre"),
    SACHET("sachet"),
    BOITE("boîte");

    private final String libelle;

    UniteMesure(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
