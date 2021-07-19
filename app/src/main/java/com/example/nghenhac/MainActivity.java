package com.example.nghenhac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nghenhac.Model.UploadSong;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStrorageref;
    StorageTask mUploadsTask;
    DatabaseReference referenceSongs;
    String songCategory;
    MediaMetadataRetriever mediaMetadataRetriever;
    byte [] art;
    String title1, artist1, album_art1 = "", durations1;
    TextView title, artist, album, durations, dataa;
    ImageView album_art;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewImage = findViewById(R.id.textviewSongsFileselected);
        progressBar = findViewById(R.id.progressbar);
        title = findViewById(R.id.title);
       artist = findViewById(R.id.artist);
        album = findViewById(R.id.album);
        durations = findViewById(R.id.duration);
        dataa = findViewById(R.id.dataa);
        album_art = findViewById(R.id.imagview);

        mediaMetadataRetriever = new MediaMetadataRetriever();
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStrorageref = FirebaseStorage.getInstance().getReference().child("songs");

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        List <String> categories = new ArrayList<>();

        categories.add("Love Songs");
        categories.add("sad Songs");
        categories.add("party Songs");
        categories.add("birthday Songs");
        categories.add("good Songs");

        ArrayAdapter <String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
        songCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this,"Selected"+songCategory,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public  void openAudioFiles (View v) {

        Intent i = new Intent((Intent.ACTION_GET_CONTENT));
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==101 && requestCode == RESULT_OK && data.getData() !=null){
            audioUri = data.getData();
            String fileNames = getFileName(audioUri);
            textViewImage.setText(fileNames);
            mediaMetadataRetriever.setDataSource(this,audioUri);

            art = mediaMetadataRetriever.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            album_art.setImageBitmap(bitmap);
            album.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            dataa.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            durations.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            title.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

            artist1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            durations1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);




        }
    }

    private String getFileName(Uri uri) {

        String result = null;
        if (uri.getScheme().equals("Content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }

            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut !=-1){
                result = result.substring(cut+1);

            }

        }
        return  result;
    }

    public void uploadFileTofirebase (View v){
        if(textViewImage.equals("No file Selected")){
            Toast.makeText(this,"please Selected an image!",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mUploadsTask != null && mUploadsTask.isInProgress()){
                Toast.makeText(this,"songs uploads in allready progress!",Toast.LENGTH_SHORT).show();
                
            }else{
                uploadFiles();
            }
        }
    }

    private void uploadFiles() {

        if(audioUri != null){
            Toast.makeText(this,"Upload please wait!",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final  StorageReference storageReference = mStrorageref.child(System.currentTimeMillis()+"."+getfileetensions(audioUri));
            mUploadsTask = storageReference.putFile(audioUri).addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    UploadSong uploadSong = new UploadSong(songCategory,title1,artist1,album_art1,durations1,uri.toString());
                    String uploadId = referenceSongs.push().getKey();
                    referenceSongs.child(uploadId).setValue(uploadSong);

                });
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull  UploadTask.TaskSnapshot taskSnapshot) {

                    double progess = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progess);

                }
            });
        }else{
            Toast.makeText(this,"No file selected to upload",Toast.LENGTH_SHORT).show();
        }
    }
    private String getfileetensions(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
    public void openAlbumUploadActivity(View v){
        Intent in = new Intent(MainActivity.this,UploadAlbumactivity.class);
        startActivity(in);
    }

}