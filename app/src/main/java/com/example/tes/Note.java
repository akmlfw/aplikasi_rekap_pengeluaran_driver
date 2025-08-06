package com.example.tes;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Note {

    double kmawali, kmakhiri, bahanbkr, biypark, serman, ngetol;
    String tanggalu, docId, bulanu;
    ArrayList<String> ImageUrls;

    public Note() { }

    public Note(double kmawali, double bahanbkr, double biypark,
                double serman, double ngetol, double kmakhiri, String tanggalu, String bulanu, String docId, ArrayList<String> imageUrls) {
        this.kmawali = kmawali;
        this.bahanbkr = bahanbkr;
        this.biypark = biypark;
        this.serman = serman;
        this.ngetol = ngetol;
        this.kmakhiri = kmakhiri;
        this.tanggalu = tanggalu;
        this.bulanu = bulanu;
        this.docId = docId;
        this.ImageUrls = imageUrls;
    }

    public double getKmawali() {
        return kmawali;
    }

    public void setKmawali(double kmawali) {
        this.kmawali = kmawali;
    }

    public double getBahanbkr() {
        return bahanbkr;
    }

    public void setBahanbkr(double bahanbkr) {
        this.bahanbkr = bahanbkr;
    }

    public double getNgetol() {
        return ngetol;
    }

    public void setNgetol(double ngetol) {
        this.ngetol = ngetol;
    }

    public double getBiypark() {
        return biypark;
    }

    public void setBiypark(double biypark) {
        this.biypark = biypark;
    }

    public double getSerman() {
        return serman;
    }

    public void setSerman(double serman) {
        this.serman = serman;
    }

    public double getKmakhiri() {
        return kmakhiri;
    }

    public void setKmakhiri(double kmakhiri) {
        this.kmakhiri = kmakhiri;
    }

    public String getTanggalu() {
        return tanggalu;
    }

    public void setTanggalu(String tanggalu) {
        this.tanggalu = tanggalu;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public ArrayList<String> getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.ImageUrls = imageUrls;
    }
    public String getBulanu() {
        return bulanu;
    }

    public void setBulanu(String bulanu) {
        this.bulanu = bulanu;
    }
}
