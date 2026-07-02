package com.example.odev.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odev.R;
import com.example.odev.model.LabSonucu;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class KayitAdapter extends RecyclerView.Adapter<KayitAdapter.KayitViewHolder> {

    private Context context;
    private List<LabSonucu> labListesi;
    private FirebaseFirestore db;

    public KayitAdapter(Context context, List<LabSonucu> labListesi) {
        this.context = context;
        this.labListesi = labListesi;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public KayitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kayit, parent, false);
        return new KayitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KayitViewHolder holder, int position) {
        LabSonucu labSonucu = labListesi.get(position);

        // Parsel ID'sini ekrana yazdırıyoruz
        holder.tvItemParselId.setText("Parsel: " + labSonucu.getParsel_id());

        // Başlangıçta yükleniyor gösterelim
        holder.tvItemTarlaSahibi.setText("Yükleniyor...");

        // --- TARLA SAHİBİNİ BULMA MOTORU ---
        // Lab sonucundaki parsel_id'yi kullanarak önce Tarlalar tablosuna gidiyoruz
        db.collection("Tarlalar").document(labSonucu.getParsel_id()).get()
                .addOnSuccessListener(tarlaDoc -> {
                    if (tarlaDoc.exists()) {
                        String kullaniciId = tarlaDoc.getString("kullanici_id");
                        if (kullaniciId != null) {
                            // Tarladan kullanici_id'yi aldık, şimdi sahibinin adına ulaşmak için Kullanicilar tablosuna gidiyoruz
                            db.collection("Kullanicilar").document(kullaniciId).get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (userDoc.exists()) {
                                            String sahipAdi = userDoc.getString("kullanici_adi");
                                            holder.tvItemTarlaSahibi.setText(sahipAdi);
                                        }
                                    });
                        }
                    } else {
                        holder.tvItemTarlaSahibi.setText("Bilinmeyen Sahip");
                    }
                });

        // 1. Karta Tıklanınca (Tarla Detayına Git)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TarlaDetayActivity.class);
            intent.putExtra("PARSEL_ID", labSonucu.getParsel_id());
            context.startActivity(intent);
        });

        // 2. Çöp Kutusuna Tıklanınca (Sadece Lab ve Mahsul Silinir, Tarla Kalır)
        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Kaydı Sil")
                    .setMessage("Bu laboratuvar sonucunu ve bağlı mahsul önerilerini silmek istiyor musunuz? (Tarla silinmeyecektir.)")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        // GÜNCELLEME: position göndermiyoruz, sadece objeyi gönderiyoruz.
                        kaydiSil(labSonucu);
                    })
                    .setNegativeButton("İptal", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return labListesi.size();
    }

    // GÜNCELLEME: Parametrelerden int position kaldırıldı.
    private void kaydiSil(LabSonucu labSonucu) {
        // Önce Lab sonucunu siliyoruz
        db.collection("LabSonuclari").document(labSonucu.getSonuc_id())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Lab silindiyse, ona bağlı Mahsul kayıtlarını da bulup silelim
                    db.collection("Mahsul")
                            .whereEqualTo("sonuc_id", labSonucu.getSonuc_id())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    db.collection("Mahsul").document(doc.getId()).delete();
                                }

                                // --- SİHİRLİ DOKUNUŞ BURASI ---
                                // Silinen öğenin listedeki GÜNCEL sırasını buluyoruz
                                int guncelSira = labListesi.indexOf(labSonucu);
                                if (guncelSira != -1) { // Eğer listede hala varsa
                                    labListesi.remove(guncelSira); // Guncel sıradakini sil
                                    notifyItemRemoved(guncelSira); // Ekrandan anında uçur
                                    notifyItemRangeChanged(guncelSira, labListesi.size()); // Altındakileri yukarı kaydır
                                }

                                Toast.makeText(context, "Laboratuvar verileri başarıyla silindi.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Silme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static class KayitViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemParselId, tvItemTarlaSahibi;
        ImageView ivLocation, ivDelete;

        public KayitViewHolder(@NonNull View itemView) {
            super(itemView);
            // Senin XML dosendaki gerçek ID karşılıkları
            tvItemParselId = itemView.findViewById(R.id.tvItemParselId);
            tvItemTarlaSahibi = itemView.findViewById(R.id.tvItemTarlaSahibi);
            ivLocation = itemView.findViewById(R.id.ivLocation);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}