package com.example.pesanternku;

public class Item {
    String nama;
    String alamat;
    String kyai;
    String kota;
    String provinsi;

    public Item(String nama, String alamat, String kyai, String kota, String provinsi) {
        this.nama = nama;
        this.alamat = alamat;
        this.kyai = kyai;
        this.kota = kota;
        this.provinsi = provinsi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKyai() {
        return kyai;
    }

    public void setKyai(String kyai) {
        this.kyai = kyai;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }
}
