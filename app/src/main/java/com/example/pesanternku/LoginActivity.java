package com.example.pesanternku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailLogin, passwordLogin;
    private Button loginButton;
    private ImageView googleLoginButton;
    private TextView noAccountText;

    private static final int RC_SIGN_IN = 9001; // Request code for Google sign-in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Google Sign-In
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        // Bind UI elements
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.rectangle_3);
        googleLoginButton = findViewById(R.id.icon_google);
        noAccountText = findViewById(R.id.noAccount);

// Buat teks dengan HTML untuk API 21 ke atas
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            // Menggunakan R.string.no_account yang memiliki HTML
            noAccountText.setText(Html.fromHtml(getString(R.string.no_account), Html.FROM_HTML_MODE_LEGACY));
        } else {
            // Untuk versi API lebih rendah
            noAccountText.setText(Html.fromHtml(getString(R.string.no_account)));
        }

// Mendapatkan teks yang sudah diproses dari HTML
        String text = noAccountText.getText().toString();

// Debugging untuk memastikan teks yang dihasilkan sesuai
        Log.d("LoginActivity", "Teks yang ada: " + text);

// Temukan posisi teks "Create Account"
        int startIndex = text.indexOf("Create Account");

// Pastikan teks ditemukan
        if (startIndex != -1) {
            int endIndex = startIndex + "Create Account".length();

            // Membuat SpannableString untuk menambahkan ClickableSpan
            SpannableString spannableString = new SpannableString(text);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // Navigasi ke RegisterActivity
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(android.R.color.holo_blue_dark)); // Warna teks
                    ds.setUnderlineText(false); // Tidak ada garis bawah
                }
            };

            // Terapkan ClickableSpan ke bagian "Create Account"
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Terapkan SpannableString ke TextView
            noAccountText.setText(spannableString);
            noAccountText.setMovementMethod(LinkMovementMethod.getInstance());
            noAccountText.setHighlightColor(Color.TRANSPARENT);
        } else {
            Log.e("LoginActivity", "'Create Account' tidak ditemukan dalam teks.");
        }


        // Set onClickListeners
        loginButton.setOnClickListener(view -> loginWithEmail());
        googleLoginButton.setOnClickListener(view -> signInWithGoogle());

        noAccountText.setOnClickListener(view -> {
            // Redirect to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Login using email and password
    private void loginWithEmail() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            // Show error message if fields are empty
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user
                    }
                });
    }

    // Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Handle result from Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google sign-in failed, handle error
            }
        }
    }

    // Authenticate with Firebase using Google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign-in fails
                    }
                });
    }

    // Update UI based on user
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Go to the next activity if login is successful
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
