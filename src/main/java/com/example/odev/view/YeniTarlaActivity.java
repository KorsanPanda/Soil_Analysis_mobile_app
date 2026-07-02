package com.example.odev.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odev.R;
import com.example.odev.model.Tarla;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID; // Eşsiz ID üretmek için eklendi

public class YeniTarlaActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etTarlaSahibi, etParselId, etIlIlce, etEnlem, etBoylam;
    private MaterialButton btnSaveTarla;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_tarla);

        // Firebase Başlatma
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Arayüz Bağlamaları
        ivBack = findViewById(R.id.ivBack);
        etTarlaSahibi = findViewById(R.id.etTarlaSahibi);
        etParselId = findViewById(R.id.etParselId);
        etIlIlce = findViewById(R.id.etIlIlce);
        etEnlem = findViewById(R.id.etEnlem);
        etBoylam = findViewById(R.id.etBoylam);
        btnSaveTarla = findViewById(R.id.btnSaveTarla);

        // --- OTOMATİK VERİ DOLDURMA İŞLEMLERİ ---
        if (mAuth.getCurrentUser() != null) {
            // Firebase'den ismi çek
            String kullaniciAdi = mAuth.getCurrentUser().getDisplayName();

            // İsim boşsa e-postayı al ve "@" işaretinden böl
            if (kullaniciAdi == null || kullaniciAdi.isEmpty()) {
                String tamEmail = mAuth.getCurrentUser().getEmail();
                if (tamEmail != null && tamEmail.contains("@")) {
                    kullaniciAdi = tamEmail.split("@")[0]; // "@" işaretinden böler ve sadece ilk kısmı alır
                } else {
                    kullaniciAdi = tamEmail;
                }
            }
            etTarlaSahibi.setText(kullaniciAdi);
        }

        // 8 Haneli Rastgele ve Eşsiz bir Parsel ID oluştur
        String otomatikParselId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        etParselId.setText(otomatikParselId);


        // --- GERİ BUTONU ---
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // --- KAYDET BUTONU ---
        btnSaveTarla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tarlaKaydet();
            }
        });
    }

    private void tarlaKaydet() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Hata: Kullanıcı oturumu bulunamadı!", Toast.LENGTH_SHORT).show();
            return;
        }

        String kullaniciId = mAuth.getCurrentUser().getUid();

        // Kutulardaki metinleri alıyoruz
        String parselId = etParselId.getText().toString().trim();
        String ilIlce = etIlIlce.getText().toString().trim();
        String enlemStr = etEnlem.getText().toString().trim();
        String boylamStr = etBoylam.getText().toString().trim();

        if (ilIlce.isEmpty() || enlemStr.isEmpty() || boylamStr.isEmpty()) {
            Toast.makeText(this, "Lütfen il/ilçe ve koordinat bilgilerini doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        String il = ilIlce;
        String ilce = "";
        if (ilIlce.contains("/")) {
            String[] parcalar = ilIlce.split("/");
            il = parcalar[0].trim();
            if (parcalar.length > 1) {
                ilce = parcalar[1].trim();
            }
        }

        double enlem = 0.0;
        double boylam = 0.0;
        try {
            enlem = Double.parseDouble(enlemStr);
            boylam = Double.parseDouble(boylamStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lütfen koordinatları geçerli bir sayı formatında girin.", Toast.LENGTH_SHORT).show();
            return;
        }

        Tarla yeniTarla = new Tarla(parselId, il, ilce, enlem, boylam, kullaniciId);

        db.collection("Tarlalar").document(parselId)
                .set(yeniTarla)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(YeniTarlaActivity.this, "Tarla başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(YeniTarlaActivity.this, "Hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}