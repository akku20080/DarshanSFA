package com.darshansfa.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.NoteRecyclerAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Note;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteActivity extends AppCompatActivity {


    @BindView(R.id.recyclerNote)
    RecyclerView recyclerView;

    @BindView(R.id.llNoData)
    LinearLayout llNoData;


    ArrayList<Note> arrayList;
    private NoteRecyclerAdapter adapter;
    private String retailerId, distributorId, dsrId, retailerName, noteType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForThePJPCreate();

            }
        });

        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DISTRIBUTOR_ID);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        retailerName = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_NAME);
        noteType = PreferencesManger.getStringFields(this, Constants.Pref.NOTE_TYPE);

        if (Constants.RETAILER_NOTE.equalsIgnoreCase(noteType)) {
            getSupportActionBar().setTitle(retailerName);
        } else {
            getSupportActionBar().setTitle("Daily Note");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
//        Note n = new Note();
//        n.setTitle("Title demo");
//        n.setContent("test content");
//        n.setTime("09:52 AM");
//        n.setDate("20-06-2017");
//        arrayList.add(n);

        adapter = new NoteRecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new NoteRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onDelete(View view, int position) {
//                deleteConfirm(position);
            }

            @Override
            public void onEdit(View view, int position) {
                try {
                    Intent intent = new Intent(NoteActivity.this, AddUpdateNoteActivity.class);
                    intent.putExtra(Constants.UPDATE, true);
                    if (Constants.RETAILER_NOTE.equalsIgnoreCase(noteType)) {
                        intent.putExtra(Constants.NOTE_HEAD, retailerName);
                    }else{
                        intent.putExtra(Constants.NOTE_HEAD, "");
                    }
                    intent.putExtra(Constants.RETAILER_NOTE, Constants.RETAILER_NOTE.equalsIgnoreCase(noteType));
                    intent.putExtra(Constants.DAILY_NOTE, arrayList.get(position));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        downloadNote();


    }

    private void checkForThePJPCreate() {

//        if (noteType != Constants.RETAILER_NOTE) {
//            PJPSchedule.find(PJPSchedule.class, "pjp_date = ?" , new String[]{})
//        }
        Intent intent = new Intent(NoteActivity.this, AddUpdateNoteActivity.class);
        if (Constants.RETAILER_NOTE.equalsIgnoreCase(noteType)) {
            intent.putExtra(Constants.NOTE_HEAD, retailerName);
        }else{
            intent.putExtra(Constants.NOTE_HEAD, "");
        }
        intent.putExtra(Constants.UPDATE, false);
        startActivity(intent);
    }

    @OnClick(R.id.tvAddNote)
    protected void addNote() {
        checkForThePJPCreate();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.sync) {
            updateNote();
            downloadNote();

//            Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dsr_stock, menu);

        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateNote();
        downloadNote();
    }

    @Override
    protected void onResume() {
        super.onResume();


        arrayList.clear();
//        Note n = new Note();
//
//        n.setTitle("Title demo");
//        n.setContent("test content");
//        n.setTime("09:52 AM");
//        n.setDate("20-06-2017");
//        n.setStatus(Constants.STATUS_PENDING);
//        arrayList.add(n);

        if (Constants.RETAILER_NOTE.equalsIgnoreCase(noteType)) {
            arrayList.addAll(Note.find(Note.class, "note_type = ? and retailer_Id = ? ", new String[]{noteType, retailerId}));
        } else {
            arrayList.addAll(Note.find(Note.class, "note_type = ? ", new String[]{noteType}));
        }
        Collections.reverse(arrayList);
        llNoData.setVisibility(arrayList.size() > 0 ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();

        //downloadNote();

    }

    private void deleteConfirm(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Do you want delete Note?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Note note = arrayList.get(position);
                note.delete();
                arrayList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void updateNote() {

        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet, Please connect internet", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<Note> notes = Note.find(Note.class, "note_type = ? and status = ? ", new String[]{noteType, Constants.STATUS_PENDING});

        if (!(notes.size() > 0)) {
            Toast.makeText(this, "All note are up to date ", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new Gson();
        String s = gson.toJson(notes);
        JsonArray jsonArray = gson.fromJson(s, JsonArray.class);
        if (!noteType.equalsIgnoreCase(Constants.RETAILER_NOTE)) {
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonArray.get(i).getAsJsonObject().remove("retailerId");
            }
        }

        UIUtil.startProgressDialog(this, "Updating note, please wait...");
        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().uploadNote(jsonArray,
                noteType.equalsIgnoreCase(Constants.RETAILER_NOTE) ? Constants.PATH_RETAILER_NOTE : Constants.PATH_DAILY_NOTE,
                PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE), PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        for (int i = 0; i < notes.size(); i++) {
                            Note note = notes.get(i);
                            note.setStatus(Constants.STATUS_DONE);
                            note.save();
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    private void downloadNote() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet, Please connect internet", Toast.LENGTH_SHORT).show();
            return;
        }
        UIUtil.startProgressDialog(this, "Downloading note, Please wait....");

        Call<List<Note>> call = RetrofitAPI.getInstance().getApi().getNote(
                noteType.equalsIgnoreCase(Constants.RETAILER_NOTE) ? Constants.PATH_RETAILER_NOTE : Constants.PATH_DAILY_NOTE,
                PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Log.e("Collection", "-------------------------------- P - " + response.body());
                Note.deleteAll(Note.class, "note_type = ? and status = ? ", new String[]{noteType, Constants.STATUS_DONE});
                Note.saveInTx(response.body());
                onResume();
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
