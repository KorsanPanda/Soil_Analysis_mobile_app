package com.example.odev.model;

public class Mahsul {
    private String oneri_id;
    private String mahsul;
    private double verim;
    private String sonuc_id; // Mahsul önerisini Lab sonucuna bağlayan ID

    public Mahsul() { }

    public Mahsul(String oneri_id, String mahsul, double verim, String sonuc_id) {
        this.oneri_id = oneri_id;
        this.mahsul = mahsul;
        this.verim = verim;
        this.sonuc_id = sonuc_id;
    }

    public String getOneri_id() { return oneri_id; }
    public void setOneri_id(String oneri_id) { this.oneri_id = oneri_id; }

    public String getMahsul() { return mahsul; }
    public void setMahsul(String mahsul) { this.mahsul = mahsul; }

    public double getVerim() { return verim; }
    public void setVerim(double verim) { this.verim = verim; }

    public String getSonuc_id() { return sonuc_id; }
    public void setSonuc_id(String sonuc_id) { this.sonuc_id = sonuc_id; }
}