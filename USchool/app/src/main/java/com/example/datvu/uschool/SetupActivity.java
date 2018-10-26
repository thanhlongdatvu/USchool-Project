package com.example.datvu.uschool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.datvu.support.KiemTra;
import com.example.datvu.support.SupportImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {
    CircleImageView setupImage;
    Toolbar setupToolBar;
    Uri imageUri = null;
    Button btnSave;
    ProgressBar setupProgess;
    EditText txtLastName,txtFirstName,txtPhone;

    boolean isChange = false;
    String userID;

    StorageReference mStorageRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        addControls();
        addEvents();
    }

    private void addEvents() {
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDoiAnh();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(false);
                setupProgess.setVisibility(View.VISIBLE);
                if(imageUri != null && isChange == true){
                    byte[] data = SupportImage.nenAnh(imageUri, SetupActivity.this);
                    StorageReference imagePath = mStorageRef.child("imageUser").child(userID+".jpg");
                    imagePath.putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    capNhatThongTin(downloadUrl.toString());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SetupActivity.this,"cập nhật không thành công",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    if(imageUri != null)
                        capNhatThongTin(imageUri.toString());
                    else
                        capNhatThongTin("");
                }
            }
        });
    }

    private void capNhatThongTin(String downloadUrl)
    {
        String firstname =txtFirstName.getText().toString();
        String phone =txtPhone.getText().toString();
        String lastname =txtLastName.getText().toString();
        if(!TextUtils.isEmpty(firstname) && !TextUtils.isEmpty(lastname) && !TextUtils.isEmpty(phone)){
            if (KiemTra.KiemTraPhone(phone)) {
                Map<String, String> user = new HashMap<>();
                user.put("firstName", firstname);
                user.put("lastName", lastname);
                user.put("phone", phone);
                user.put("image", downloadUrl);
                db.collection("User")
                        .document(userID)
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SetupActivity.this, "Cập nhật thành công"
                                            , Toast.LENGTH_SHORT).show();
                                    setupProgess.setVisibility(View.INVISIBLE);
                                    btnSave.setEnabled(true);
                                    Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            } else {
                Toast.makeText(SetupActivity.this, "Số điện thoại không chính xác"
                        , Toast.LENGTH_SHORT).show();
                setupProgess.setVisibility(View.INVISIBLE);
                btnSave.setEnabled(true);
            }
        }else{
            Toast.makeText(SetupActivity.this,"vui lòng nhập đầy đủ thông tin"
                    ,Toast.LENGTH_SHORT).show();
            setupProgess.setVisibility(View.INVISIBLE);
            btnSave.setEnabled(true);
        }
    }

    private void xuLyDoiAnh() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SetupActivity.this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                setupImage.setImageURI(imageUri);
                isChange = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void addControls() {
        setupImage = findViewById(R.id.setupImage);
        setupToolBar = findViewById(R.id.setupToolBar);
        btnSave = findViewById(R.id.btnSave);
        setupProgess = findViewById(R.id.setupProgress);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtPhone = findViewById(R.id.txtPhone);
        thongTinMacDinh();
    }

    private void thongTinMacDinh() {
        btnSave.setEnabled(false);
        setupProgess.setVisibility(View.VISIBLE);
        userID = mAuth.getCurrentUser().getUid();
        db.collection("User")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                            if(task.getResult().exists()){
                                String lastName = task.getResult().getString("lastName");
                                String  firstName= task.getResult().getString("firstName");
                                String phone = task.getResult().getString("phone");
                                String image = task.getResult().getString("image");

                                txtFirstName.setText(firstName);
                                txtLastName.setText(lastName);
                                txtPhone.setText(phone);
                                if(!TextUtils.isEmpty(image)) {
                                    imageUri = Uri.parse(image);
                                    Glide.with(SetupActivity.this).load(image).into(setupImage);
                                }
                                setupProgess.setVisibility(View.INVISIBLE);
                                btnSave.setEnabled(true);
                            }
                    }
                });
    }
}
