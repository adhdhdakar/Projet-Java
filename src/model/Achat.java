package model;

public class Achat {
    private String nomArticle;
    private String nomClient;
    private int quantite;
    private String dateAchat;
    private int numCommande;
    private int idArticle;

    public Achat(String nomArticle, String nomClient, int quantite, String dateAchat, int numCommande, int idArticle) {
        this.nomArticle = nomArticle;
        this.nomClient = nomClient;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.numCommande = numCommande;
        this.idArticle = idArticle;
    }

    // Getters
    public String getNomArticle() { return nomArticle; }
    public String getNomClient() { return nomClient; }
    public int getQuantite() { return quantite; }
    public String getDateAchat() { return dateAchat; }
    public int getNumCommande() { return numCommande; }
    public int getIdArticle() { return idArticle; }

    // Setters
    public void setNomArticle(String nomArticle) { this.nomArticle = nomArticle; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }
    public void setNumCommande(int numCommande) { this.numCommande = numCommande; }
    public void setIdArticle(int idArticle) { this.idArticle = idArticle; }
}