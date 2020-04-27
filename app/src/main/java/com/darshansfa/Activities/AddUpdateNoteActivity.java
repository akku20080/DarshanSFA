package com.darshansfa.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Note;
import com.darshansfa.dbModel.PJPSchedule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUpdateNoteActivity extends AppCompatActivity {

    private String retailerId;
    private String distributorId;
    private String retailerName;
    private String dsrId, noteType;

    @BindView(R.id.edTitle)
    EditText edTitle;

    @BindView(R.id.edContent)
    EditText edContent;
    private Note note;
    private boolean isUpdate;
    private String pjpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_update_note);
        ButterKnife.bind(this);

        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        retailerName = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_NAME);
        noteType = PreferencesManger.getStringFields(this, Constants.Pref.NOTE_TYPE);
        getSupportActionBar().setTitle("Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            Intent intent = getIntent();
            if (intent.getStringExtra(Constants.NOTE_HEAD).matches("")) {
                edTitle.setHint("Enter title");
            } else {
                edTitle.setText(UIUtil.getText(intent.getStringExtra(Constants.NOTE_HEAD)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intent intent = getIntent();
            isUpdate = intent.getBooleanExtra(Constants.UPDATE, false);
            note = (Note) intent.getSerializableExtra(Constants.DAILY_NOTE);
            edContent.setText(UIUtil.getText(note.getContent()));
            edTitle.setText(UIUtil.getText(note.getTitle()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.DAILY_NOTE.equalsIgnoreCase(noteType))
            checkForDailyNote();
    }

    private void checkForDailyNote() {
        if (isUpdate) {
            return;
        }
        List<PJPSchedule> list = PJPSchedule.find(PJPSchedule.class, "pjp_date = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy").format(new Date())});
        if (!(list.size() > 0)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppTheme_AppBarOverlay);
            builder.setCancelable(false);
            builder.setTitle("No PJP");
            String message = "PJP is not available for today, Please create PJP";
            builder.setMessage(message);
            builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(), CreatePJPDateWiseActivity.class));
                }
            });

            builder.show();
        } else {
            pjpId = list.get(0).getPjpId();

        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btnSave)
    public void saveNote() {
        if (TextUtils.isEmpty(edTitle.getText())) {
            edTitle.setError("Enter some text");
            return;
        }
        if (TextUtils.isEmpty(edContent.getText())) {
            edContent.setError("Enter some text");
            return;
        }

        if (!isUpdate) {
            note = new Note();
            note.setDsrId(dsrId);
            note.setRetailerId(retailerId);
            note.setDistributorId(distributorId);
            note.setTitle(edTitle.getText().toString());
            note.setContent(edContent.getText().toString());
            note.setNoteType(noteType);
            note.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            note.setTime(new SimpleDateFormat("hh:mm a").format(new Date()));
        } else {
            note.setTitle(edTitle.getText().toString());
            note.setContent(edContent.getText().toString());
            note.save();
        }


        if (!UIUtil.isInternetAvailable(this)) {
            long i = note.save();
            note.setStatus(Constants.STATUS_PENDING);
            Toast.makeText(getApplicationContext(), "Note Save but not update on server. : " + i, Toast.LENGTH_SHORT).show();
            return;
        }


        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();

        String s = gson.toJson(note);
        JsonObject object = gson.fromJson(s, JsonObject.class);
        if (Constants.DAILY_NOTE.equalsIgnoreCase(noteType)) {
            object.remove("retailerId");
        }
        object.addProperty("pjpId", note.getPjpId());
        object.addProperty("pjpPlanned", note.getDate());
        object.addProperty("remarks", note.getRemarks());

        if (isUpdate) {
            object.remove("pjpId");
            object.remove("id");
            object.addProperty("id", note.getPjpId());
        }
        jsonArray.add(object);
        updateNote(jsonArray);
    }

    private void updateNote(JsonArray jsonArray) {
        UIUtil.startProgressDialog(this, "Updating note, please wait...");
        String path = "";
        if (isUpdate) {
            path = noteType.equalsIgnoreCase(Constants.DAILY_NOTE) ? Constants.PATH_DAILY_NOTE_UPDATE : Constants.PATH_RETAILER_NOTE_UPDATE;
        } else {
            path = noteType.equalsIgnoreCase(Constants.DAILY_NOTE) ? Constants.PATH_DAILY_NOTE : Constants.PATH_RETAILER_NOTE;
        }

        Call<JsonObject> call;
        if (isUpdate) {
            call = RetrofitAPI.getInstance().getApi().uploadNote(jsonArray, path, distributorId, dsrId, note.getNoteId());
        } else {
            call = RetrofitAPI.getInstance().getApi().uploadNote(jsonArray, path, distributorId, dsrId);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        long i = note.save();
                        Toast.makeText(getApplicationContext(), "Note Save. Id : " + i, Toast.LENGTH_SHORT).show();
                        finish();
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
}
