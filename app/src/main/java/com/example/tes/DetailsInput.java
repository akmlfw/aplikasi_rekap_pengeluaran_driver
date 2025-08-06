package com.example.tes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLogTags;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.PKLApp.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

public class DetailsInput extends AppCompatActivity {
    EditText kilometerawal, kilometerakhir, bbm, parkir, servis, etoll, tanggal, bulana;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String kmawali, kmakhiri, bahanbkr, biypark, serman, ngetol, tanggalu, docId, bulanu;
    String ImageUri;
    TextView deleteNoteTextViewBtn;
    ArrayList<Uri> ChooseImageList;
    RelativeLayout pickImageButton;
    ViewPager viewPager;
    ArrayList<String> Urlslist;
    StorageReference storageReference;
    FirebaseStorage mStorage;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_input);

        docId = getIntent().getStringExtra("docId");
        pickImageButton = findViewById(R.id.chooseImage);
        viewPager = findViewById(R.id.viewpager);
        kilometerawal = findViewById(R.id.awall);
        kilometerakhir = findViewById(R.id.akhirr);
        bbm = findViewById(R.id.bengsin);
        parkir = findViewById(R.id.parkir);
        servis = findViewById(R.id.serviss);
        etoll = findViewById(R.id.toll);
        tanggal = findViewById(R.id.tanggal);
        bulana = findViewById(R.id.bulan);
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        firestore = Utility.getCollectionReferenceForData().document().getFirestore();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Upload data");
        progressDialog.setMessage("Harap tunggu");


        Urlslist=new ArrayList<>();

        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        kmawali = getIntent().getStringExtra("kmawali");
        bahanbkr = getIntent().getStringExtra("bahanbkr");
        ngetol = getIntent().getStringExtra("ngetol");
        biypark = getIntent().getStringExtra("biypark");
        serman = getIntent().getStringExtra("serman");
        kmakhiri = getIntent().getStringExtra("kmakhiri");
        tanggalu = getIntent().getStringExtra("tanggalu");
        bulanu = getIntent().getStringExtra("bulanu");
        docId = getIntent().getStringExtra("docId");



        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());

        ChooseImageList=new ArrayList<>();

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
                PickimageFromGallery();

            }
        });

        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImages();


            }
        });
    }

    private void UploadImages() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            if (ChooseImageList.isEmpty()) {
                // No images to upload, directly call StoreLinks
                StoreLinks(Urlslist);
            } else {
                progressDialog.show();
                for (int i = 0; i < ChooseImageList.size(); i++) {
                    Uri IndividualImage = ChooseImageList.get(i);
                    if (IndividualImage != null) {
                        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages").child(uid);
                        final StorageReference ImageName = ImageFolder.child("Image" + i + ": " + IndividualImage.getLastPathSegment());
                        ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Urlslist.add(String.valueOf(uri));
                                        if (Urlslist.size() == ChooseImageList.size()) {
                                            // Panggil StoreLinks setelah semua gambar berhasil diunggah
                                            StoreLinks(Urlslist);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void StoreLinks(ArrayList<String> Urlslist) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String kmawaliStr = kilometerawal.getText().toString();
            double kmawali = TextUtils.isEmpty(kmawaliStr) ? 0 : Double.parseDouble(kmawaliStr);
            double bahanbkr = TextUtils.isEmpty(bbm.getText().toString()) ? 0 : Double.parseDouble(bbm.getText().toString());
            double biypark = TextUtils.isEmpty(parkir.getText().toString()) ? 0 : Double.parseDouble(parkir.getText().toString());
            double ngetol = TextUtils.isEmpty(etoll.getText().toString()) ? 0 : Double.parseDouble(etoll.getText().toString());
            double serman = TextUtils.isEmpty(servis.getText().toString()) ? 0 : Double.parseDouble(servis.getText().toString());
            double kmakhiri = TextUtils.isEmpty(kilometerakhir.getText().toString()) ? 0 : Double.parseDouble(kilometerakhir.getText().toString());
            String tanggalu = tanggal.getText().toString();
            String bulanu = bulana.getText().toString();

            if (TextUtils.isEmpty(kmawaliStr)) {
                kilometerawal.setError("Kilometer awal wajib diisi!");
                return;
            }

            if (TextUtils.isEmpty(tanggalu)) {
                tanggal.setError("Tanggal wajib diisi!");
                return;
            }

            Note data = new Note(kmawali, bahanbkr, biypark, serman, ngetol, kmakhiri, tanggalu, bulanu, "", Urlslist);
            data.setDocId(docId);

            firestore.collection("data").document(uid).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(DetailsInput.this, "Berhasil Upload KBM", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(DetailsInput.this, "Gagal Upload KBM: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            ChooseImageList.clear();

            data = new Note();
            data.setKmawali(kmawali);
            data.setBahanbkr(bahanbkr);
            data.setBiypark(biypark);
            data.setSerman(serman);
            data.setNgetol(ngetol);
            data.setKmakhiri(kmakhiri);
            data.setTanggalu(tanggalu);
            data.setBulanu(bulanu);
            data.setImageUrls(Urlslist);
            data.setDocId(docId);

            saveNoteToFirebase(data);
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    private void CheckPermission() {
        if (Build.VERSION.SDK_INT>-Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(DetailsInput.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DetailsInput.this, new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
            }else {
                PickimageFromGallery();
            }
        }else {
            PickimageFromGallery();
        }
    }

    private void PickimageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getClipData() !=null){
            int count = data.getClipData().getItemCount();
            for (int i=0;i<count;i++){
                ImageUri = String.valueOf(data.getClipData().getItemAt(i).getUri());
                ChooseImageList.add(Uri.parse(ImageUri));
                SetAdapter();
            }
        }
    }

    private void SetAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,ChooseImageList);
        viewPager.setAdapter(adapter);
    }


    void saveNoteToFirebase(Note data){
        DocumentReference documentReference;{
            documentReference = Utility.getCollectionReferenceForData().document();
        }

        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    //note is added
                    Utility.showToast(DetailsInput.this, "Input Berhasil");
                    finish();
                }else{
                    Utility.showToast(DetailsInput.this,"Input Gagal");

                }

            }
        });

    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForData().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //note is deleted
                    Utility.showToast(DetailsInput.this, "Berhasil Dihapus");
                    finish();
                }else{
                    Utility.showToast(DetailsInput.this,"Gagal Dihapus");

                }

            }
        });
    }
}