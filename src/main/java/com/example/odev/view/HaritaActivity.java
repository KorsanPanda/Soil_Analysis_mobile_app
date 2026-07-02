package com.example.odev.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odev.R;
import com.example.odev.model.Tarla;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HaritaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private com.google.android.material.button.MaterialButton btnSaveTarla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harita);

        // Firebase Başlatma
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Arayüz Bağlamaları (XML ile tam uyumlu)
        btnSaveTarla = findViewById(R.id.btnSaveTarla);

        // Haritayı Çağırma
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Artı (+) Butonuna Tıklama Olayı -> YeniTarlaActivity sayfasına gidiş
        btnSaveTarla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HaritaActivity.this, YeniTarlaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Harita tipini uydu görünümü (Satellite) yapıyoruz
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Veritabanından giriş yapan kullanıcının tarlalarını çekip iğneliyoruz
        kullaniciTarlalariniGetir();

        // Haritadaki iğnelere (Marker) Tıklama Olayı -> TarlaDetayActivity sayfasına gidiş
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Tıklanan iğnenin içine gizlediğimiz parsel_id'yi alıyoruz
                String secilenParselId = (String) marker.getTag();

                if (secilenParselId != null) {
                    Intent intent = new Intent(HaritaActivity.this, TarlaDetayActivity.class);

                    // Seçilen parselin ID'sini detay sayfasına paketleyip gönderiyoruz
                    intent.putExtra("PARSEL_ID", secilenParselId);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void kullaniciTarlalariniGetir() {
        // Eğer kullanıcı giriş yapmamışsa (null ise) çökmemesi için güvenlik kontrolü
        if (mAuth.getCurrentUser() == null) return;

        String gecerliKullaniciId = mAuth.getCurrentUser().getUid();

        // Sadece giriş yapan kullanıcının ID'sine sahip tarlaları getir
        db.collection("Tarlalar")
                .whereEqualTo("kullanici_id", gecerliKullaniciId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Tarla tarla = document.toObject(Tarla.class);
                            if (tarla != null) {
                                // Tarlanın enlem ve boylamını LatLng objesine dönüştürüyoruz
                                LatLng tarlaKonumu = new LatLng(tarla.getEnlem(), tarla.getBoylam());

                                // Haritaya iğneyi (Marker) ekliyoruz
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(tarlaKonumu)
                                        .title(tarla.getIlce() + " Tarlası")
                                        .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN)));

                                // İğnenin içine parsel_id'yi etiket (Tag) olarak yapıştırıyoruz
                                if (marker != null) {
                                    marker.setTag(tarla.getParsel_id());
                                }

                                // Kamerayı son eklenen tarlanın üzerine yakınlaştırıyoruz
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tarlaKonumu, 15f));
                            }
                        }
                    } else {
                        Toast.makeText(HaritaActivity.this, "Tarlalar yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}