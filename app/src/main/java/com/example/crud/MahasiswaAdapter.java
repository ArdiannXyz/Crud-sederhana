package com.example.crud;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud.API.ApiClient;
import com.example.crud.Model.Mahasiswa;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder> {
    private Context context;
    private List<Mahasiswa> mahasiswaList;
    private OnDataChangedListener dataChangedListener;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public MahasiswaAdapter(Context context, List<Mahasiswa> mahasiswaList) {
        this.context = context;
        this.mahasiswaList = mahasiswaList;
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.dataChangedListener = listener;
    }

    // ViewHolder Class
    public class MahasiswaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvNim, tvJurusan;
        ImageButton btnHapus;

        public MahasiswaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvNim = itemView.findViewById(R.id.tvNim);
            tvJurusan = itemView.findViewById(R.id.tvJurusan);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }

    @NonNull
    @Override
    public MahasiswaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new MahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaViewHolder holder, int position) {
        Mahasiswa mahasiswa = mahasiswaList.get(position);

        holder.tvNama.setText(mahasiswa.getNama());
        holder.tvNim.setText(mahasiswa.getNim());
        holder.tvJurusan.setText(mahasiswa.getJurusan());

        // Hapus Mahasiswa
        holder.btnHapus.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Konfirmasi")
                    .setMessage("Yakin hapus mahasiswa?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        hapusMahasiswa(mahasiswa.getId(), position);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // Update Mahasiswa
        holder.itemView.setOnClickListener(v -> {
            showUpdateDialog(mahasiswa);
        });
    }

    @Override
    public int getItemCount() {
        return mahasiswaList.size();
    }

    private void showUpdateDialog(Mahasiswa mahasiswa) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_tambah_mahasiswa);

        EditText etNama = dialog.findViewById(R.id.etNama);
        EditText etNim = dialog.findViewById(R.id.etNim);
        EditText etJurusan = dialog.findViewById(R.id.etJurusan);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);

        // Set Data Existing
        etNama.setText(mahasiswa.getNama());
        etNim.setText(mahasiswa.getNim());
        etJurusan.setText(mahasiswa.getJurusan());
        btnSimpan.setText("Update");

        btnSimpan.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String nim = etNim.getText().toString().trim();
            String jurusan = etJurusan.getText().toString().trim();

            if (validateInput(nama, nim, jurusan)) {
                updateMahasiswa(mahasiswa.getId(), nama, nim, jurusan, dialog);
            }
        });

        dialog.show();
    }

    private boolean validateInput(String nama, String nim, String jurusan) {
        if (nama.isEmpty()) {
            showToast("Nama tidak boleh kosong");
            return false;
        }
        if (nim.isEmpty() || nim.length() != 10) {
            showToast("NIM harus 10 digit");
            return false;
        }
        if (jurusan.isEmpty()) {
            showToast("Jurusan tidak boleh kosong");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void updateMahasiswa(int id, String nama, String nim, String jurusan, Dialog dialog) {
        ApiClient.getApiInterface().updateMahasiswa(id, nama, nim, jurusan)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            showToast("Berhasil Update Mahasiswa");
                            dialog.dismiss();

                            // Update data di list
                            updateMahasiswaInList(id, nama, nim, jurusan);

                            // Panggil listener jika ada
                            if (dataChangedListener != null) {
                                dataChangedListener.onDataChanged();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showToast("Error: " + t.getMessage());
                    }
                });
    }

    private void updateMahasiswaInList(int id, String nama, String nim, String jurusan) {
        for (Mahasiswa m : mahasiswaList) {
            if (m.getId() == id) {
                m.setNama(nama);
                m.setNim(nim);
                m.setJurusan(jurusan);
                break;
            }
        }
        notifyDataSetChanged();
    }

    private void hapusMahasiswa(int id, int position) {
        ApiClient.getApiInterface().hapusMahasiswa(id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            showToast("Berhasil Hapus Mahasiswa");
                            mahasiswaList.remove(position);
                            notifyItemRemoved(position);

                            // Panggil listener jika ada
                            if (dataChangedListener != null) {
                                dataChangedListener.onDataChanged();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showToast("Error: " + t.getMessage());
                    }
                });
    }

    private void handleErrorResponse(Response<ResponseBody> response) {
        try {
            showToast(response.errorBody() != null
                    ? response.errorBody().string()
                    : "Gagal memproses permintaan");
        } catch (Exception e) {
            showToast("Terjadi kesalahan");
        }
    }

    // Metode tambahan untuk memudahkan manipulasi data

    // Menambah mahasiswa baru ke list
    public void addMahasiswa(Mahasiswa mahasiswa) {
        mahasiswaList.add(mahasiswa);
        notifyItemInserted(mahasiswaList.size() - 1);
    }

    // Update seluruh list
    public void updateList(List<Mahasiswa> newList) {
        mahasiswaList.clear();
        mahasiswaList.addAll(newList);
        notifyDataSetChanged();
    }

    // Mendapatkan item berdasarkan posisi
    public Mahasiswa getItem(int position) {
        if (position >= 0 && position < mahasiswaList.size()) {
            return mahasiswaList.get(position);
        }
        return null;
    }

    // Mendapatkan seluruh list mahasiswa
    public List<Mahasiswa> getMahasiswaList() {
        return mahasiswaList;
    }
}