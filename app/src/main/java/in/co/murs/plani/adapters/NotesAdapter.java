package in.co.murs.plani.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.murs.plani.PlanIApplication;
import in.co.murs.plani.R;
import in.co.murs.plani.SchedulerActivity;
import in.co.murs.plani.SingleEventActivity;
import in.co.murs.plani.Utils;
import in.co.murs.plani.models.Note;
import in.co.murs.plani.views.CustomAlertDialog;

public class NotesAdapter extends RecyclerSwipeAdapter<NotesAdapter.NoteViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<Note> mDataset;

    public NotesAdapter(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        if (activity instanceof SchedulerActivity) {
            this.mDataset = ((SchedulerActivity) activity).notes;
        }
        if (activity instanceof SingleEventActivity) {
            this.mDataset = ((SingleEventActivity) activity).notes;
        }
        if (this.mDataset == null || this.mDataset.size() == 0) {
            this.mDataset = new ArrayList<Note>();
            // TODO: 7/13/2016 show no notes
        }
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
        final NoteViewHolder noteViewHolder = new NoteViewHolder(view, mContext);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder viewHolder, final int position) {
        final Note note = mDataset.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        if (mActivity instanceof SingleEventActivity) {
            viewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        }
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.ivDelete));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.ivAlarm));
            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 7/13/2016 show full note
                IconicsDrawable icon = new IconicsDrawable(mContext)
                        .icon(FontAwesome.Icon.faw_pencil_square_o)
                        .color(Color.WHITE)
                        .sizeDp(20);

                CustomAlertDialog builder = new CustomAlertDialog(mContext);
                builder.setNoteData(icon, "", "", "Do you want to delete this note?", "");
                builder.setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    PlanIApplication.db.deleteNote(mDataset.get(position).get_id());
                                    if (mActivity instanceof SchedulerActivity)
                                        ((SchedulerActivity) mActivity).notes.remove(position);
                                    if (mActivity instanceof SingleEventActivity) {
                                        ((SingleEventActivity) mActivity).notes.remove(position);
                                        ((SingleEventActivity) mActivity).notesUpdated = true;
                                    }
                                    refreshRecyclerView();
                                    Toast.makeText(mContext, "Note Deleted", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(viewHolder.ivDelete);
                                }
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
        });

        viewHolder.ivAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Utils.dialogAlarm(mActivity, 0, note.get_id());
                } catch (Exception e) {
                    e.printStackTrace();
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(viewHolder.ivAlarm);
                }
            }
        });

        viewHolder.tvTitle.setText(note.getTitle());
        viewHolder.tvTime.setReferenceTime(note.getTime());
        viewHolder.tvDescription.setText(note.getDescription());
        if (note.getEvent() > 0l && !(mActivity instanceof SingleEventActivity)) {
            try {
                viewHolder.tvProject.setText(PlanIApplication.getDb().getEventTitle(note.getEvent()));
            } catch (Exception e) {
                viewHolder.tvProject.setText("");
            }

        } else
            viewHolder.tvProject.setText("");

        viewHolder.llNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 7/13/2016 show full note
                IconicsDrawable icon = new IconicsDrawable(mContext)
                        .icon(FontAwesome.Icon.faw_pencil_square_o)
                        .color(Color.WHITE)
                        .sizeDp(20);

                String project = "";
                if (note.getEvent() > 0l) {
                    try {
                        project = PlanIApplication.getDb().getEventTitle(note.getEvent());
                    } catch (Exception e) {
                        project = "";
                    }
                }

                CustomAlertDialog builder = new CustomAlertDialog(mContext);
                builder.setNoteData(icon, project, Utils.timeToString(note.getTime()), note.getTitle(), note.getDescription());
                builder.setCancelable(false)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //MyActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //dialog.cancel();
                            }
                        });
                AlertDialog alertdialog = builder.create();
                alertdialog.show();
                alertdialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
            }
        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    public void refreshRecyclerView() {
        if (mActivity instanceof SchedulerActivity)
            this.mDataset = ((SchedulerActivity) mActivity).notes;
        if (mActivity instanceof SingleEventActivity)
            this.mDataset = ((SingleEventActivity) mActivity).notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.swipe)
        SwipeLayout swipeLayout;

        @BindView(R.id.llNote)
        LinearLayout llNote;

        @BindView(R.id.tvDescription)
        TextView tvDescription;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvProject)
        TextView tvProject;

        @BindView(R.id.tvTime)
        RelativeTimeTextView tvTime;

        @BindView(R.id.ivAlarm)
        ImageButton ivAlarm;

        @BindView(R.id.ivDelete)
        ImageButton ivDelete;

        public NoteViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivDelete.setImageDrawable(new IconicsDrawable(context)
                    .icon(FontAwesome.Icon.faw_trash)
                    .color(Color.WHITE)
                    .sizeDp(30));

            ivAlarm.setImageDrawable(new IconicsDrawable(context)
                    .icon(FontAwesome.Icon.faw_clock_o)
                    .color(Color.WHITE)
                    .sizeDp(30));
        }
    }
}