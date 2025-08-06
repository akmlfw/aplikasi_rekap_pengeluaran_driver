package com.example.tes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.PKLApp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;
    TextView judul;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView =findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn);
        judul = findViewById(R.id.page_title);
        judul.setSelected(true);

        addNoteBtn.setOnClickListener((v)-> startActivity(new Intent(MainActivity.this, DetailsInput.class)));
        menuBtn.setOnClickListener((v)->showMenu());
        setupRecycleView();

        // Check if user is signed in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // No user is signed in, redirect to login screen
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }

    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
    void setupRecycleView(){
        CollectionReference notesCollection = Utility.getCollectionReferenceForData();

        if (notesCollection != null) {
            Query query = notesCollection.orderBy("tanggalu", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                    .setQuery(query, Note.class).build();

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            noteAdapter = new NoteAdapter(options, this);
            recyclerView.setAdapter(noteAdapter);
        } else {
            // Handle the case where the CollectionReference is null
            Utility.showToast(this, "Error retrieving notes");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}