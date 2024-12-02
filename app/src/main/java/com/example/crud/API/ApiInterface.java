package com.example.crud.API;


import com.example.crud.Model.Mahasiswa;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("get_mahasiswa.php")
    Call<List<Mahasiswa>> getMahasiswa();

    @FormUrlEncoded
    @POST("tambah_mahasiswa.php")
    Call<ResponseBody> tambahMahasiswa(
            @Field("nama") String nama,
            @Field("nim") String nim,
            @Field("jurusan") String jurusan
    );

    @FormUrlEncoded
    @POST("update_mahasiswa.php")
    Call<ResponseBody> updateMahasiswa(
            @Field("id") int id,
            @Field("nama") String nama,
            @Field("nim") String nim,
            @Field("jurusan") String jurusan
    );

    @FormUrlEncoded
    @POST("hapus_mahasiswa.php")
    Call<ResponseBody> hapusMahasiswa(
            @Field("id") int id
    );
}
