package com.iicdc.shiningstars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class loginScreen extends AppCompatActivity {

    //Object Declarations
    private EditText mNum, mOTP;
    private Button vrf, sub;
    private ProgressDialog loadingBar;

    //String values
    private String strNum, strOTP;
    private String mVerificationID;

    //Firebase auth calls
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

    }

    private void clickListeners() {

        vrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get input from edit text field (Mobile Number)
                strNum = mNum.getText().toString();

                if (strNum.isEmpty()) {
                    Toast.makeText(loginScreen.this, "Mobile Number is empty", Toast.LENGTH_SHORT).show();
                    mNum.setError("Enter mobile number");
                }
                else {

                    if (strNum.length() != 10) {

                        Toast.makeText(loginScreen.this, "Mobile Number badly formatted see error for details", Toast.LENGTH_SHORT).show();
                        mNum.setError("Mobile number should be of length 10, no spaces are allowed, no need to put country code '+91' or '0' either.");

                    }

                    else {

                        loadingBar.setTitle("Phone Verification");
                        loadingBar.setMessage("Please wait while we authenticate +91" + strNum + ".");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + strNum,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                loginScreen.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks

                    }

                }

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOTP = mOTP.getText().toString();

                if (strOTP.isEmpty()) {
                    Toast.makeText(loginScreen.this, "Please enter otp", Toast.LENGTH_SHORT).show();
                    mOTP.setError("Please provide otp");
                }
                else {

                    loadingBar.setTitle("OTP Verification");
                    loadingBar.setMessage("Please wait while we authenticate provided OTP.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, strOTP);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(loginScreen.this, "" + e, Toast.LENGTH_SHORT).show();

                //Revert back state if phone authentication step failed
                mNum.setVisibility(View.VISIBLE);
                vrf.setVisibility(View.VISIBLE);

                mOTP.setVisibility(View.INVISIBLE);
                sub.setVisibility(View.INVISIBLE);

                loadingBar.dismiss();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationID = verificationId;
                mResendToken = token;

                Toast.makeText(loginScreen.this, "Code has been sent to " + "+91" + strNum, Toast.LENGTH_SHORT).show();
                //Invisible both the fields when phone number is verified and jump to otp verification
                mNum.setVisibility(View.INVISIBLE);
                vrf.setVisibility(View.INVISIBLE);

                mOTP.setVisibility(View.VISIBLE);
                sub.setVisibility(View.VISIBLE);

            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();

                        } else {

                            Toast.makeText(loginScreen.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();

                            //Revert back state if phone authentication step failed
                            mNum.setVisibility(View.VISIBLE);
                            vrf.setVisibility(View.VISIBLE);

                            mOTP.setVisibility(View.INVISIBLE);
                            sub.setVisibility(View.INVISIBLE);

                            loadingBar.dismiss();

                        }
                    }
                });
    }

    private void initFields() {

        //EditText plugin from layout
        mNum = findViewById(R.id.m_num);
        mOTP = findViewById(R.id.otp);

        //Buttons plugin form layout
        vrf = findViewById(R.id.verify);
        sub = findViewById(R.id.submit);

        //Initializing firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //loading bar while authentication process
        loadingBar = new ProgressDialog(loginScreen.this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Initialize objects
        initFields();


        //On click listeners
        clickListeners();
    }
}