package com.makienkovs.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Note> notes;
    private ListView lvNotes;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setTitle("");

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        lvNotes = findViewById(R.id.list);
        registerForContextMenu(lvNotes);
        createList();

        adapter = new Adapter(notes, this);
        lvNotes.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Note n = adapter.getNote();
        if (n == null && item.getItemId() != R.id.add) {
            Toast.makeText(this, R.string.choose, Toast.LENGTH_SHORT).show();
        } else {
            switch (item.getItemId()) {
                case R.id.add:
                    addNewNote();
                    break;
                case R.id.edit:
                    assert n != null;
                    editNote(n);
                    break;
                case R.id.delete:
                    assert n != null;
                    deleteNote(n);
                    adapter.nullNote();
                    break;
                case R.id.cancel:
                    assert n != null;
                    cancelNote(n);
                    break;
                case R.id.mark:
                    assert n != null;
                    markNote(n);
                    break;
                case R.id.share:
                    assert n != null;
                    shareNote(n);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    private void shareNote(Note n) {
        String output = n.getContent();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        share.putExtra(Intent.EXTRA_SUBJECT, R.string.shareNote);
        share.putExtra(Intent.EXTRA_TEXT, output);
        startActivity(Intent.createChooser(share, getString(R.string.share)));
    }

    private void sort() {
        notes.sort(new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                String time1 = String.valueOf(o1.getTime());
                String time2 = String.valueOf(o2.getTime());
                return time2.compareTo(time1);
            }
        });
    }

    private void createList() {
        notes = new ArrayList<>();
        Cursor c = db.query(DBHelper.MYTABLE, null, null, null, null, null, null);
        if (c.moveToNext()) {
            int contentIndex = c.getColumnIndex(DBHelper.CONTENT);
            int timeIndex = c.getColumnIndex(DBHelper.TIME);
            int doneIndex = c.getColumnIndex(DBHelper.DONE);
            int cancelIndex = c.getColumnIndex(DBHelper.CANCEL);
            do {
                Note n = new Note();
                n.setContent(c.getString(contentIndex));
                n.setTime(c.getLong(timeIndex));
                n.setDone(c.getInt(doneIndex) == 1);
                n.setCancel(c.getInt(cancelIndex) == 1);
                notes.add(n);
            } while (c.moveToNext());
        }
        c.close();
        sort();
    }

    private void editNote(final Note n) {
        final Calendar c = Calendar.getInstance();
        final View noteLayout = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText editText = noteLayout.findViewById(R.id.textNote);
        editText.setText(n.getContent());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        new AlertDialog.Builder(this)
                .setTitle(R.string.edit)
                .setView(noteLayout)
                .setPositiveButton(R.string.keep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long time = n.getTime();
                        n.setContent(editText.getText().toString());
                        editDB(time, n);
                        lvNotes.setAdapter(adapter);
                    }
                })
                .setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long time = n.getTime();
                        n.setContent(editText.getText().toString());
                        n.setTime(c.getTimeInMillis());
                        sort();
                        editDB(time, n);
                        lvNotes.setAdapter(adapter);
                    }
                })
                .create()
                .show();
    }

    private void addNewNote() {
        final Calendar c = Calendar.getInstance();
        final View noteLayout = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText editText = noteLayout.findViewById(R.id.textNote);
        final Note n = new Note();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        new AlertDialog.Builder(this)
                .setTitle(R.string.add)
                .setView(noteLayout)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        n.setContent(editText.getText().toString());
                        n.setTime(c.getTimeInMillis());
                        notes.add(n);
                        sort();
                        addToDB(n);
                        lvNotes.setAdapter(adapter);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void deleteNote(final Note n) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setCancelable(false)
                .setMessage(R.string.sure)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long time = n.getTime();
                        notes.remove(n);
                        sort();
                        deleteFromDB(time);
                        lvNotes.setAdapter(adapter);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void cancelNote(Note n) {
        n.setCancel(!n.isCancel());
        editDB(n.getTime(), n);
        lvNotes.setAdapter(adapter);
    }

    private void markNote(Note n) {
        n.setDone(!n.isDone());
        editDB(n.getTime(), n);
        lvNotes.setAdapter(adapter);
    }

    private void deleteFromDB(final long time) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.delete(DBHelper.MYTABLE, "TIME = " + time, null);
            }
        });
        thread.start();
    }

    private void editDB(final long time, final Note n) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.CONTENT, n.getContent());
                cv.put(DBHelper.TIME, n.getTime());
                int done;
                if (n.isDone()) done = 1;
                else done = 0;
                int cancel;
                if (n.isCancel()) cancel = 1;
                else cancel = 0;
                cv.put(DBHelper.DONE, done);
                cv.put(DBHelper.CANCEL, cancel);
                db.update(DBHelper.MYTABLE, cv, "TIME = " + time, null);
            }
        });
        thread.start();
    }

    private void addToDB(final Note n) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.CONTENT, n.getContent());
                cv.put(DBHelper.TIME, n.getTime());
                int done;
                if (n.isDone()) done = 1;
                else done = 0;
                cv.put(DBHelper.DONE, done);
                db.insert(DBHelper.MYTABLE, null, cv);
            }
        });
        thread.start();
    }
}