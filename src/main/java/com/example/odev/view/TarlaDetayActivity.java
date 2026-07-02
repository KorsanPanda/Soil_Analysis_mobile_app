package com.example.odev.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.odev.R;
import com.example.odev.model.LabSonucu;
import com.example.odev.model.Mahsul;
import com.example.odev.model.Tarla;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TarlaDetayActivity extends AppCompatActivity {

    private ImageView ivBack;
    private MaterialButton btnDelete;
    private TextView tvHeaderTitle, tvEnlemValue, tvBoylamValue, tvIlIlceValue;
    private TextView tvPhValue, tvOrganikValue, tvFosforValue, tvPotasyumValue;
    private TextView tvMahsul1, tvVerim1, tvMahsul2, tvVerim2, tvMahsul3, tvVerim3;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // Auth'u buraya tanımladık
    private String gelenParselId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarla_detay);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance(); // Auth'u başlattık

        // Haritadan gelen parsel_id'yi yakalıyoruz
        gelenParselId = getIntent().getStringExtra("PARSEL_ID");

        if (gelenParselId == null || gelenParselId.isEmpty()) {
            Toast.makeText(this, "Tarla bilgisi alınamadı!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Arayüz Bağlamaları
        ivBack = findViewById(R.id.ivBack);
        btnDelete = findViewById(R.id.btnDelete);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);

        tvEnlemValue = findViewById(R.id.tvEnlemValue);
        tvBoylamValue = findViewById(R.id.tvBoylamValue);
        tvIlIlceValue = findViewById(R.id.tvIlIlceValue);

        tvPhValue = findViewById(R.id.tvPhValue);
        tvOrganikValue = findViewById(R.id.tvOrganikValue);
        tvFosforValue = findViewById(R.id.tvFosforValue);
        tvPotasyumValue = findViewById(R.id.tvPotasyumValue);

        tvMahsul1 = findViewById(R.id.tvMahsul1);
        tvVerim1 = findViewById(R.id.tvVerim1);
        tvMahsul2 = findViewById(R.id.tvMahsul2);
        tvVerim2 = findViewById(R.id.tvVerim2);
        tvMahsul3 = findViewById(R.id.tvMahsul3);
        tvVerim3 = findViewById(R.id.tvVerim3);

        // --- ZİRAATÇİ KONTROLÜ VE SİLME BUTONUNU GİZLEME ---
        if (mAuth.getCurrentUser() != null) {
            db.collection("Kullanicilar").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String kullaniciRolu = documentSnapshot.getString("rol");

                            // Giriş yapan kişi Ziraat Mühendisi ise butonu tamamen gizle
                            if ("Ziraatci".equals(kullaniciRolu)) {
                                btnDelete.setVisibility(View.GONE);
                            }
                        }
                    });
        }


        // Başlangıçta verileri temizle (Boş görünsünler)
        alanlariTemizle();

        // Verileri Getir
        tarlaBilgileriniGetir();

        // --- GERİ BUTONU ---
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Sayfayı kapat, haritaya dön
            }
        });

        // --- SİL BUTONU ---
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TarlaDetayActivity.this)
                        .setTitle("Tarlayı Sil")
                        .setMessage("Bu tarlayı ve ona ait tüm laboratuvar/mahsul verilerini silmek istediğinize emin misiniz?")
                        .setPositiveButton("Evet, Sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tarlayiVeBagliVerileriSil();
                            }
                        })
                        .setNegativeButton("İptal", null)
                        .show();
            }
        });
    }

    private void alanlariTemizle() {
        tvEnlemValue.setText("-");
        tvBoylamValue.setText("-");
        tvIlIlceValue.setText("-");
        tvPhValue.setText("-");
        tvOrganikValue.setText("-");
        tvFosforValue.setText("-");
        tvPotasyumValue.setText("-");

        // Mahsul alanlarını varsayılan olarak boşaltıyoruz
        tvMahsul1.setText("-"); tvVerim1.setText("-");
        tvMahsul2.setText("");  tvVerim2.setText("");
        tvMahsul3.setText("");  tvVerim3.setText("");
    }

    private void tarlaBilgileriniGetir() {
        db.collection("Tarlalar").document(gelenParselId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            Tarla tarla = task.getResult().toObject(Tarla.class);
                            if (tarla != null) {
                                tvHeaderTitle.setText("Parsel " + tarla.getParsel_id());
                                tvEnlemValue.setText(String.valueOf(tarla.getEnlem()));
                                tvBoylamValue.setText(String.valueOf(tarla.getBoylam()));
                                tvIlIlceValue.setText(tarla.getIl() + "/" + tarla.getIlce());

                                // Tarla bilgisi geldikten sonra Lab verilerini çek
                                labSonuclariniGetir();
                            }
                        } else {
                            Toast.makeText(TarlaDetayActivity.this, "Tarla bilgisi bulunamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void labSonuclariniGetir() {
        db.collection("LabSonuclari")
                .whereEqualTo("parsel_id", gelenParselId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            LabSonucu labSonucu = document.toObject(LabSonucu.class);

                            if (labSonucu != null) {
                                tvPhValue.setText(String.valueOf(labSonucu.getpH()));
                                tvOrganikValue.setText("%" + labSonucu.getOrganik_m());
                                tvFosforValue.setText(labSonucu.getFosfor() + " kg/da");
                                tvPotasyumValue.setText(labSonucu.getPotasyum() + " kg/da");

                                mahsulOnerileriniGetir(labSonucu.getSonuc_id());
                            }
                        } else {
                            tvPhValue.setText("Veri Yok");
                        }
                    }
                });
    }

    private void mahsulOnerileriniGetir(String sonucId) {
        db.collection("Mahsul")
                .whereEqualTo("sonuc_id", sonucId)
                .limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            int kayitSayisi = task.getResult().size();

                            if (kayitSayisi >= 1) {
                                Mahsul m1 = task.getResult().getDocuments().get(0).toObject(Mahsul.class);
                                tvMahsul1.setText(m1.getMahsul());
                                tvVerim1.setText("%" + m1.getVerim());
                            }
                            if (kayitSayisi >= 2) {
                                Mahsul m2 = task.getResult().getDocuments().get(1).toObject(Mahsul.class);
                                tvMahsul2.setText(m2.getMahsul());
                                tvVerim2.setText("%" + m2.getVerim());
                            }
                            if (kayitSayisi >= 3) {
                                Mahsul m3 = task.getResult().getDocuments().get(2).toObject(Mahsul.class);
                                tvMahsul3.setText(m3.getMahsul());
                                tvVerim3.setText("%" + m3.getVerim());
                            }
                        } else {
                            tvMahsul1.setText("Öneri Yok");
                            tvVerim1.setText("-");
                        }
                    }
                });
    }

    private void tarlayiVeBagliVerileriSil() {
        db.collection("Tarlalar").document(gelenParselId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TarlaDetayActivity.this, "Tarla başarıyla silindi.", Toast.LENGTH_SHORT).show();
                            finish(); // Sayfayı kapatıp haritaya dön
                        } else {
                            Toast.makeText(TarlaDetayActivity.this, "Silme işlemi başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}