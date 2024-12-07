package com.example.pesanternku;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private CommentAdapter commentAdapter;
    private List<ItemComment> commentList;
    private RecyclerView recyclerView;

    private TextView namaPesantren, alamatPesantren, namaKyai;
    private EditText commentField;

    private String username; // Username pengguna saat ini
    private String emailUser;
    private String idKota;   // ID Kota dari Intent
    private String idPesantren; // ID Pesantren dari API

    private String linkPesantren = "https://api-pesantren-indonesia.vercel.app/pesantren/{idKota}.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Firebase instance
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.list_coment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentField = findViewById(R.id.comment_field);

        namaPesantren = findViewById(R.id.nama_pesantren);
        alamatPesantren = findViewById(R.id.alamat_pesantren);
        namaKyai = findViewById(R.id.nama_kyai);

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        // Dapatkan ID Kota dari Intent
        idKota = getIntent().getStringExtra("idKota");
        // Dapatkan ID Pesantren dari Intent
        idPesantren = getIntent().getStringExtra("idPesantren");
        if (idKota == null) {
            Toast.makeText(this, "ID Kota tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil username pengguna saat ini
        fetchUsername();

        // Ambil ID Pesantren dari API
        linkPesantren = linkPesantren.replace("{idKota}", idKota);
        fetchPesantrenData(linkPesantren);

        // Tombol Kirim Komentar
        findViewById(R.id.btn_send_comment).setOnClickListener(v -> addComment());
    }

    private void fetchUsername() {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        if (currentUserEmail != null) {
            firestore.collection("user")
                    .whereEqualTo("email", currentUserEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            username = document.getString("username");
                            emailUser = document.getString("email");
                        } else {
                            Log.e(TAG, "Gagal mendapatkan username", task.getException());
                        }
                    });
        }
    }

    private void fetchPesantrenData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                // Loop untuk mencari pesantren berdasarkan ID
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject pesantren = response.getJSONObject(i);

                                    // Ambil ID pesantren dari API dan bandingkan dengan idPesantren
                                    String pesantrenId = pesantren.getString("id");
                                    if (pesantrenId.equals(idPesantren)) {
                                        // Ambil data dari objek JSON jika ID pesantren cocok
                                        String nama = pesantren.getString("nama");
                                        String kyai = pesantren.getString("kyai");
                                        String alamat = pesantren.getString("alamat");

                                        JSONObject kabKota = pesantren.getJSONObject("kab_kota");
                                        String namaKabKota = kabKota.getString("nama");

                                        JSONObject provinsi = pesantren.getJSONObject("provinsi");
                                        String namaProvinsi = provinsi.getString("nama");

                                        // Set data ke TextView
                                        namaPesantren.setText(nama);
                                        namaKyai.setText(kyai);
                                        alamatPesantren.setText(alamat + ", " + namaKabKota + ", " + namaProvinsi);

                                        // Simulasikan ID pesantren dari API
                                        idPesantren = pesantren.getString("id");
                                        fetchComments(); // Ambil komentar setelah ID pesantren diketahui
                                        break; // Hentikan loop setelah menemukan pesantren yang sesuai
                                    }
                                }
                            } else {
                                Toast.makeText(CommentActivity.this,
                                        "Tidak ada data pesantren untuk kota ini.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CommentActivity.this,
                                    "Gagal memproses data!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentActivity.this,
                                "Gagal mendapatkan data!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }


    private void fetchComments() {
        if (idPesantren == null) {
            Log.e(TAG, "ID Pesantren tidak tersedia.");
            return;
        }

        firestore.collection("comment")
                .whereEqualTo("idPesantren", idPesantren)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idComment = document.getId();
                            String username = document.getString("username");
                            String comment = document.getString("comment");
                            String emailComment = document.getString("email");
                            commentList.add(new ItemComment(idComment, username, comment, emailUser, emailComment));
                        }
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Gagal memuat komentar", task.getException());
                    }
                });
    }

    private void addComment() {
        String commentText = commentField.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username == null || idPesantren == null) {
            Toast.makeText(this, "Gagal menambahkan komentar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat komentar baru
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("email", emailUser);
        commentData.put("username", username);
        commentData.put("comment", commentText);
        commentData.put("idPesantren", idPesantren);

        firestore.collection("comment")
                .add(commentData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Komentar berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    commentField.setText("");
                    fetchComments();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menambahkan komentar", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Gagal menambahkan komentar", e);
                });
    }
}
