package com.fauzanpramulia.tbpemob;

import com.fauzanpramulia.tbpemob.model.MahasiswaItems;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AbsensiClient {
    @GET("absensi")
    Call<List<MahasiswaItems>> getAbsensi();

//    @FormUrlEncoded
    @Multipart
    @POST("absensi/insert")
    Call<ResponseBody> insertAbsen(
            @Part("bp") RequestBody bp,
            @Part("nama") RequestBody nama,
            @Part("kelas") RequestBody kelas,
            @Part("mata_kuliah") RequestBody mata_kuliah,
            @Part MultipartBody.Part filePart,
            @Part("name") RequestBody foto);

//    @POST("my/files/photo/")
//    Call<FileUploadResponse> uploadPhoto(@Header("Content-Type") String contentType,
//                                         @Header("Authorization") String auth,
//                                         @Body MultipartBody body);
}
