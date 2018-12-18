package com.fauzanpramulia.tbpemob;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fauzanpramulia.tbpemob.model.MahasiswaItems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    @BindView(R.id.editBp) EditText editBp;
    @BindView(R.id.editNama) EditText editNama;
    @BindView(R.id.editMataKuliah) EditText editMataKuliah;
    @BindView(R.id.editKelas) EditText editKelas;
    @BindView(R.id.button2) Button ambilGambar;
    @BindView(R.id.save) Button buttonSave;
    @BindView(R.id.imageViewSaja)
    ImageView viewSaja;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    protected static final int CAMERA_REQUEST = 0;
    private static final String SERVER_PATH = "Path_to_your_server";
    private Uri uri;
    RequestBody filename,bp,nama,kelas,mata_kuliah;
    MultipartBody.Part fileToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);

        ambilGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startDialog();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()){
                    if (checkData()){
                        progressBar.setVisibility(View.VISIBLE);
                        insertAbsen();
                    }
                }else{
                    Toast.makeText(InsertActivity.this, "Jaringan tidak ada, Aktifkan jaringan dahulu!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void insertAbsen(){
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter
        String API_BASE_URL = "http://192.168.1.6:8000/api/";

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL) //Setting the Root URL
                .addConverterFactory(GsonConverterFactory.create())
                .build(); //Finally building the adapter

        bp = RequestBody.create( okhttp3.MultipartBody.FORM, editBp.getText().toString());
        nama = RequestBody.create(okhttp3.MultipartBody.FORM, editNama.getText().toString());
        kelas = RequestBody.create(okhttp3.MultipartBody.FORM, editKelas.getText().toString());
        mata_kuliah = RequestBody.create(okhttp3.MultipartBody.FORM, editMataKuliah.getText().toString());
        //Creating object for our interface
        AbsensiClient api = adapter.create(AbsensiClient.class);

        api.insertAbsen(
                bp,
                nama,
                kelas,
                mata_kuliah,
                fileToUpload,
                filename
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(InsertActivity.this, "Data Berhasil Dimasukkan", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(InsertActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    Log.i("Yes", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Oh no", "Unable to submit post to API.");
                progressBar.setVisibility(View.INVISIBLE);
            }

        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, InsertActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK){
            uri = data.getData();
            Bitmap bitmapImage = null;
            try {
                bitmapImage = decodeBitmap(uri );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            viewSaja.setImageBitmap(bitmapImage);
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, InsertActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "Filename " + file.getName());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            }else{
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else if(requestCode == CAMERA_REQUEST){
            Toast.makeText(this, "From camera", Toast.LENGTH_SHORT).show();
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            Bitmap bitmap = null;
            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


            } catch (Exception e) {
                e.printStackTrace();
            }
            viewSaja.setImageBitmap(bitmap);
            Log.d(TAG, "Filename " + f.getName());
//            RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), f);
            fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), mFile);
            filename = RequestBody.create(MediaType.parse("text/plain"), f.getName());
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(uri != null){
            String filePath = getRealPathFromURIPath(uri, InsertActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    public  Bitmap decodeBitmap(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                        openGalleryIntent.setType("image/*");
                        startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
//                        Intent pictureActionIntent = null;
//                        pictureActionIntent = new Intent(
//                                Intent.ACTION_PICK,
//                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(
//                                pictureActionIntent,
//                                GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        startActivityForResult(intent,
                                CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    public boolean checkData(){
        if(TextUtils.isEmpty(editBp.getText().toString().trim())) {
            editBp.setError("This field is required");
            return false;
        }
        if(TextUtils.isEmpty(editNama.getText().toString().trim())) {
            editNama.setError("This field is required");
            return false;
        }
        if(TextUtils.isEmpty(editKelas.getText().toString().trim())) {
            editKelas.setError("This field is required");
            return false;
        }
        if(TextUtils.isEmpty(editMataKuliah.getText().toString().trim())) {
            editMataKuliah.setError("This field is required");
            return false;
        }
        if(viewSaja.getDrawable()==null) {
            Toast.makeText(this, "Ambil Foto Dulu!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
