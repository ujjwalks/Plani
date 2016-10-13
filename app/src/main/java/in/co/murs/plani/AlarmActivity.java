package in.co.murs.plani;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.co.murs.plani.models.Event;
import in.co.murs.plani.models.Note;

public class AlarmActivity extends Activity {

    Ringtone r;

    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvStart)
    TextView tvStart;
    @BindView(R.id.tvProject)
    TextView tvProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        try {
            long id = getIntent().getLongExtra("id", 0l);
            int type = getIntent().getIntExtra("type", -1);

            if (type == 0) {
                Note note = PlanIApplication.db.getNote(id);
                tvTitle.setText(note.getTitle());
                tvDescription.setText(note.getDescription());
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                tvStart.setText(sdf.format(new Date(note.getTime())));
                if (note.getEvent() > 0l) {
                    String project = PlanIApplication.getDb().getEventTitle(note.getEvent());
                    tvProject.setText(note.getEvent() + " " + project);
                }
            }

            if (type == 1) {
                Event event = PlanIApplication.getDb().getEvent(id);
                tvTitle.setText(event.getTitle());
                tvDescription.setText(event.getDescription());
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                tvStart.setText("Starts at " + sdf.format(new Date(event.getStartTime())));
                if(event.getEndTime() > event.getStartTime())
                    tvDuration.setText(Utils.getDuration(event.getStartTime(), event.getEndTime()));
                else
                    tvDuration.setText("");
                if(event.getLocation() != null)
                    tvProject.setText(event.getLocation().getAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        r = RingtoneManager.getRingtone(getApplicationContext(), getAlarmUri());

        playSound();
    }

    private void playSound() {


        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    r.play();
                } catch (Throwable t) {
                    Log.i("Animation", "Thread  exception " + t);
                }
            }
        });
        background.start();
    }

    @OnClick(R.id.btnStopAlarm)
    public void stopAlarm(View view) {
        // TODO Auto-generated method stub
        r.stop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        r.stop();
    }                //Get an alarm sound. Try for an alarm. If none set, try notification,

    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            alert = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);

        }
        return alert;
    }
}
