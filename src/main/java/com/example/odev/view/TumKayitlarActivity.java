package com.example.odev.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odev.R;
import com.example.odev.model.LabSonucu;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TumKayitlarActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView recyclerView;
    private KayitAdapter adapter;
    private List<LabSonucu> labListesi;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tum_kayitlar);

        db = FirebaseFirestore.getInstance();

        // Arayüz Bağlamaları
        ivBack = findViewById(R.id.ivBack);
        recyclerView = findViewById(R.id.recyclerView);

        // RecyclerView Ayarları (Senin XML'deki spanCount="2" ile uyumlu ızgara yapısı)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        labListesi = new ArrayList<>();
        adapter = new KayitAdapter(this, labListesi);
        recyclerView.setAdapter(adapter);

        // --- GERİ BUTONU ---
        ivBack.setOnClickListener(v -> {
            finish(); // Sayfayı kapatıp bir önceki sayfaya (Laboratuvar) döner
        });

        // Veritabanından kayıtları çek
        kayitlariGetir();
    }

    private void kayitlariGetir() {
        // Tıpkı haritada yaptığımız gibi, LabSonuclari tablosundan tüm verileri çekiyoruz.
        // İlerleyen aşamada modelimize "muhendis_id" eklersek buraya .whereEqualTo() sorgusu atarak
        // sadece o mühendisin girdiği verileri filtreleyebiliriz. Şu an olanları listeliyor.
        db.collection("LabSonuclari")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        labListesi.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LabSonucu labSonucu = document.toObject(LabSonucu.class);
                            labListesi.add(labSonucu);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TumKayitlarActivity.this, "Kayıtlar getirilemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}