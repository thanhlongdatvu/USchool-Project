package com.example.datvu.uschool;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.datvu.support.KiemTra;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText txtLastName, txtFirstName, txtPassWord, txtConfirm, txtEmail, txtPhone;
    Button btnRegister;
    ProgressBar registerProgress;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kiemTraHopLe()) {
                    btnRegister.setEnabled(false);
                    registerProgress.setVisibility(View.VISIBLE);
                    xuLyTaoTaiKhoan();
                }
            }
        });
    }

    private void xuLyTaoTaiKhoan() {
        mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassWord.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            thongTinCaNhan();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("loi", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại.",
                                    Toast.LENGTH_SHORT).show();
                            btnRegister.setEnabled(true);
                            registerProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }

    private void thongTinCaNhan() {
        String userID = mAuth.getCurrentUser().getUid();
        Map<String, String> user = new HashMap<>();
        user.put("firstName", txtFirstName.getText().toString());
        user.put("lastName", txtLastName.getText().toString());
        user.put("phone", txtPhone.getText().toString());

        db.collection("User")
                .document(userID)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Đăng kí thành công.",
                                    Toast.LENGTH_SHORT).show();
                            sendToMain();
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "(FIRESTORE Error) :"+error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }

    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean kiemTraHopLe() {
        if (txtFirstName.getText().toString().equals("") || txtLastName.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(KiemTra.kiemTraEmail(txtEmail.getText().toString()))) {
            Toast.makeText(RegisterActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(KiemTra.KiemTraPhone(txtPhone.getText().toString()))) {
            Toast.makeText(RegisterActivity.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtPassWord.getText().toString().length() < 6) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu phải từ 6 kí tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(txtPassWord.getText().toString().equals(txtConfirm.getText().toString()))) {
            Toast.makeText(RegisterActivity.this, "Vui lòng xác nhận lại mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void addControls() {
        txtConfirm = findViewById(R.id.txtConfirm);
        txtEmail = findViewById(R.id.txtEmail);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtPassWord = findViewById(R.id.txtPassWord);
        txtPhone = findViewById(R.id.txtPhone);
        btnRegister = findViewById(R.id.btnRegister);
        registerProgress = findViewById(R.id.registerProgess);
        db = FirebaseFirestore.getInstance();
    }

}
