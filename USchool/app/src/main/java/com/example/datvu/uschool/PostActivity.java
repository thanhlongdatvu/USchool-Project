package com.example.datvu.uschool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.datvu.model.Post;
import com.example.datvu.support.SupportImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity {
    private static final int MAX_LENGTH =  100;
    Toolbar postToolbar;
    EditText txtPost;
    Button btnPost;
    ImageView postImage;
    ProgressBar postProgress;

    String userID;
    Uri imageUri = null;

    StorageReference mStorageRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addControls();
        addEvents();
    }

    private void addEvents() {
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDoiAnh();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(txtPost.getText().toString()) && imageUri != null){
                    postProgress.setVisibility(View.VISIBLE);
                    btnPost.setEnabled(false);
                    final String randomName = random();
                    final StorageReference filePath = mStorageRef.child("postImage").child(randomName+".jpg");
                    filePath.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    byte[] data = SupportImage.nenAnh(imageUri, PostActivity.this);

                                    UploadTask uploadTask = mStorageRef.child("postImage/Compression")
                                            .child(randomName + ".jpg").putBytes(data);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri downloadcompressUrl = taskSnapshot.getDownloadUrl();
                                            taoThongTinBaiDang(downloadUrl,downloadcompressUrl);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(PostActivity.this,"Không thành công",
                                            Toast.LENGTH_SHORT).show();
                                    postProgress.setVisibility(View.INVISIBLE);
                                    btnPost.setEnabled(true);
                                }
                            });
                }
                else{
                    Toast.makeText(PostActivity.this,"vui lòng chọn hình và viết mô tả",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void taoThongTinBaiDang(Uri downloadUrl, Uri downloadcompressUrl) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("image",downloadUrl.toString());
        postMap.put("imageCompress",downloadcompressUrl.toString());
        postMap.put("description",txtPost.getText().toString());
        postMap.put("timestamp",FieldValue.serverTimestamp());
        postMap.put("userID",userID);

        db.collection("Post")
                .add(postMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PostActivity.this,"Đăng bài thành công"
                                    ,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PostActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(PostActivity.this,"Không thành công",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addControls() {
        postToolbar = findViewById(R.id.postToolbar);
        setSupportActionBar(postToolbar);
        getSupportActionBar().setTitle("Đăng bài");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtPost = findViewById(R.id.txtPost);
        btnPost = findViewById(R.id.btnPost);
        postImage = findViewById(R.id.postImage);
        postProgress = findViewById(R.id.postProgess);
        userID = mAuth.getCurrentUser().getUid();
    }
    private void xuLyDoiAnh() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(PostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PostActivity.this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(PostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(PostActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                postImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
