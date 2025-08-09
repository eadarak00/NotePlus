package com.example.noteplus;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteplus.R;
import com.example.noteplus.adapter.NoteAdapter;
import com.example.noteplus.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteList = new ArrayList<>();
        // Test avec données statiques (SQLite viendra plus tard)
        noteList.add(new Note(1, "Ma première note", "2025-08-09"));
        noteList.add(new Note(2, "Faire les courses", "2025-08-10"));

        adapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ajouter une note", Toast.LENGTH_SHORT).show();
                // Plus tard : ouvrir NoteFormActivity
            }
        });
    }
}