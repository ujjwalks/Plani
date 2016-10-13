package in.co.murs.plani.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.murs.plani.Constants;
import in.co.murs.plani.PlanIApplication;
import in.co.murs.plani.R;
import in.co.murs.plani.SchedulerActivity;
import in.co.murs.plani.SingleEventActivity;
import in.co.murs.plani.Utils;
import in.co.murs.plani.models.Event;
import in.co.murs.plani.views.CustomAlertDialog;

public class EventsAdapter extends RecyclerSwipeAdapter<EventsAdapter.EventViewHolder>{
    private Context mContext;
    private List<Event> mDataset;
    private Activity mActivity;

    private int type;

    public EventsAdapter(Context context, Activity activity, int type) {
        this.mContext = context;
        this.mActivity = activity;
        this.type = type;
        if(activity instanceof SchedulerActivity){
            if(type == 0)
                this.mDataset = ((SchedulerActivity) activity).events;
            if(type == 1)
                this.mDataset = ((SchedulerActivity) activity).upcomingEvents;
        }

        if(this.mDataset == null || this.mDataset.size() == 0){
            this.mDataset = new ArrayList<Event>();
            // TODO: 7/13/2016 show no notes
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        final EventViewHolder eventViewHolder = new EventViewHolder(view, mContext);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(final EventViewHolder viewHolder, final int position) {
        final Event event = mDataset.get(position);
        if(type == 1)
            viewHolder.swipeLayout.setSwipeEnabled(false);
        if(type == 0) {
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
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
        }
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 7/13/2016 show full note
                IconicsDrawable icon = new IconicsDrawable(mContext)
                        .icon(FontAwesome.Icon.faw_calendar_minus_o)
                        .color(Color.WHITE)
                        .sizeDp(20);

                CustomAlertDialog builder = new CustomAlertDialog(mContext);
                builder.setNoteData(icon, "", "", "Do you want to delete this event?", "");
                builder.setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    if(mDataset.get(position).getLocation() != null)
                                        PlanIApplication.db.deleteEvent(mDataset.get(position).get_id(), mDataset.get(position).getLocation().get_id());
                                    else
                                        PlanIApplication.db.deleteEvent(mDataset.get(position).get_id(), 0l);

                                    if(mActivity instanceof SchedulerActivity)
                                        ((SchedulerActivity) mActivity).onEventDelete(mDataset.get(position), 0l);

                                    Toast.makeText(mContext, "Event Deleted", Toast.LENGTH_SHORT).show();
                                }catch(Exception e){
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
                    Utils.dialogAlarm(mContext, 1, event.get_id());
                }catch(Exception e){
                    e.printStackTrace();
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(viewHolder.ivAlarm);
                }
            }
        });

        viewHolder.llEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SingleEventActivity.class);
                intent.putExtra("event", event);
                mActivity.startActivityForResult(intent, Constants.SINGLE_EVENT_ACTIVITY);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewHolder.tvTitle.setText(event.getTitle());
        viewHolder.tvTime.setText(Utils.timeToString(event.getStartTime()));
        if(event.getLocation() != null){
            if(TextUtils.isEmpty(event.getLocation().getAddress())){
                viewHolder.tvDescription.setText(event.getDescription());
            }else{
                viewHolder.tvDescription.setText(event.getLocation().getAddress());
            }
        }else{
            viewHolder.tvDescription.setText(event.getDescription());
        }

        if(event.getStartTime() > 0l && event.getStartTime() > 0l) {
            viewHolder.tvDuration.setText(Utils.getDuration(event.getStartTime(), event.getEndTime()) + " duration");
        }else {
            viewHolder.tvDuration.setText("");
        }

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void refreshRecyclerView(){
        if(type == 0)
            this.mDataset = ((SchedulerActivity) mActivity).events;
        if(type == 1)
            this.mDataset = ((SchedulerActivity) mActivity).upcomingEvents;
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.swipe)
        SwipeLayout swipeLayout;

        @BindView(R.id.llEvent)
        LinearLayout llEvent;

        @BindView(R.id.tvDescription)
        TextView tvDescription;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvDuration)
        TextView tvDuration;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.ivAlarm)
        ImageButton ivAlarm;

        @BindView(R.id.ivDelete)
        ImageButton ivDelete;

        public EventViewHolder(View itemView, Context context) {
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