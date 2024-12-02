package com.example.crud.Model;

import com.google.gson.annotations.SerializedName;

public class Mahasiswa {
        @SerializedName("id")
        private int id;

        @SerializedName("nama")
        private String nama;

        @SerializedName("nim")
        private String nim;

        @SerializedName("jurusan")
        private String jurusan;

        @SerializedName("created_at")
        private String createdAt;

        // Konstruktor
        public Mahasiswa(int id, String nama, String nim, String jurusan, String createdAt) {
            this.id = id;
            this.nama = nama;
            this.nim = nim;
            this.jurusan = jurusan;
            this.createdAt = createdAt;
        }

        // Getter dan Setter
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }
        public String getNim() { return nim; }
        public void setNim(String nim) { this.nim = nim; }
        public String getJurusan() { return jurusan; }
        public void setJurusan(String jurusan) { this.jurusan = jurusan; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

}
