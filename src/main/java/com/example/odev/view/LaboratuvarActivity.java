package com.example.odev.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odev.R;
import com.example.odev.model.LabSonucu;
import com.example.odev.model.Mahsul;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class LaboratuvarActivity extends AppCompatActivity {

    private EditText etParselId, etPh, etOrganik, etFosfor, etPotasyum;
    private EditText etMahsul1, etVerim1, etMahsul2, etVerim2, etMahsul3, etVerim3;
    private MaterialButton buttonKaydet, buttonAllRecords;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratuvar);

        // Firebase Başlatma
        db = FirebaseFirestore.getInstance();

        // Arayüz Bağlamaları
        etParselId = findViewById(R.id.etParselId);
        etPh = findViewById(R.id.etPh);
        etOrganik = findViewById(R.id.etOrganik);
        etFosfor = findViewById(R.id.etFosfor);
        etPotasyum = findViewById(R.id.etPotasyum);

        etMahsul1 = findViewById(R.id.etMahsul1);
        etVerim1 = findViewById(R.id.etVerim1);
        etMahsul2 = findViewById(R.id.etMahsul2);
        etVerim2 = findViewById(R.id.etVerim2);
        etMahsul3 = findViewById(R.id.etMahsul3);
        etVerim3 = findViewById(R.id.etVerim3);

        buttonKaydet = findViewById(R.id.buttonKaydet);
        buttonAllRecords = findViewById(R.id.buttonAllRecords);

        // --- TÜM KAYITLAR BUTONU ---
        buttonAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaboratuvarActivity.this, TumKayitlarActivity.class);
                startActivity(intent);
            }
        });

        // --- KAYDET BUTONU ---
        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verileriKaydet();
            }
        });
    }

    private void verileriKaydet() {
        // Kutulardaki metinleri alıyoruz
        String parselIdStr = etParselId.getText().toString().trim();
        String phStr = etPh.getText().toString().trim();
        String organikStr = etOrganik.getText().toString().trim();
        String fosforStr = etFosfor.getText().toString().trim();
        String potasyumStr = etPotasyum.getText().toString().trim();

        // Temel laboratuvar verileri boş mu kontrolü
        if (parselIdStr.isEmpty() || phStr.isEmpty() || organikStr.isEmpty() || fosforStr.isEmpty() || potasyumStr.isEmpty()) {
            Toast.makeText(this, "Lütfen Parsel ID ve tüm Laboratuvar Sonuçlarını doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Metinleri ondalıklı sayılara (Double) çeviriyoruz
        double ph, organik, fosfor, potasyum;
        try {
            ph = Double.parseDouble(phStr);
            organik = Double.parseDouble(organikStr);
            fosfor = Double.parseDouble(fosforStr);
            potasyum = Double.parseDouble(potasyumStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lütfen sayısal değerleri doğru formatta girin (Örn: 5.5).", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bu laboratuvar kaydı için benzersiz rastgele bir ID oluşturuyoruz
        String sonucId = UUID.randomUUID().toString();

        // 1. ADIM: Laboratuvar Sonucunu Modelimize Döküp Firestore'a Gönderme
        LabSonucu yeniLabSonucu = new LabSonucu(sonucId, parselIdStr, ph, organik, fosfor, potasyum);

        db.collection("LabSonuclari").document(sonucId)
                .set(yeniLabSonucu)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Lab verisi başarıyla kaydedildi, şimdi mahsulleri kaydedelim
                            mahsulleriKaydet(sonucId);
                        } else {
                            Toast.makeText(LaboratuvarActivity.this, "Kayıt sırasında hata oluştu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mahsulleriKaydet(String sonucId) {
        // XML'deki 3 satır mahsul verisini alıp veritabanına tek tek yollayacağız
        kaydetTekilMahsul(etMahsul1.getText().toString().trim(), etVerim1.getText().toString().trim(), sonucId);
        kaydetTekilMahsul(etMahsul2.getText().toString().trim(), etVerim2.getText().toString().trim(), sonucId);
        kaydetTekilMahsul(etMahsul3.getText().toString().trim(), etVerim3.getText().toString().trim(), sonucId);

        Toast.makeText(LaboratuvarActivity.this, "Tüm veriler başarıyla kaydedildi!", Toast.LENGTH_LONG).show();
        alanlariTemizle();
    }

    private void kaydetTekilMahsul(String mahsulAd, String verimStr, String sonucId) {
        // Eğer satır boş bırakılmışsa kaydetme işlemini atla
        if (mahsulAd.isEmpty() || verimStr.isEmpty()) {
            return;
        }

        double verim;
        try {
            verim = Double.parseDouble(verimStr);
        } catch (NumberFormatException e) {
            return; // Verim rakam değilse o satırı atla
        }

        // Mahsul için benzersiz ID oluşturuyoruz
        String oneriId = UUID.randomUUID().toString();
        Mahsul yeniMahsul = new Mahsul(oneriId, mahsulAd, verim, sonucId);

        // Firestore "Mahsul" tablosuna gönder
        db.collection("Mahsul").document(oneriId).set(yeniMahsul);
    }

    private void alanlariTemizle() {
        // Kayıt sonrası ekranı yeni bir işlem için sıfırla
        etParselId.setText("");
        etPh.setText("");
        etOrganik.setText("");
        etFosfor.setText("");
        etPotasyum.setText("");
        etMahsul1.setText(""); etVerim1.setText("");
        etMahsul2.setText(""); etVerim2.setText("");
        etMahsul3.setText(""); etVerim3.setText("");
    }
}