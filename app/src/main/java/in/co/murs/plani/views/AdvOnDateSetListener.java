package in.co.murs.plani.views;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ujjwal on 7/13/2016.
 */
public class AdvOnDateSetListener implements DatePickerDialog.OnDateSetListener {
    EditText etDate;

    Calendar myCalendar;

    public AdvOnDateSetListener(EditText etDate, Calendar myCalendar){
        this.etDate = etDate;
        this.myCalendar = myCalendar;
    }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        myCalendar.set(Calendar.YEAR, i);
        myCalendar.set(Calendar.MONTH, i1);
        myCalendar.set(Calendar.DAY_OF_MONTH, i2);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String date = sdf.format(new Date(myCalendar.getTimeInMillis()));
        etDate.setText(date);
    }
}
