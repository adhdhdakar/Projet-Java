package model;

public class ArticleInCart {
    private Article article;
    private int quantite;

    public ArticleInCart(Article article, int quantite) {
        this.article  = article;
        this.quantite = quantite;
    }

    public Article getArticle() {
        return article;
    }

    public int getQuantite() {
        return quantite;
    }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    /** Calcule le total prix × quantité */
    public double getTotal() {
        return article.getPrixUnitaire() * quantite;
    }
}