package com.example.noteplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteplus.activities.NoteDetailActivity;
import com.example.noteplus.activities.NoteFormActivity;
import com.example.noteplus.adapter.NoteAdapter;
import com.example.noteplus.db.NoteDatabaseHelper;
import com.example.noteplus.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.noteplus.R;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private NoteDatabaseHelper db;

    private ActivityResultLauncher<Intent> noteFormLauncher;
    private ActivityResultLauncher<Intent> noteDetailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new NoteDatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList, note -> {
            Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
            intent.putExtra("noteId", note.getId());
            noteDetailLauncher.launch(intent);
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteFormActivity.class);
            noteFormLauncher.launch(intent);
        });

        noteFormLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshNoteList();
                    }
                }
        );

        noteDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshNoteList();
                    }
                }
        );

        refreshNoteList();
    }

    private void refreshNoteList() {
        noteList.clear();
        noteList.addAll(db.getAllNotes());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Cacher l'item partage dans MainActivity
        MenuItem shareItem = menu.findItem(R.id.menu_share);
        if (shareItem != null) {
            shareItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_note) {
            Intent intent = new Intent(this, NoteFormActivity.class);
            noteFormLauncher.launch(intent);
            return true;
        } else if (id == R.id.menu_sort_date) {
            sortNotesByDate();
            return true;
        } else if (id == R.id.menu_sort_title) {
            sortNotesByTitle();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void sortNotesByDate() {
        noteList.sort((n1, n2) -> n2.getDate().compareTo(n1.getDate()));  // tri descendant
        adapter.notifyDataSetChanged();
    }

    private void sortNotesByTitle() {
        noteList.sort((n1, n2) -> n1.getTitle().compareToIgnoreCase(n2.getTitle()));
        adapter.notifyDataSetChanged();
    }
}
