package com.example.crud;


import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.crud.API.ApiClient;
import com.example.crud.Model.Mahasiswa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MahasiswaAdapter adapter;
    private List<Mahasiswa> mahasiswaList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabTambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi View
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabTambah = findViewById(R.id.fabTambah);

        // Setup RecyclerView
        mahasiswaList = new ArrayList<>();
        adapter = new MahasiswaAdapter(this, mahasiswaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load Data Mahasiswa
        loadMahasiswa();

        // Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(() -> loadMahasiswa());

        // Tambah Mahasiswa
        fabTambah.setOnClickListener(v -> showTambahDialog());
    }



    private void loadMahasiswa() {
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getApiInterface().getMahasiswa()
                .enqueue(new Callback<List<Mahasiswa>>() {
                    @Override
                    public void onResponse(Call<List<Mahasiswa>> call, Response<List<Mahasiswa>> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            mahasiswaList.clear();
                            mahasiswaList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Mahasiswa>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showTambahDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tambah_mahasiswa);

        EditText etNama = dialog.findViewById(R.id.etNama);
        EditText etNim = dialog.findViewById(R.id.etNim);
        EditText etJurusan = dialog.findViewById(R.id.etJurusan);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String nim = etNim.getText().toString().trim();
            String jurusan = etJurusan.getText().toString().trim();

            if (validateInput(nama, nim, jurusan)) {
                tambahMahasiswa(nama, nim, jurusan, dialog);
            }
        });

        dialog.show();
    }

    private boolean validateInput(String nama, String nim, String jurusan) {
        if (nama.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nim.isEmpty() || nim.length() != 10) {
            Toast.makeText(this, "NIM harus 10 digit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (jurusan.isEmpty()) {
            Toast.makeText(this, "Jurusan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void tambahMahasiswa(String nama, String nim, String jurusan, Dialog dialog) {
        ApiClient.getApiInterface().tambahMahasiswa(nama, nim, jurusan)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Berhasil Tambah Mahasiswa", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadMahasiswa();
                        } else {
                            try {
                                Toast.makeText(MainActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Gagal menambah mahasiswa", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}