package model;

public class Client {
    private int idClient;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String typeClient;
    private String codeCb;        // nouveau champ

    // Constructeur étendu pour inclure codeCb
    public Client(int idClient, String nom, String prenom, String email,
                  String motDePasse, String typeClient, String codeCb) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.typeClient = typeClient;
        this.codeCb = codeCb;
    }

    // Ancien constructeur (pour compatibilité) - codeCb à null
    public Client(int idClient, String nom, String prenom, String email,
                  String motDePasse, String typeClient) {
        this(idClient, nom, prenom, email, motDePasse, typeClient, null);
    }

    // Getters
    public int getIdClient() { return idClient; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public String getTypeClient() { return typeClient; }
    public String getCodeCb() { return codeCb; }           // getter ajouté

    // Setters
    public void setIdClient(int idClient) { this.idClient = idClient; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setTypeClient(String typeClient) { this.typeClient = typeClient; }
    public void setCodeCb(String codeCb) { this.codeCb = codeCb; } // setter ajouté

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + email + ")";
    }
}