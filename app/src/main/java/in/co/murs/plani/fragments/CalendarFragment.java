package in.co.murs.plani.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.github.clans.fab.FloatingActionButton;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.co.murs.plani.Constants;
import in.co.murs.plani.EventActivity;
import in.co.murs.plani.NoteActivity;
import in.co.murs.plani.PlanIApplication;
import in.co.murs.plani.R;
import in.co.murs.plani.SchedulerActivity;
import in.co.murs.plani.adapters.EventsAdapter;
import in.co.murs.plani.views.DividerItemDecoration;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by Ujjwal on 7/11/2016.
 */
public class CalendarFragment extends Fragment {

    /**
     * RecyclerView: The new recycler view replaces the list view. Its more modular and therefore we
     * must implement some of the functionality ourselves and attach it to our recyclerview.
     * <p/>
     * 1) Position items on the screen: This is done with LayoutManagers
     * 2) Animate & Decorate views: This is done with ItemAnimators & ItemDecorators
     * 3) Handle any touch events apart from scrolling: This is now done in our adapter's ViewHolder
     */

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private EventsAdapter mAdapter;

    @BindView(R.id.calView)
    CompactCalendarView mCompactCalendar;

    @BindView(R.id.fabEvent)
    FloatingActionButton fabEvent;

    @BindView(R.id.fabNote)
    FloatingActionButton fabNote;

    @BindView(R.id.tvUpcoming)
    TextView tvUpcoming;


    // newInstance constructor for creating fragment with arguments
    public static CalendarFragment newInstance() {
        CalendarFragment calendarFragment = new CalendarFragment();
        return calendarFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);

        fabEvent.setImageDrawable(new IconicsDrawable(getActivity())
                .icon(FontAwesome.Icon.faw_calendar_plus_o)
                .color(getResources().getColor(R.color.colorCalendarSelected))
                .sizeDp(24));

        fabNote.setImageDrawable(new IconicsDrawable(getActivity())
                .icon(FontAwesome.Icon.faw_pencil_square_o)
                .color(getResources().getColor(R.color.colorCalendarSelected))
                .sizeDp(24));

        try {
            List<in.co.murs.plani.models.Event> allEvents = PlanIApplication.getDb().getAllEvents();
            List<Event> events = new ArrayList<Event>();
            if(allEvents != null) {
                for (int i = 0; i < allEvents.size(); i++) {
                    Event event = new Event(R.color.colorPrimary, allEvents.get(i).getStartTime(), allEvents.get(i).getTitle());
                    events.add(event);
                }
                mCompactCalendar.addEvents(events);
                System.out.println(mCompactCalendar.getEvents(new Date()).size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Events not loaded!", Toast.LENGTH_LONG).show();
        }

        // define a listener to receive callbacks when certain events happen.
        mCompactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(java.util.Date dateClicked) {

                try {
                    ((SchedulerActivity)getActivity()).getEventForDate(dateClicked);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Events not loaded", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onMonthScroll(java.util.Date firstDayOfNewMonth) {
                try {
                    ((SchedulerActivity) getActivity()).setMonth(firstDayOfNewMonth.getTime());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        mAdapter = new EventsAdapter(getActivity(), getActivity(), 1);
        ((EventsAdapter) mAdapter).setMode(Attributes.Mode.Multiple);
        recyclerView.setAdapter(mAdapter);

        /* Listeners */
        recyclerView.setOnScrollListener(onScrollListener);
        return view;
    }

    public void setUpcomingTxt(String upcoming){
        tvUpcoming.setText(upcoming);
    }

    public void addEvent(in.co.murs.plani.models.Event event){
        Event calEvent = new Event(R.color.colorPrimary, event.getStartTime(), event.getTitle());
        List<Event> events = new ArrayList<Event>();
        events.add(calEvent);
        mCompactCalendar.addEvents(events);
    }

    public void removeEvent(String title, long startTime){
        Event calEvent = new Event(R.color.colorPrimary, startTime, title);
        List<Event> events = new ArrayList<Event>();
        events.add(calEvent);
        mCompactCalendar.removeEvents(events);
    }

    public void refreshRecyclerView(){
        mAdapter.refreshRecyclerView();
    }

    @OnClick(R.id.fabNote)
    public void onClickNewNote(View view){
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        getActivity().startActivityForResult(intent, Constants.NEW_NOTE_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @OnClick(R.id.fabEvent)
    public void onClickNewEvent(View view){
        Intent intent = new Intent(getActivity(), EventActivity.class);
        getActivity().startActivityForResult(intent, Constants.NEW_EVENT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
}
