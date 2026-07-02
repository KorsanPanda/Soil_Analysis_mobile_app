package com.example.odev.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.odev.R;

public class MainActivity extends AppCompatActivity {

    private Button btnZiraatci, btnCiftci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML'deki butonları bağlıyoruz
        btnZiraatci = findViewById(R.id.btnZiraatci);
        btnCiftci = findViewById(R.id.btnCiftci);

        // Ziraat Mühendisi butonuna tıklandığında
        btnZiraatci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yonlendirGirisEkranina("Ziraatci");
            }
        });

        // Çiftçi butonuna tıklandığında
        btnCiftci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yonlendirGirisEkranina("Ciftci");
            }
        });
    }

    // Rol bilgisini alan ve Giriş Ekranına fırlatan fonksiyon
    private void yonlendirGirisEkranina(String secilenRol) {

        // YENİ EKLENEN KOD: Kullanıcı giriş ekranına gitmek istiyorsa,
        // arka planda kalmış eski bir oturum varsa onu zorla kapatıyoruz.
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(MainActivity.this, GirisActivity.class);
        intent.putExtra("KULLANICI_ROLÜ", secilenRol);
        startActivity(intent);
    }
    }
