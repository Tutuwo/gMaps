package com.example.gmaps;

public class Note {
    private String data;
    private String descricao;
    private String morada;
    private String url;
    private Double lati;
    private Double longi;
    private String tipo;
    private String localidade;
    public Note() {
        //empty constructor needed
    }

    public Note(String data, String descricao, String morada, String url, Double lati, Double longi, String tipo) {
        this.data = data;
        this.descricao= descricao;
        this.morada = morada;
        this.url = url;
        this.lati = lati;
        this.longi = longi;
        this.tipo = tipo;

    }

    public Double getLati() { return lati; }

    public Double getLongi() { return longi; }

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMorada() {
        return morada;
    }

    public String getTipo() { return tipo; }
}
