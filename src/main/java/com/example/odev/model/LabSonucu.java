package com.example.odev.model;

public class LabSonucu {
    private String sonuc_id;
    private String parsel_id; // Sonucu tarlaya bağlayan ID
    private double pH;
    private double organik_m;
    private double fosfor;
    private double potasyum;

    public LabSonucu() { }

    public LabSonucu(String sonuc_id, String parsel_id, double pH, double organik_m, double fosfor, double potasyum) {
        this.sonuc_id = sonuc_id;
        this.parsel_id = parsel_id;
        this.pH = pH;
        this.organik_m = organik_m;
        this.fosfor = fosfor;
        this.potasyum = potasyum;
    }

    public String getSonuc_id() { return sonuc_id; }
    public void setSonuc_id(String sonuc_id) { this.sonuc_id = sonuc_id; }

    public String getParsel_id() { return parsel_id; }
    public void setParsel_id(String parsel_id) { this.parsel_id = parsel_id; }

    public double getpH() { return pH; }
    public void setpH(double pH) { this.pH = pH; }

    public double getOrganik_m() { return organik_m; }
    public void setOrganik_m(double organik_m) { this.organik_m = organik_m; }

    public double getFosfor() { return fosfor; }
    public void setFosfor(double fosfor) { this.fosfor = fosfor; }

    public double getPotasyum() { return potasyum; }
    public void setPotasyum(double potasyum) { this.potasyum = potasyum; }
}