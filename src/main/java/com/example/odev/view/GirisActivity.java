package com.example.odev.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.odev.R;
import com.example.odev.model.Kullanici;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GirisActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // MainActivity'den gelecek rolü tutacağımız değişken (Varsayılan olarak Ciftci atadık)
    private String gelenRol = "Ciftci";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 1. ADIM: MainActivity'den fırlatılan rolü yakala
        if (getIntent().hasExtra("KULLANICI_ROLÜ")) {
            gelenRol = getIntent().getStringExtra("KULLANICI_ROLÜ");
        }

        // Eğer kullanıcı zaten giriş yapmışsa, rolünü bul ve doğru sayfaya at
        if (mAuth.getCurrentUser() != null) {
            kullaniciRoluneGoreYonlendir(mAuth.getCurrentUser().getUid());
        }

        // --- GİRİŞ YAP BUTONU ---
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String sifre = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || sifre.isEmpty()) {
                    Toast.makeText(GirisActivity.this, "Lütfen boş alan bırakmayın", Toast.LENGTH_SHORT).show();
                    return;
                }

                String emailFormat = username + "@tarim.com";

                mAuth.signInWithEmailAndPassword(emailFormat, sifre)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(GirisActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                                    // Giriş yapıldı, Firestore'dan rolüne bakıp yönlendireceğiz
                                    kullaniciRoluneGoreYonlendir(mAuth.getCurrentUser().getUid());
                                } else {
                                    Toast.makeText(GirisActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // --- KAYIT OL BUTONU ---
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String sifre = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || sifre.isEmpty()) {
                    Toast.makeText(GirisActivity.this, "Lütfen boş alan bırakmayın", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sifre.length() < 6) {
                    Toast.makeText(GirisActivity.this, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show();
                    return;
                }

                String emailFormat = username + "@tarim.com";

                mAuth.createUserWithEmailAndPassword(emailFormat, sifre)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // 2. ADIM: Modeli oluştururken ana sayfadan gelen rolü kullan
                                        Kullanici yeniKullanici = new Kullanici(user.getUid(), username, sifre, gelenRol);

                                        db.collection("Kullanicilar").document(user.getUid())
                                                .set(yeniKullanici)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(GirisActivity.this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show();
                                                            dogruSayfayaGit(gelenRol); // Kayıt olduğu rolle yönlendir
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(GirisActivity.this, "Kayıt Hatası: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    // Firestore'dan kullanıcının rolünü okuyup yönlendirme yapan yardımcı fonksiyon
    private void kullaniciRoluneGoreYonlendir(String uid) {
        db.collection("Kullanicilar").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            String veritabanindakiRol = task.getResult().getString("rol");
                            dogruSayfayaGit(veritabanindakiRol);
                        } else {
                            // Veritabanında rol bulunamazsa varsayılan olarak geldiği rolle devam et
                            dogruSayfayaGit(gelenRol);
                        }
                    }
                });
    }

    // Rol "Ziraatci" ise Laboratuvar'a, değilse Harita'ya fırlatan fonksiyon
    private void dogruSayfayaGit(String rol) {
        Intent intent;
        if ("Ziraatci".equals(rol)) {
            intent = new Intent(GirisActivity.this, LaboratuvarActivity.class);
        } else {
            intent = new Intent(GirisActivity.this, HaritaActivity.class);
        }
        startActivity(intent);
        finish();
    }
}