package com.example.datvu.uschool;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button btnSignIn;
    EditText txtUserName,txtPassWord;
    TextView txtSignUp;

    private FirebaseAuth mAuth;

    ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        addControls();
        addEvents();

    }

    private void addEvents() {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.VISIBLE);
                try {
                    xuLyDangNhap();
                }catch(Exception ex){
                    Toast.makeText(LoginActivity.this,"Vui lòng nhập tài khoản mật khẩu",Toast.LENGTH_SHORT).show();
                    loginProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void xuLyDangNhap() {
        mAuth.signInWithEmailAndPassword(txtUserName.getText().toString(), txtPassWord.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                            sendToMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("loi", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại.",
                                    Toast.LENGTH_SHORT).show();
                            loginProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });

    }

    private void addControls() {
        btnSignIn = findViewById(R.id.btnSignIn);
        txtPassWord = findViewById(R.id.txtPassWord);
        txtUserName = findViewById(R.id.txtUserName);
        txtSignUp =findViewById(R.id.txtSignUp);
        loginProgress = findViewById(R.id.loginProgress);
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
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
