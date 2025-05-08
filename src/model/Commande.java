package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Représente une commande passée par un client.
 */
public class Commande {

    private int idCommande;
    private LocalDate dateCommande;
    private int idClient;
    private String statut;
    private double total;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }


    /** Constructeur complet (généralement pour lecture depuis la BDD). */
    public Commande(int idCommande, LocalDate dateCommande, int idClient, String statut, double total) {
        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.idClient = idClient;
        this.statut = statut;
        this.total = total;
    }


    // Constructeur sans id (pour création avant insertion)
    public Commande(LocalDate dateCommande, int idClient, String statut) {
        this.dateCommande = dateCommande;
        this.idClient    = idClient;
        this.statut      = statut;
    }

    public Commande() {
        // constructeur vide si nécessaire
    }

    public Commande(int idCommande, LocalDate dateCommande, int idClient, String statut) {
        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.idClient = idClient;
        this.statut = statut;
    }



    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
    // ――― equals / hashCode / toString ―――

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commande)) return false;
        Commande commande = (Commande) o;
        return idCommande == commande.idCommande &&
                idClient    == commande.idClient &&
                Objects.equals(dateCommande, commande.dateCommande) &&
                Objects.equals(statut, commande.statut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCommande, dateCommande, idClient, statut);
    }

    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", dateCommande=" + dateCommande +
                ", idClient=" + idClient +
                ", statut='" + statut + '\'' +
                '}';
    }
}