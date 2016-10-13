package in.co.murs.plani;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.clans.fab.FloatingActionButton;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.calView)
    CompactCalendarView mCompactCalendar;

    @BindView(R.id.fabEvent)
    FloatingActionButton fabEvent;

    @BindView(R.id.fabNote)
    FloatingActionButton fabNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupFabView();

        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        Event ev1 = new Event(Color.GREEN, 1433701251000L, "Some extra data that I want to store.");
        mCompactCalendar.addEvent(ev1);

        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
        Event ev2 = new Event(Color.GREEN, 1433704251000L);
        mCompactCalendar.addEvent(ev2);

        // Query for events on Sun, 07 Jun 2015 GMT.
        // Time is not relevant when querying for events, since events are returned by day.
        // So you can pass in any arbitary DateTime and you will receive all events for that day.
        List<Event> events = mCompactCalendar.getEvents(1433701251000L); // can also take a Date object

        // events has size 2 with the 2 events inserted previously
        Log.d(Constants.TAG, "Events: " + events);

        // define a listener to receive callbacks when certain events happen.
        mCompactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(java.util.Date dateClicked) {
                List<Event> events = mCompactCalendar.getEvents(dateClicked);
                Log.d(Constants.TAG, "Day was clicked: " + dateClicked + " with events " + events);
            }

            @Override
            public void onMonthScroll(java.util.Date firstDayOfNewMonth) {
                Log.d(Constants.TAG, "Month was scrolled to: " + firstDayOfNewMonth);
            }

        });
    }

    private void setupFabView(){
        fabEvent.setImageDrawable(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_calendar_plus_o)
                .color(getResources().getColor(R.color.colorCalendarSelected))
                .sizeDp(24));

        fabNote.setImageDrawable(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_pencil_square_o)
                .color(getResources().getColor(R.color.colorCalendarSelected))
                .sizeDp(24));
    }
}
