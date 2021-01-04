package com.otacodes.goestateapp.Models;

/**
 * Created by balqisstudio on 3/24/2019.
 */

public class BeritaModels {

        private String Id;
        private String Judul;
        private String deskripsi;
        private String tipe;
        private String foto;
        private String tanggal;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            this.Id = id;
        }


        public String getJudul() {
            return Judul;
        }

        public void setJudul(String judul) {
            this.Judul = judul;
        }

        public String getTipe() {
            return tipe;

        }

        public void setTipe(String tipe) {
            this.tipe = tipe;
        }

    public String getDeskripsi() {
        return deskripsi;

    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFoto() {
        return foto;

    }

    public void setFoto(String foto) {
        this.foto = foto;
    }



    public String getTanggal() {
        return tanggal;

    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

}
