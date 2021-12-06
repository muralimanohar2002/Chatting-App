package com.example.abreak.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.abreak.databinding.ActivityOTPactionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPaction extends AppCompatActivity {

    ActivityOTPactionBinding binding;
    FirebaseAuth fauth;
    String verifyId;
    ProgressDialog dial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOTPactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fauth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        dial = new ProgressDialog(this);
        dial.setMessage("Sending OTP...");
        dial.setCancelable(false);
        dial.show();

        String number = getIntent().getStringExtra("number");
        binding.numView.setText("Verify " + number);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fauth)
                .setPhoneNumber("+91"+number)
                .setActivity(OTPaction.this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                        fauth.signInWithCredential(phoneAuthCredential);
                        Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d("OtpAction", "onVerificationFailed: " + e.getMessage());
                        dial.dismiss();
                    }

                    @Override
                    public void onCodeSent(@NonNull String Id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(Id, forceResendingToken);
                        dial.dismiss();
                        verifyId = Id;

                        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                        binding.otpView.requestFocus();
                    }
                }).build();

                PhoneAuthProvider.verifyPhoneNumber(options);

                binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
                    @Override
                    public void onOtpCompleted(String otp) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId,otp);

                        fauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(OTPaction.this, "Registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(OTPaction.this, profile.class));
                                    finishAffinity();
                                }
                                else{
                                    Toast.makeText(OTPaction.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }
}