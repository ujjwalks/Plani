package in.co.murs.plani;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.co.murs.plani.views.AdvOnDateSetListener;
import in.co.murs.plani.views.AdvOnTimeSetListener;

/**
 * Created by Ujjwal on 7/11/2016.
 */
public class Utils {
    public static String getCurrentTime(String format) {
        String currentTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        currentTime = sdf.format(new Date());
        return currentTime;
    }

    public static String getTimeToString(String format, long time) {
        String currentTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        currentTime = sdf.format(new Date(time));
        return currentTime;
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static String getDuration(long start, long end) {
        long diff = Math.abs(end - start);
        if (diff <= 60000) {
            return "1 min";
        } else if (diff <= 60 * 60000l) {
            return (int) Math.ceil(diff / 60000l) + " mins";
        } else if (diff <= 24 * 60 * 60000l) {
            return (int) Math.ceil(diff / 60 / 60000l) + " hrs";
        } else if (diff <= 30 * 24 * 60 * 60000l) {
            return (int) Math.ceil(diff / 24 / 60 / 60000l) + " days";
        } else if (diff <= 365 * 24 * 60 * 60000l) {
            return (int) Math.ceil(diff / 30 / 24 / 60 / 60000l) + " months";
        } else {
            return (int) Math.ceil(diff / 365 / 24 / 60 / 60000l) + " years";
        }
    }

    public static int compareDate(Date date1, Date date2) {
        if (getStartOfDay(date1).getTime() > getStartOfDay(date2).getTime())
            return 1;
        else if (getStartOfDay(date1).getTime() == getStartOfDay(date2).getTime())
            return 0;
        else
            return -1;
    }

    public static int compareTime(Date date1, Date date2) {
        long t1 = 0l, t2 = 0l;
        t1 = date1.getTime() - getStartOfDay(date1).getTime();
        t2 = date2.getTime() - getStartOfDay(date2).getTime();
        if (t1 > t2)
            return 1;
        else if (t1 == t2)
            return 0;
        else
            return -1;
    }

    public static String timeToString(long time) {
        String strTime = "";
        SimpleDateFormat oldTime = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat intTime = new SimpleDateFormat("MMM dd");
        SimpleDateFormat newTime = new SimpleDateFormat("MMM dd, HH:mm");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MMM");


        if (year.format(new Date(time)).equals(year.format(new Date()))) {
            if (month.format(new Date(time)).equals(month.format(new Date())))
                strTime = newTime.format(new Date(time));
            else
                strTime = intTime.format(new Date(time));
        } else
            strTime = oldTime.format(new Date(time));

        return strTime;
    }

    public static void dialogAlarm(final Context mContext, final int type, final long _id) {
        // get prompts.xml view
        View dView = LayoutInflater.from(mContext).inflate(R.layout.dialog_alarm, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dView);
        final EditText etDate = (EditText) dView.findViewById(R.id.etDate);
        final EditText etTime = (EditText) dView.findViewById(R.id.etTime);
        final Calendar myCalendar = Calendar.getInstance();
        ImageView ivIcon = (ImageView) dView.findViewById(R.id.ivIcon);

        if (type == 0) {
            ivIcon.setImageDrawable(new IconicsDrawable(mContext)
                    .icon(FontAwesome.Icon.faw_clock_o)
                    .color(Color.WHITE)
                    .sizeDp(24));
        }

        if (type == 1) {
            ivIcon.setImageDrawable(new IconicsDrawable(mContext)
                    .icon(FontAwesome.Icon.faw_calendar_plus_o)
                    .color(Color.WHITE)
                    .sizeDp(24));
        }

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnDateSetListener date = new AdvOnDateSetListener(etDate, myCalendar);
                new DatePickerDialog(mContext, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvOnTimeSetListener time = new AdvOnTimeSetListener(etTime, myCalendar);
                new TimePickerDialog(mContext, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Set Alarm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                try {

                                    //Create a new PendingIntent and add it to the AlarmManager
                                    Intent intent = new Intent(mContext, AlarmActivity.class);
                                    intent.putExtra("type", 0);
                                    intent.putExtra("id", _id);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                                            (int) (_id * 10 + type), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager alarmManager =
                                            (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), pendingIntent);
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                                    Toast.makeText(mContext, "Alarm set for " + sdf.format(new Date(myCalendar.getTimeInMillis())),
                                            Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

}
