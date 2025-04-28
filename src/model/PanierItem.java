package model;

public class PanierItem {
    private Article article;
    private int quantite;

    public PanierItem(Article article, int quantite) {
        this.article = article;
        this.quantite = quantite;
    }

    public Article getArticle() {
        return article;
    }

    public int getQuantite() {
        return quantite;
    }

    public void ajouter1() {
        this.quantite++;
    }

    public void retirer1(){
        if(quantite>0){
            this.quantite--;
        }
    }

    public double getTotal() {
        return article.getPrixUnitaire() * quantite;
    }

    public String getNom() {
        return article.getNom();
    }

    public double getPrixUnitaire() {
        return article.getPrixUnitaire();
    }
}
