package model;

/**
 * Représente une ligne de commande (un article + quantité) rattachée à une commande.
 */
public class LigneCommande {
    private int idLigne;
    private int idCommande;
    private int idArticle;
    private int quantite;

    // Constructeur complet (lecture BDD)
    public LigneCommande(int idLigne, int idCommande, int idArticle, int quantite) {
        this.idLigne    = idLigne;
        this.idCommande = idCommande;
        this.idArticle  = idArticle;
        this.quantite   = quantite;
    }

    // Constructeur sans idLigne (avant insertion)
    public LigneCommande(int idCommande, int idArticle, int quantite) {
        this(0, idCommande, idArticle, quantite);
    }

    // Getters & Setters
    public int getIdLigne() {
        return idLigne;
    }
    public void setIdLigne(int idLigne) {
        this.idLigne = idLigne;
    }
    public int getIdCommande() {
        return idCommande;
    }
    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }
    public int getIdArticle() {
        return idArticle;
    }
    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }
    public int getQuantite() {
        return quantite;
    }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}