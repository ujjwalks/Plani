package in.co.murs.plani;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.murs.plani.models.Address;
import in.co.murs.plani.models.Event;
import in.co.murs.plani.views.AdvOnDateSetListener;
import in.co.murs.plani.views.AdvOnTimeSetListener;

public class EventActivity extends AppCompatActivity {

    ActionBar mActionBar;

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.etDescription)
    EditText etDescription;

    @BindView(R.id.etEndDate)
    EditText etEndDate;

    @BindView(R.id.etEndTime)
    EditText etEndTime;

    @BindView(R.id.etStartDate)
    EditText etStartDate;

    @BindView(R.id.etStartTime)
    EditText etStartTime;

    @BindView(R.id.etLocation)
    EditText etLocation;

    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setTitle("New Event");
        setUpActionBar();
        ButterKnife.bind(this);
        setupView();
    }

    private void setupView() {

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnDateSetListener date = new AdvOnDateSetListener(etStartDate, startCalendar);
                new DatePickerDialog(EventActivity.this, date, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnTimeSetListener time = new AdvOnTimeSetListener(etStartTime, startCalendar);
                new TimePickerDialog(EventActivity.this, time, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnDateSetListener date = new AdvOnDateSetListener(etEndDate, endCalendar);
                new DatePickerDialog(EventActivity.this, date, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnTimeSetListener time = new AdvOnTimeSetListener(etEndTime, endCalendar);
                new TimePickerDialog(EventActivity.this, time, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        });

    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_save:
                // TODO: 7/12/2016 save note
                try {
                    if (validateEvent()) {
                        Address address = null;
                        if (!TextUtils.isEmpty(etLocation.getText())) {
                            address = new Address(0l, 0l, etLocation.getText().toString());
                            address.set_id(PlanIApplication.db.addAddress(new Address(0l, 0l, etLocation.getText().toString())));
                        }
                        Event event = new Event(etTitle.getText().toString(), etDescription.getText().toString(),
                                startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis(), address);
                        if(address != null)
                            event.set_id(PlanIApplication.getDb().addEvent(event, address.get_id()));
                        else
                            event.set_id(PlanIApplication.getDb().addEvent(event, 0l));
                        Intent intent = new Intent();
                        intent.putExtra("event", event);
                        setResult(Constants.NEW_EVENT_REQUEST_CODE, intent);
                        this.finish();
                    }

                    return true;
                }catch(Exception e){
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean validateEvent(){
        boolean check = true;

        if(TextUtils.isEmpty(etTitle.getText())) {
            etTitle.setError("Invalid Input");
            check = false;
        }else{
            etTitle.setError(null);
        }

        if(TextUtils.isEmpty(etStartDate.getText()) || Utils.compareDate(startCalendar.getTime(), new Date()) == -1){
            etStartDate.setError("Invalid Input");
            check = false;
        }else{
            etStartDate.setError(null);
        }

        if(TextUtils.isEmpty(etStartTime.getText())  ||
                (Utils.compareTime(startCalendar.getTime(), new Date()) != 1 &&
                        Utils.compareDate(startCalendar.getTime(), new Date()) == 0 )){
            etStartTime.setError("Invalid Input");
            check = false;
        }else{
            etStartTime.setError(null);
        }

        if(startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis()
                && (!TextUtils.isEmpty(etEndDate.getText()) || !TextUtils.isEmpty(etEndTime.getText()))){
            check = false;
            etEndDate.setError("Invalid End Time");
            etEndTime.setError("Invalid End Time");
        }else{
            etEndDate.setError(null);
            etEndTime.setError(null);
        }

        return check;
    }
}
