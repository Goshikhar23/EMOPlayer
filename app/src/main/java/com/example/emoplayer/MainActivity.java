package com.example.emoplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emoplayer.Login.LoginActivity;
import com.example.emoplayer.Music.SongsActivity;
import com.example.emoplayer.Utils.AppPermission;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        getAppPermission();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void getAppPermission() {

        AppPermission appPermission = new AppPermission(this);
        if (!appPermission.checkCameraPermission()) {
            appPermission.requestCameraPermission();
        }
    }

    private void checkUserStatus() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this, SongsActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        finish();
    }
}
