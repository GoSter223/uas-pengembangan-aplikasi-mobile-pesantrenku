package com.example.pesanternku;

public class Item {
    String nama;
    String alamat;
    String kyai;
    String kota;
    String provinsi;
    String idPesantren;
    String idKota;

    public Item(String nama, String alamat, String kyai, String kota, String provinsi, String idPesantren, String idKota) {
        this.nama = nama;
        this.alamat = alamat;
        this.kyai = kyai;
        this.kota = kota;
        this.provinsi = provinsi;
        this.idPesantren = idPesantren;
        this.idKota = idKota;
    }

    // Getter dan Setter
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

    public String getIdPesantren() {
        return idPesantren;
    }

    public void setIdPesantren(String idPesantren) {
        this.idPesantren = idPesantren;
    }

    public String getIdKota() {
        return idKota;
    }

    public void setIdKota(String idKota) {
        this.idKota = idKota;
    }
}
