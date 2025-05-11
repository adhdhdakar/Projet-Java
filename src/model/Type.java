package model;

public class Type {
    private int idType;
    private String nomType;

    public Type(int idType, String nomType) {
        this.idType  = idType;
        this.nomType = nomType;
    }

    // Getters / Setters
    public int    getIdType()  { return idType; }
    public String getNomType() { return nomType; }
    public void   setNomType(String nomType) { this.nomType = nomType; }

    @Override
    public String toString() {
        return nomType;
    }
}