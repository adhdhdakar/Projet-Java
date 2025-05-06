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

    /**
     * Calcule le total en appliquant le tarif vrac
     */
    public double getTotal() {
        int q = quantite;
        int packSize   = article.getQuantiteVrac();  // ex. 10
        double packPrice = article.getPrixVrac();    // ex. 4 €

        if (packSize > 0 && packPrice > 0) {
            int nbPacks = q / packSize;       // ex. 34 / 10 = 3
            int reste   = q % packSize;       // ex. 34 % 10 = 4
            return nbPacks * packPrice
                    + reste   * article.getPrixUnitaire();
        } else {
            // Pas de prix vrac défini → tarif unitaire classique
            return q * article.getPrixUnitaire();
        }
    }
}