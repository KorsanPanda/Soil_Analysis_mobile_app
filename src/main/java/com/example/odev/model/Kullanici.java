package com.example.odev.model;

public class Kullanici {
    private String kullanici_id;
    private String kullanici_adi;
    private String sifre;
    private String rol;

    // Firebase için boş yapıcı metod (Zorunludur)
    public Kullanici() { }

    public Kullanici(String kullanici_id, String kullanici_adi, String sifre, String rol) {
        this.kullanici_id = kullanici_id;
        this.kullanici_adi = kullanici_adi;
        this.sifre = sifre;
        this.rol = rol;
    }

    public String getKullanici_id() { return kullanici_id; }
    public void setKullanici_id(String kullanici_id) { this.kullanici_id = kullanici_id; }

    public String getKullanici_adi() { return kullanici_adi; }
    public void setKullanici_adi(String kullanici_adi) { this.kullanici_adi = kullanici_adi; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}