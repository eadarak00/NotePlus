package com.example.noteplus.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteplus.R;
import com.example.noteplus.db.NoteDatabaseHelper;
import com.example.noteplus.model.Note;

import java.util.Calendar;

public class NoteFormActivity extends AppCompatActivity {

    private EditText editTitle, editContent;
    private TextView textDate;
    private Button buttonPickDateTime, buttonSave;

    private int year, month, day, hour, minute;
    private NoteDatabaseHelper dbHelper;

    private int noteId = -1;
    private Note currentNote;

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE_TEXT = "date_text";
    private static final String KEY_YEAR = "year";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        dbHelper = new NoteDatabaseHelper(this);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        textDate = findViewById(R.id.textDate);
        buttonPickDateTime = findViewById(R.id.buttonPickDateTime);
        buttonSave = findViewById(R.id.buttonSave);

        noteId = getIntent().getIntExtra("noteId", -1);

        if (savedInstanceState != null) {
            // Restaurer depuis bundle sauvegardé
            editTitle.setText(savedInstanceState.getString(KEY_TITLE));
            editContent.setText(savedInstanceState.getString(KEY_CONTENT));
            textDate.setText(savedInstanceState.getString(KEY_DATE_TEXT));

            year = savedInstanceState.getInt(KEY_YEAR);
            month = savedInstanceState.getInt(KEY_MONTH);
            day = savedInstanceState.getInt(KEY_DAY);
            hour = savedInstanceState.getInt(KEY_HOUR);
            minute = savedInstanceState.getInt(KEY_MINUTE);
        } else {
            // Pas de restauration, initialisation normale
            if (noteId != -1) {
                currentNote = dbHelper.getNoteById(noteId);
                if (currentNote != null) {
                    editTitle.setText(currentNote.getTitle());
                    editContent.setText(currentNote.getContent());

                    String dateStr = currentNote.getDate();
                    try {
                        year = Integer.parseInt(dateStr.substring(0, 4));
                        month = Integer.parseInt(dateStr.substring(5, 7)) - 1;
                        day = Integer.parseInt(dateStr.substring(8, 10));
                        hour = Integer.parseInt(dateStr.substring(11, 13));
                        minute = Integer.parseInt(dateStr.substring(14, 16));
                    } catch (Exception e) {
                        setCurrentDateTime();
                    }
                } else {
                    setCurrentDateTime();
                }
            } else {
                setCurrentDateTime();
            }
            updateDateTimeText();
        }

        buttonPickDateTime.setOnClickListener(v -> pickDateThenTime());

        buttonSave.setOnClickListener(v -> saveNote());
    }

    private void setCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }

    private void pickDateThenTime() {
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, y, m, d) -> {
            year = y;
            month = m;
            day = d;
            TimePickerDialog timePicker = new TimePickerDialog(this, (view1, h, min) -> {
                hour = h;
                minute = min;
                updateDateTimeText();
            }, hour, minute, true);
            timePicker.show();
        }, year, month, day);
        datePicker.show();
    }

    private void updateDateTimeText() {
        String dateTime = String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hour, minute);
        textDate.setText(dateTime);
    }

    private void saveNote() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();
        String date = textDate.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Le titre est obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noteId == -1) {
            // Nouvelle note
            Note note = new Note(title, date, content);
            long id = dbHelper.addNote(note);
            if (id > 0) {
                Toast.makeText(this, "Note enregistrée", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Modification note existante
            currentNote.setTitle(title);
            currentNote.setContent(content);
            currentNote.setDate(date);

            int rows = dbHelper.updateNote(currentNote);
            if (rows > 0) {
                Toast.makeText(this, "Note modifiée", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLE, editTitle.getText().toString());
        outState.putString(KEY_CONTENT, editContent.getText().toString());
        outState.putString(KEY_DATE_TEXT, textDate.getText().toString());

        outState.putInt(KEY_YEAR, year);
        outState.putInt(KEY_MONTH, month);
        outState.putInt(KEY_DAY, day);
        outState.putInt(KEY_HOUR, hour);
        outState.putInt(KEY_MINUTE, minute);
    }
}
