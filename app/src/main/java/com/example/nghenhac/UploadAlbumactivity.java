package com.example.nghenhac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nghenhac.Model.Constants;
import com.example.nghenhac.Model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadAlbumactivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChose;
    private Button buttononUpload;
    private EditText edittextName;
    private ImageView imageview;

    String songCategory;
    private static final int PICK_IMAGE_REQUESt = 234;
    private Uri fileFath;
    StorageReference storageReference;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_albumactivity);

        buttonChose = findViewById(R.id.buttonChose);
        buttononUpload = findViewById(R.id.buttonUpload);
        edittextName = findViewById(R.id.edit_text);
        imageview = findViewById(R.id.imagview);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        Spinner spinner = findViewById(R.id.spinner);

        buttonChose.setOnClickListener(this);
        buttononUpload.setOnClickListener(this);

        List<String> categories = new ArrayList<>();

        categories.add("Love Songs");
        categories.add("sad Songs");
        categories.add("party Songs");
        categories.add("birthday Songs");
        categories.add("good Songs");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                songCategory = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UploadAlbumactivity.this,"selected: "+songCategory,Toast.LENGTH_SHORT).show();
;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view == buttonChose){
            showFileChose();
            
        }
        else if(view == buttononUpload){
            uploadFie();
        }

    }

    private void uploadFie() {
        if (fileFath!= null){
            ProgressDialog progressDialog = new ProgressDialog(this );
            progressDialog.setTitle("uploading...");
            progressDialog.show();
            final  StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS+System.currentTimeMillis()+"."+ getFileExtension(fileFath));
            sRef.putFile(fileFath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = uri.toString();
                            Upload upload = new Upload(edittextName.getText().toString().trim(),
                                    url, songCategory);
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);
                            progressDialog.dismiss();
                            Toast.makeText(UploadAlbumactivity.this,"File upload ...", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(UploadAlbumactivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull  UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(" Uploaded "+((int)progress)+"%....");
                }
            });

        }
    }

    private void showFileChose() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture "),PICK_IMAGE_REQUESt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUESt && requestCode == RESULT_OK && data == null && data.getData() != null){
            fileFath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileFath);
                imageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String getFileExtension (Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType((cr.getType(uri)));
    }
}