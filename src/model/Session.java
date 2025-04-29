package model;

import model.Client;

public class Session {
    private static Session instance = new Session();
    private Client client;
    private Session(){}
    public static Session getInstance(){ return instance; }
    public Client getClient(){ return client; }
    public void setClient(Client c){ this.client = c; }
}