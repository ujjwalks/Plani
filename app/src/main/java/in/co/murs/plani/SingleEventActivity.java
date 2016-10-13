package in.co.murs.plani;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.util.Attributes;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.co.murs.plani.adapters.NotesAdapter;
import in.co.murs.plani.models.Address;
import in.co.murs.plani.models.Event;
import in.co.murs.plani.models.Note;
import in.co.murs.plani.views.AdvOnDateSetListener;
import in.co.murs.plani.views.AdvOnTimeSetListener;
import in.co.murs.plani.views.CustomAlertDialog;
import in.co.murs.plani.views.DividerItemDecoration;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;


public class SingleEventActivity extends AppCompatActivity {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";


    @BindView(R.id.rvNotes)
    RecyclerView rvNotes;

    @BindView(R.id.content)
    CoordinatorLayout clContent;

    @BindView(R.id.vEvent)
    View vEvent;

    @BindView(R.id.tvTitle)
    ExpandableTextView tvTitle;

    @BindView(R.id.tvDescription)
    ExpandableTextView tvDescription;

    @BindView(R.id.tvLocation)
    TextView tvLocation;

    @BindView(R.id.tvDuration)
    TextView tvDuration;

    @BindView(R.id.ivEdit)
    ImageButton ivEdit;

    @BindView(R.id.ivDelete)
    ImageButton ivDelete;

    @BindView(R.id.ivAlarm)
    ImageButton ivAlarm;

    private NotesAdapter mNotesAdapter;
    private Event mEvent;
    private ActionBar mActionBar;
    private long mEventStartTime;

    public List<Note> notes = new ArrayList<Note>();

    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();

