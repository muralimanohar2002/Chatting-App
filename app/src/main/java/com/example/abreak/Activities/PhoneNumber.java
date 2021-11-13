package com.example.abreak.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.abreak.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumber extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.phonenum.requestFocus();
        fauth = FirebaseAuth.getInstance();

        //if(fauth.getCurrentUser()!=null){
            //startActivity(new Intent(PhoneNumber.this,MainActivity.class));
        //}
        binding.otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumber.this,OTPaction.class);
                intent.putExtra("number",binding.phonenum.getText().toString());
                startActivity(intent);
            }
        });
    }
}