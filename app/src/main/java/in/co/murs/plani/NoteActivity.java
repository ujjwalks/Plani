package in.co.murs.plani;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.murs.plani.models.Note;

public class NoteActivity extends AppCompatActivity {

    ActionBar mActionBar;

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.etDescription)
    EditText etDescription;

    private long mEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        setTitle("New Note");
        setUpActionBar();

        ButterKnife.bind(this);

        mEvent = getIntent().getLongExtra("event", 0l);
    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_save:
                // TODO: 7/12/2016 save note
                try {
                    Note note = null;
                    if (validateNote()) {
                        note = new Note(etTitle.getText().toString(), etDescription.getText().toString(),
                                new Date().getTime(), mEvent);
                        note.set_id(PlanIApplication.getDb().addNote(note));
                        Intent intent = new Intent();
                        intent.putExtra("note", note);
                        setResult(Constants.NEW_NOTE_REQUEST_CODE, intent);
                        this.finish();
                    }
                } catch (Exception e) {

                }
                return true;
        default:
            return super.onOptionsItemSelected(item);
    }

}

    private boolean validateNote() {
        boolean check = true;

        if (TextUtils.isEmpty(etTitle.getText())) {
            etTitle.setError("Cannot be empty");
            check = false;
        }

        return check;
    }
}