    //checks for changes to be reflected in scheduler activity
    public boolean notesUpdated = false;
    private boolean eventUpdated = false;
    private boolean eventDeleted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        ButterKnife.bind(this);
        try {
            mActionBar = getSupportActionBar();
            init();
            setupAdapter();
            showFeedLoadingItemDelayed();

        } catch (Exception e) {
            Toast.makeText(this, "Please Try Again", Toast.LENGTH_LONG).show();
            finish();
            e.printStackTrace();
        }
    }

    private void setupAdapter() {
        // Layout Managers:
        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        // Item Decorator:
        rvNotes.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        rvNotes.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        mNotesAdapter = new NotesAdapter(this, this);
        mNotesAdapter.setMode(Attributes.Mode.Multiple);
        rvNotes.setAdapter(mNotesAdapter);

        /* Listeners */
        rvNotes.setOnScrollListener(onScrollListener);
    }

    /**
     *
     */
    private void init() throws Exception {
        Intent intent = getIntent();
        this.mEvent = (Event) intent.getSerializableExtra("event");
        this.notes = PlanIApplication.getDb().getAllNotes(mEvent.get_id());
        this.mEventStartTime = mEvent.getStartTime();

        if(notes != null)
            System.out.println(notes.size());

        if (mActionBar != null)
            mActionBar.setTitle("Starts @  " + Utils.getTimeToString("MMM dd, yyyy hh:mm aa", mEvent.getStartTime()));

        tvTitle.setText(mEvent.getTitle());
        tvDescription.setText(mEvent.getDescription());

        if (mEvent.getLocation() != null && !TextUtils.isEmpty(mEvent.getLocation().getAddress())) {
            tvLocation.setText(mEvent.getLocation().getAddress());
        } else {
            tvLocation.setText("Address not specified");
        }

        if (mEvent.getEndTime() > 0l)
            tvDuration.setText(Utils.getDuration(mEvent.getStartTime(), mEvent.getEndTime()) + " duration ");
        else {
            tvDuration.setText("");
        }

        ivEdit.setImageDrawable(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_pencil_square_o)
                .color(Color.WHITE)
                .sizeDp(24));

        ivDelete.setImageDrawable(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_trash)
                .color(Color.WHITE)
                .sizeDp(24));

        ivAlarm.setImageDrawable(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_clock_o)
                .color(Color.WHITE)
                .sizeDp(24));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NEW_NOTE_REQUEST_CODE) {
            try {
                Note note = (Note) data.getSerializableExtra("note");
                notes.add(note);
                mNotesAdapter.refreshRecyclerView();
                Toast.makeText(this, "Note Created", Toast.LENGTH_LONG).show();
                notesUpdated = true;
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvNotes.smoothScrollToPosition(0);
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("note_updated", notesUpdated);
        intent.putExtra("event_updated", eventUpdated);
        intent.putExtra("event_details", mEvent);
        intent.putExtra("event_start_time", mEventStartTime);
        intent.putExtra("event_deleted", eventDeleted);
        setResult(Constants.SINGLE_EVENT_ACTIVITY, intent);

        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.fabNote)
    public void onClickFabNote(View view) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("event", mEvent.get_id());
        startActivityForResult(intent, Constants.NEW_NOTE_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @OnClick(R.id.ivEdit)
    public void onClickEdit(View view) {
        try {
            dialogUpdateEvent();
        }catch(Exception e){
            e.printStackTrace();
            YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(view);
        }
    }

    @OnClick(R.id.ivDelete)
    public void onClickDelete(View view) {
        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(view);
        // TODO: 7/13/2016 show full note
        IconicsDrawable icon = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_calendar_minus_o)
                .color(Color.WHITE)
                .sizeDp(20);

        CustomAlertDialog builder = new CustomAlertDialog(this);
        builder.setNoteData(icon, "", "", "Do you want to delete this event?", "");
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            //mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                            if(mEvent.getLocation() != null)
                                PlanIApplication.db.deleteEvent(mEvent.get_id(), mEvent.getLocation().get_id());
                            else
                                PlanIApplication.db.deleteEvent(mEvent.get_id(), 0l);
                            eventUpdated = true;
                            eventDeleted = true;
                            onBackPressed();

                        }catch(Exception e){
                            e.printStackTrace();
                            YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(ivDelete);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        android.app.AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

    @OnClick(R.id.ivAlarm)
    public void onClickAlarm(View view) {
        try {
            Utils.dialogAlarm(SingleEventActivity.this, 1, mEvent.get_id());
        }catch(Exception e){
            e.printStackTrace();
            YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(ivAlarm);
        }
    }

    /**
     * Substitute for our onScrollListener for RecyclerView
     */
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("ListView", "onScrollStateChanged");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Could hide open views here if you wanted. //
        }
    };

    private void dialogUpdateEvent(){
        LayoutInflater li = LayoutInflater.from(this);
        View dView = li.inflate(R.layout.dialog_update_event, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dView);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        final EditText etStartDate = (EditText) dView.findViewById(R.id.etStartDate);
        final EditText etStartTime = (EditText) dView.findViewById(R.id.etStartTime);
        final EditText etEndDate = (EditText) dView.findViewById(R.id.etEndDate);
        final EditText etEndTime = (EditText) dView.findViewById(R.id.etEndTime);

        final EditText etDescription = (EditText) dView.findViewById(R.id.etDescription);
        final EditText etAddress = (EditText) dView.findViewById(R.id.etLocation);

        final Button btnUpdateAddress = (Button) dView.findViewById(R.id.btnUpdateAddress);
        final Button btnUpdateDesc = (Button) dView.findViewById(R.id.btnUpdateDesc);
        final Button btnUpdateEndTime = (Button) dView.findViewById(R.id.btnUpdateEndTime);
        final Button btnUpdateStartTime = (Button) dView.findViewById(R.id.btnUpdateStartTime);


        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnDateSetListener date = new AdvOnDateSetListener(etStartDate, startCalendar);
                new DatePickerDialog(SingleEventActivity.this, date, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnTimeSetListener time = new AdvOnTimeSetListener(etStartTime, startCalendar);
                new TimePickerDialog(SingleEventActivity.this, time, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnDateSetListener date = new AdvOnDateSetListener(etEndDate, endCalendar);
                new DatePickerDialog(SingleEventActivity.this, date, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnTimeSetListener time = new AdvOnTimeSetListener(etEndTime, endCalendar);
                new TimePickerDialog(SingleEventActivity.this, time, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        });

        //button onclick listeners

        btnUpdateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String msg = "Address" + " Updated";
                    String address = etAddress.getText().toString();
                    if(TextUtils.isEmpty(address)) {
                        etAddress.setError("Invalid input");
                    }else{
                        etAddress.setError(null);

                        Address address1 = new Address(0l,0l,address);
                        PlanIApplication.getDb().updateEventAddress(address1, mEvent.get_id());
                        mEvent.setLocation(address1);
                        eventUpdated = true;

                        etAddress.setText(address);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpdateDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String msg = "Description" + " Updated";
                    String desc = etDescription.getText().toString();
                    if(TextUtils.isEmpty(desc)) {
                        etDescription.setError("Invalid input");
                    }else{
                        etDescription.setError(null);

                        PlanIApplication.getDb().updateEventDescription(desc, mEvent.get_id());
                        mEvent.setDescription(desc);
                        eventUpdated = true;

                        etDescription.setText(desc);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpdateStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String msg = "Start Time" + " Updated";
                    if(startCalendar.getTimeInMillis() <= new Date().getTime()) {
                        etStartDate.setError("Invalid input");
                        etStartTime.setError("Invalid input");
                    }else{
                        etStartDate.setError(null);
                        etStartTime.setError(null);

                        PlanIApplication.getDb().updateEventStartTime(startCalendar.getTimeInMillis(), mEvent.get_id());
                        mEvent.setStartTime(startCalendar.getTimeInMillis());
                        eventUpdated = true;

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                        mActionBar.setTitle("Starts @   " + sdf.format(startCalendar.getTimeInMillis()));
                        tvDuration.setText(Utils.getDuration(mEvent.getStartTime(), mEvent.getEndTime()));
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpdateEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String msg = "End Time" + " Updated";
                    if(endCalendar.getTimeInMillis() < mEvent.getStartTime()) {
                        etEndDate.setError("Invalid input");
                        etEndTime.setError("Invalid input");
                    }else{
                        etEndDate.setError(null);
                        etEndTime.setError(null);

                        PlanIApplication.getDb().updateEventEndTime(endCalendar.getTimeInMillis(), mEvent.get_id());
                        mEvent.setEndTime(endCalendar.getTimeInMillis());
                        eventUpdated = true;

                        tvDuration.setText(Utils.getDuration(mEvent.getStartTime(), mEvent.getEndTime()));
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_LONG).show();
                }
            }
        });


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }
}
