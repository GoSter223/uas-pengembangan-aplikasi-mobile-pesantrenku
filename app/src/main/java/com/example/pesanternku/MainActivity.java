package com.example.pesanternku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner SP_Provinsi, SP_KabupatenKota, SP_Pesantren;
    private boolean isFilterVisible = false;

    String linkProvinsi = "https://api-pesantren-indonesia.vercel.app/provinsi.json";
    String linkKabupatenKota = "https://api-pesantren-indonesia.vercel.app/kabupaten/{id_provinsi}.json";
    String linkPesantren = "https://api-pesantren-indonesia.vercel.app/pesantren/{id_kab_kota}.json";

    ArrayList<String> listProvinsi = new ArrayList<>();
    ArrayList<String> listProvinsiId = new ArrayList<>();
    ArrayList<String> listKabupatenKota = new ArrayList<>();
    ArrayList<String> listKabupatenId = new ArrayList<>();
    ArrayList<String> listPesantren = new ArrayList<>();

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RelativeLayout filterLayout = findViewById(R.id.filter);
        ImageView filterIcon = findViewById(R.id.filter_icon);

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilterVisible) {
                    filterLayout.setVisibility(View.GONE);
                } else {
                    filterLayout.setVisibility(View.VISIBLE);
                }
                isFilterVisible = !isFilterVisible; // Flip state
            }
        });

        SP_Provinsi = findViewById(R.id.SPProvinsi);
        SP_KabupatenKota = findViewById(R.id.SPKabupatenKota);
        SP_Pesantren = findViewById(R.id.SPPesantren);

        requestQueue = Volley.newRequestQueue(this);

        // Load data provinsi
        loadProvinsi();

        // Handle spinner events
        SP_Provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String idProvinsi = listProvinsiId.get(position);
                loadKabupatenKota(idProvinsi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        SP_KabupatenKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String idKabupaten = listKabupatenId.get(position);
                loadPesantren(idKabupaten);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ImageView personIcon = findViewById(R.id.person_icon);
        personIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProvinsi() {
        ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Mengambil data provinsi...", true);

        // Gunakan JsonArrayRequest karena respons berbentuk JSONArray
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                linkProvinsi,
                null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        listProvinsi.clear();
                        listProvinsiId.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            listProvinsi.add(item.getString("nama"));
                            listProvinsiId.add(item.getString("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listProvinsi);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SP_Provinsi.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Error parsing data provinsi: " + e.getMessage());
                        Toast.makeText(this, "Error parsing data provinsi", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("API_ERROR", "Gagal memuat data provinsi: " + error.getMessage());
                    Toast.makeText(this, "Gagal memuat data provinsi: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    private void loadKabupatenKota(String idProvinsi) {
        String url = linkKabupatenKota.replace("{id_provinsi}", idProvinsi);
        ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Mengambil data kabupaten/kota...", true);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        listKabupatenKota.clear();
                        listKabupatenId.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            listKabupatenKota.add(item.getString("nama"));
                            listKabupatenId.add(item.getString("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listKabupatenKota);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SP_KabupatenKota.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Error parsing data kabupaten/kota: " + e.getMessage());
                        Toast.makeText(this, "Error parsing data kabupaten/kota", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("API_ERROR", "Gagal memuat data kabupaten/kota: " + error.getMessage());
                    Toast.makeText(this, "Gagal memuat data kabupaten/kota: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    private void loadPesantren(String idKabupaten) {
        String url = linkPesantren.replace("{id_kab_kota}", idKabupaten);
        ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Mengambil data pesantren...", true);

        RecyclerView recyclerView = findViewById(R.id.list_pesantren);

        // Data Dummy
        List<Item> pesantrenList = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        listPesantren.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            listPesantren.add(item.getString("nama")); // Ambil nama pesantren
                            pesantrenList.add(new Item(item.getString("nama"), item.getString("alamat"), item.getString("kyai"), "", "", item.getString("id"), item.getJSONObject("kab_kota").getString("id")));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPesantren);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SP_Pesantren.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Error parsing data pesantren: " + e.getMessage());
                        Toast.makeText(this, "Error parsing data pesantren", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("API_ERROR", "Gagal memuat data pesantren: " + error.getMessage());
                    Toast.makeText(this, "Gagal memuat data pesantren: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(getApplicationContext(),pesantrenList));
    }
}
