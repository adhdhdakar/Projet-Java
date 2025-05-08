package model;

public class Achat {
    private String nomArticle;
    private String prenomClient;
    private String nomClient;
    private int quantite;
    private String dateAchat;
    private int numCommande;
    private int idArticle;
    private int idLigne;

    public Achat(String nomArticle, String prenomClient, String nomClient, int quantite, String dateAchat, int numCommande, int idArticle, int idLigne) {
        this.nomArticle = nomArticle;
        this.prenomClient = prenomClient;
        this.nomClient = nomClient;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.numCommande = numCommande;
        this.idArticle = idArticle;
        this.idLigne = idLigne;
    }

    // Getters
    public String getNomArticle() { return nomArticle; }
    public String getPrenomClient() { return prenomClient; }
    public String getNomClient() { return nomClient; }
    public int getQuantite() { return quantite; }
    public String getDateAchat() { return dateAchat; }
    public int getNumCommande() { return numCommande; }
    public int getIdArticle() { return idArticle; }
    public String getNomCompletClient() { return prenomClient + " " + nomClient; }
    public int getIdLigne() { return idLigne; }

    // Setters
    public void setNomArticle(String nomArticle) { this.nomArticle = nomArticle; }
    public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }
    public void setNumCommande(int numCommande) { this.numCommande = numCommande; }
    public void setIdArticle(int idArticle) { this.idArticle = idArticle; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }
}