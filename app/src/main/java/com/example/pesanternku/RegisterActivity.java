package com.example.pesanternku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameRegist, emailRegist, passwordRegist, confirmPasswordRegist;
    private Button registerButton;

    private TextView haveAccount;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        usernameRegist = findViewById(R.id.usernameRegist);
        emailRegist = findViewById(R.id.emailRegist);
        passwordRegist = findViewById(R.id.passwordRegist);
        confirmPasswordRegist = findViewById(R.id.confirmPasswordRegist);
        registerButton = findViewById(R.id.register);
        haveAccount = findViewById(R.id.haveAccount);
        // Buat teks dengan HTML untuk API 21 ke atas
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            haveAccount.setText(Html.fromHtml(getString(R.string.have_account), Html.FROM_HTML_MODE_LEGACY));
        } else {
            haveAccount.setText(Html.fromHtml(getString(R.string.have_account)));
        }

// Membuat bagian teks "Log In" dapat diklik
        SpannableString spannableString = new SpannableString(haveAccount.getText());

// Temukan posisi teks "Log In"
        String text = haveAccount.getText().toString();
        int startIndex = text.indexOf("Log In");
        int endIndex = startIndex + "Log In".length();

// Tambahkan ClickableSpan ke bagian "Log In"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Navigasi ke LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Terapkan SpannableString ke TextView
        haveAccount.setText(spannableString);
        haveAccount.setMovementMethod(LinkMovementMethod.getInstance());
        haveAccount.setHighlightColor(Color.TRANSPARENT);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Ensure "user" collection exists
        ensureUserCollectionExists();
    }

    private void registerUser() {
        String username = usernameRegist.getText().toString().trim();
        String email = emailRegist.getText().toString().trim();
        String password = passwordRegist.getText().toString().trim();
        String confirmPassword = confirmPasswordRegist.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            usernameRegist.setError("Username is required!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailRegist.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordRegist.setError("Password is required!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordRegist.setError("Passwords do not match!");
            return;
        }

        // Create user in Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserToFirestore(username, email, password);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String username, String email, String password) {
        // Create user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);

        // Save to Firestore
        db.collection("user").add(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error saving user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ensureUserCollectionExists() {
        CollectionReference userCollection = db.collection("user");
        userCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                Toast.makeText(this, "Initializing user collection...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close RegisterActivity
    }
}