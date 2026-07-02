package com.example.odev.model;

public class Tarla {
    private String parsel_id;
    private String il;
    private String ilce;
    private double enlem;
    private double boylam;
    private String kullanici_id; // Tarlayı kullanıcıya bağlayan ID

    public Tarla() { }

    public Tarla(String parsel_id, String il, String ilce, double enlem, double boylam, String kullanici_id) {
        this.parsel_id = parsel_id;
        this.il = il;
        this.ilce = ilce;
        this.enlem = enlem;
        this.boylam = boylam;
        this.kullanici_id = kullanici_id;
    }

    public String getParsel_id() { return parsel_id; }
    public void setParsel_id(String parsel_id) { this.parsel_id = parsel_id; }

    public String getIl() { return il; }
    public void setIl(String il) { this.il = il; }

    public String getIlce() { return ilce; }
    public void setIlce(String ilce) { this.ilce = ilce; }

    public double getEnlem() { return enlem; }
    public void setEnlem(double enlem) { this.enlem = enlem; }

    public double getBoylam() { return boylam; }
    public void setBoylam(double boylam) { this.boylam = boylam; }

    public String getKullanici_id() { return kullanici_id; }
    public void setKullanici_id(String kullanici_id) { this.kullanici_id = kullanici_id; }
}