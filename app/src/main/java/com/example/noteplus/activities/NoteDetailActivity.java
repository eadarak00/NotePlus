package com.example.noteplus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteplus.R;
import com.example.noteplus.db.NoteDatabaseHelper;
import com.example.noteplus.model.Note;

public class NoteDetailActivity extends AppCompatActivity {

    private TextView textDetailTitle, textDetailDate, textDetailContent;
    private Button btnEditNote, btnDeleteNote;
    private int noteId;
    private NoteDatabaseHelper db;
    private Note currentNote;

    private ActivityResultLauncher<Intent> editNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textDetailTitle = findViewById(R.id.textDetailTitle);
        textDetailDate = findViewById(R.id.textDetailDate);
        textDetailContent = findViewById(R.id.textDetailContent);
        btnEditNote = findViewById(R.id.btnEditNote);
        btnDeleteNote = findViewById(R.id.btnDeleteNote);

        db = new NoteDatabaseHelper(this);

        noteId = getIntent().getIntExtra("noteId", -1);

        // Launcher pour modifier la note
        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadNoteDetails();
                        setResult(RESULT_OK);
                    }
                }
        );

        if (noteId != -1) {
            loadNoteDetails();
        }

        btnEditNote.setOnClickListener(v -> {
            Intent intent = new Intent(NoteDetailActivity.this, com.example.noteplus.activities.NoteFormActivity.class);
            intent.putExtra("noteId", noteId);
            editNoteLauncher.launch(intent);
        });

        btnDeleteNote.setOnClickListener(v -> {
            new AlertDialog.Builder(NoteDetailActivity.this)
                    .setTitle("Supprimer la note")
                    .setMessage("Voulez-vous vraiment supprimer cette note ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        db.deleteNoteById(noteId);
                        Toast.makeText(NoteDetailActivity.this, "Note supprimée", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    private void loadNoteDetails() {
        currentNote = db.getNoteById(noteId);
        if (currentNote != null) {
            textDetailTitle.setText(currentNote.getTitle());
            textDetailDate.setText(currentNote.getDate());
            textDetailContent.setText(currentNote.getContent());
        }
    }

    private void shareNote() {
        if (currentNote == null) return;

        String shareBody = "Titre: " + currentNote.getTitle() + "\n" +
                "Date: " + currentNote.getDate() + "\n\n" +
                currentNote.getContent();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentNote.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(shareIntent, "Partager la note via"));
    }

    // Menu pour le partage uniquement
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Cacher toutes les options sauf le partage
        menu.findItem(R.id.menu_add_note).setVisible(false);
        menu.findItem(R.id.menu_sort_date).setVisible(false);
        menu.findItem(R.id.menu_sort_title).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Gérer le clic sur la flèche retour (Up)
            finish(); // Termine l'activité et revient à l'écran précédent
            return true;
        }

        if (id == R.id.menu_share) {
            shareNote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
