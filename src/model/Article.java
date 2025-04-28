package model;

public class Article {
    private int idArticle;
    private String nom;
    private String description;
    private double prixUnitaire;
    private double prixVrac;
    private int quantiteVrac;
    private int stock;

    public Article(int idArticle, String nom, String description, double prixUnitaire, double prixVrac, int quantiteVrac, int stock) {
        this.idArticle = idArticle;
        this.nom = nom;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.prixVrac = prixVrac;
        this.quantiteVrac = quantiteVrac;
        this.stock = stock;
    }

    // Getters
    public int getIdArticle() { return idArticle; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public double getPrixVrac() { return prixVrac; }
    public int getQuantiteVrac() { return quantiteVrac; }
    public int getStock() { return stock; }

    // Setters
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return nom + " - " + prixUnitaire + "€";
    }
}
