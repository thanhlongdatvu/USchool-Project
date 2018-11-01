package com.example.datvu.uschool;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {
    Toolbar mainToolBar;
    FloatingActionButton btnfaPost;
    private BottomNavigationView mainbottomNav;

    HomeFragment homeFragment;
    NotificationFragment notificationFragment;
    AccountFragment accountFragment;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    boolean begin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth =FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null) {
            addControls();
            addEvents();
        }
    }

    private void addEvents() {
        btnfaPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PostActivity.class);
                startActivity(intent);
            }
        });

        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);

                switch (item.getItemId()){
                    case R.id.bottomActionHome :
                        replaceFragment(homeFragment,currentFragment);
                        return true;
                    case R.id.bottomActionNotification :
                        replaceFragment(notificationFragment,currentFragment);
                        return true;
                    case R.id.bottomActionAccount :
                        replaceFragment(accountFragment,currentFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void addControls() {
        mainToolBar = findViewById(R.id.mainToolBar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle("CLASS NAME");
        btnfaPost = findViewById(R.id.btnfaPost);
        mainbottomNav = findViewById(R.id.mainBottomNav);
        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        initializeFragment();

        if(begin == true){
            db.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String postition = task.getResult().getString("position");
                    if(TextUtils.isEmpty(postition)){
                        btnfaPost.setEnabled(false);
                        btnfaPost.setVisibility(View.INVISIBLE);
                    }
                }
            });
            begin = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionLogout:
                logOut();
                return true;
            case R.id.actionSettings:
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.actionSchedule:
                Intent scheduleIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(scheduleIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);
        }

        if(fragment == accountFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);
        }

        if(fragment == notificationFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);
        }
        fragmentTransaction.show(fragment);

        fragmentTransaction.commit();

    }


    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.mainContainer, homeFragment);
        fragmentTransaction.add(R.id.mainContainer, notificationFragment);
        fragmentTransaction.add(R.id.mainContainer, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }
}
