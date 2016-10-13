package in.co.murs.plani;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.murs.plani.fragments.CalendarFragment;
import in.co.murs.plani.fragments.EventFragment;
import in.co.murs.plani.fragments.NotesFragment;
import in.co.murs.plani.models.Event;
import in.co.murs.plani.models.Note;
import in.co.murs.plani.views.CustomAlertDialog;

public class SchedulerActivity extends AppCompatActivity {

    @BindView(R.id.vpScheduler)
    ViewPager mPager;

    SchedulerAdapter mAdapter;
    ActionBar mActionBar;

    //Toolbar Titles
    private long currentMonth;
    private long currentDate;

    public List<Event> events = new ArrayList<Event>();
    public List<Note> notes = new ArrayList<Note>();
    public List<Event> upcomingEvents = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        ButterKnife.bind(this);

        try {
            getInitialData();
        } catch (Exception e) {
            Toast.makeText(this, "Some Error Occured. Please try again", Toast.LENGTH_LONG).show();
        }

        mAdapter = new SchedulerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        setTitle(Utils.getCurrentTime("MMM yyyy"));

        if (getSupportActionBar() != null) {
            mActionBar = getSupportActionBar();
            mActionBar.setDisplayShowHomeEnabled(true);
        }

        // Attach the page change listener inside the activity
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if (mActionBar != null) {
                    switch (position) {
                        case 0:
                            mActionBar.setTitle("Notes");
                            break;
                        case 1:
                            mActionBar.setTitle(Utils.getTimeToString("MMM yyyy", currentMonth));
                            break;
                        case 2:
                            mActionBar.setTitle(Utils.getTimeToString("MMM dd, yyyy", currentDate));
                            break;
                    }
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        mPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void getInitialData() throws Exception {
        notes = PlanIApplication.getDb().getAllNotes();
        Date date = new Date();
        currentDate = currentMonth = date.getTime();
        Date startTime = Utils.getStartOfDay(date);
        Date endTime = Utils.getEndOfDay(date);
        events = PlanIApplication.getDb().getEventsForInterval(startTime.getTime(), endTime.getTime());

        //upcoming events 2 days
        upcomingEvents = PlanIApplication.getDb().getEventsForInterval(currentDate, currentDate + 48 * 3600 * 1000l);
    }

    public void setMonth(long month) {
        this.currentMonth = month;
        if (mActionBar != null)
            mActionBar.setTitle(Utils.getTimeToString("MMM yyyy", currentMonth));
    }

    public void getEventForDate(Date date) throws Exception {
        Date startTime = Utils.getStartOfDay(date);
        Date endTime = Utils.getEndOfDay(date);
        events = PlanIApplication.getDb().getEventsForInterval(startTime.getTime(), endTime.getTime());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String queryDate = simpleDateFormat.format(date);
        if (mActionBar != null) mActionBar.setTitle(queryDate);
        this.currentDate = date.getTime();
        mPager.setCurrentItem(2);
        mAdapter.eventFragment.refreshRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NEW_NOTE_REQUEST_CODE) {
            try {
                Note note = (Note) data.getSerializableExtra("note");
                notes.add(note);
                mAdapter.notesFragment.refreshRecyclerView();
                Toast.makeText(this, "Note Created", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
            }
        }

        if (requestCode == Constants.NEW_EVENT_REQUEST_CODE) {
            // TODO: 7/14/2016 update event
            try {
                Event event = (Event) data.getSerializableExtra("event");
                onEventAdd(event);
                Toast.makeText(this, "Event Created", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
            }
        }

        if (requestCode == Constants.SINGLE_EVENT_ACTIVITY) {

            boolean notesUpdated = data.getBooleanExtra("note_updated", false);
            boolean eventUpdated = data.getBooleanExtra("event_updated", false);
            boolean eventDeleted = data.getBooleanExtra("event_deleted", false);

            if (notesUpdated) {
                try {
                    notes = PlanIApplication.getDb().getAllNotes();
                    mAdapter.notesFragment.refreshRecyclerView();
                    Log.d("Single Event", "Notes Updated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (eventUpdated) {
                try {
                    Event event = (Event) data.getSerializableExtra("event_details");
                    long startEvent = data.getLongExtra("event_start_time", 0l);
                    if(!eventDeleted) {
                        onEventUpdate(event, startEvent);
                        Log.d("Single Event", "Event Updated");
                        Toast.makeText(getApplicationContext(), "Event Updated", Toast.LENGTH_SHORT).show();
                    }else{
                        onEventDelete(event, 0l);
                        Toast.makeText(getApplicationContext(), "Event Deleted", Toast.LENGTH_SHORT).show();
                        Log.d("Single Event", "Event Deleted");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: 7/13/2016 show full note

        CustomAlertDialog builder = new CustomAlertDialog(this);
        builder.setNoteData(null, getResources().getString(R.string.app_name), "", "Do you want to Exit?", "");
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeActivity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();

    }

    private void closeActivity() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_help:
                dialogHelp();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void onEventAdd(Event event){
        Date date = new Date();
        if (date.getTime() < event.getStartTime() && event.getStartTime() < date.getTime() +
                48 * 3600 * 1000l) {
            //update upcoming
            upcomingEvents.add(event);
            mAdapter.calendarFragment.addEvent(event);
            mAdapter.calendarFragment.refreshRecyclerView();

        }

        //update current date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String eventStart = sdf.format(event.getStartTime());
        String current = sdf.format(currentDate);
        if (eventStart.equals(current)) {
            events.add(event);
            mAdapter.eventFragment.refreshRecyclerView();
        }
    }

    public void onEventUpdate(Event event, long startEvent){
        onEventDelete(event, startEvent);
        onEventAdd(event);
    }

    public void onEventDelete(Event event, long startTime){

        if(startTime == 0l)
            mAdapter.calendarFragment.removeEvent(event.getTitle(), event.getStartTime());
        else
            mAdapter.calendarFragment.removeEvent(event.getTitle(), startTime);

        for(int i = 0 ; i < upcomingEvents.size(); i++){
            if(upcomingEvents.get(i).get_id() == event.get_id())
            {
                upcomingEvents.remove(i);
                mAdapter.calendarFragment.refreshRecyclerView();
            }
        }

        for(int i = 0 ; i < events.size(); i++){
            if(events.get(i).get_id() == event.get_id())
            {
                events.remove(i);
                mAdapter.eventFragment.refreshRecyclerView();
            }
        }

    }

    public static class SchedulerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        NotesFragment notesFragment;
        EventFragment eventFragment;
        CalendarFragment calendarFragment;

        public SchedulerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    this.notesFragment = NotesFragment.newInstance();
                    return notesFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    this.calendarFragment = CalendarFragment.newInstance();
                    return calendarFragment;
                case 2: // Fragment # 1 - This will show SecondFragment
                    this.eventFragment = EventFragment.newInstance();
                    return eventFragment;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void dialogHelp(){
        LayoutInflater li = LayoutInflater.from(this);
        View dView = li.inflate(R.layout.dialog_help, null);

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dView);


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
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }
}
