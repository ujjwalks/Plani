package in.co.murs.plani.views;

import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ujjwal on 7/13/2016.
 */
public class AdvOnTimeSetListener implements TimePickerDialog.OnTimeSetListener{
    EditText etTime;
    Calendar myCalendar;

    public AdvOnTimeSetListener(EditText etTime, Calendar myCalendar){
        this.etTime = etTime;
        this.myCalendar = myCalendar;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        myCalendar.set(Calendar.HOUR_OF_DAY, i);
        myCalendar.set(Calendar.MINUTE, i1);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String date = sdf.format(new Date(myCalendar.getTimeInMillis()));
        etTime.setText(date);
    }
}
