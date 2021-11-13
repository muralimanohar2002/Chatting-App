package com.example.abreak.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(SplashScreen.this,MainActivity.class));
        }
        else{
            startActivity(new Intent(SplashScreen.this,PhoneNumber.class));
        }
    }
}
