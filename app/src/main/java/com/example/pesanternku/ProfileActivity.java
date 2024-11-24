package com.example.pesanternku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailTextView, kembaliTextView;
    private EditText usernameTextView;
    private Button simpanButton, logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi View
        usernameTextView = findViewById(R.id.username_profile);
        emailTextView = findViewById(R.id.email_profile);
        kembaliTextView = findViewById(R.id.kembali);
        simpanButton = findViewById(R.id.simpan_profile);
        logoutButton = findViewById(R.id.logout);

        // Menampilkan data user dari Firestore
        loadUserData();

        // Tombol kembali ke MainActivity
        kembaliTextView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Tombol simpan perubahan username
        simpanButton.setOnClickListener(v -> updateUsername());

        // Tombol logout
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        String email = mAuth.getCurrentUser().getEmail(); // Ambil email pengguna dari FirebaseAuth

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("user")
                .whereEqualTo("email", email) // Query berdasarkan email
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Ambil dokumen pertama (email harus unik)
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        documentId = document.getId(); // Simpan ID dokumen
                        String username = document.getString("username");
                        String emaile = document.getString("email");

                        // Tampilkan di TextView
                        usernameTextView.setText(username != null ? username : "Unknown");
                        emailTextView.setText(emaile != null ? emaile : "Unknown");
                    } else {
                        Log.e("FirestoreError", "Error getting user data: ", task.getException());
                        Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error querying user data: ", e);
                    Toast.makeText(this, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateUsername() {
        String newUsername = usernameTextView.getText().toString().trim(); // Ambil input username baru

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Dokumen tidak ditemukan. Tidak dapat memperbarui username.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update data di Firestore
        DocumentReference docRef = db.collection("user").document(documentId); // Gunakan documentId
        docRef.update("username", newUsername).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Username berhasil diperbarui", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("FirestoreError", "Error updating username", task.getException());
                Toast.makeText(this, "Gagal memperbarui username", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
