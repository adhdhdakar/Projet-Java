package model;

public class Achat {
    private String nomArticle;
    private String nomClient;
    private int quantite;
    private String dateAchat;
    private int numCommande;

    public Achat(String nomArticle, String nomClient, int quantite, String dateAchat, int numCommande) {
        this.nomArticle = nomArticle;
        this.nomClient = nomClient;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.numCommande = numCommande;
    }

    // Getters
    public String getNomArticle() { return nomArticle; }
    public String getNomClient() { return nomClient; }
    public int getQuantite() { return quantite; }
    public String getDateAchat() { return dateAchat; }
    public int getNumCommande() { return numCommande; }

    // Setters
    public void setNomArticle(String nomArticle) { this.nomArticle = nomArticle; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }
    public void setNumCommande(int numCommande) { this.numCommande = numCommande; }
}