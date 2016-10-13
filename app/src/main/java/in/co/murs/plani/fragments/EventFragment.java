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

import com.daimajia.swipe.util.Attributes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.co.murs.plani.Constants;
import in.co.murs.plani.EventActivity;
import in.co.murs.plani.R;
import in.co.murs.plani.adapters.EventsAdapter;
import in.co.murs.plani.views.DividerItemDecoration;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class EventFragment extends Fragment {

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

    // newInstance constructor for creating fragment with arguments
    public static EventFragment newInstance() {
        EventFragment eventFragment = new EventFragment();
        return eventFragment;
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
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.bind(this, view);
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        mAdapter = new EventsAdapter(getActivity(), getActivity(), 0);
        ((EventsAdapter) mAdapter).setMode(Attributes.Mode.Multiple);
        recyclerView.setAdapter(mAdapter);

        /* Listeners */
        recyclerView.setOnScrollListener(onScrollListener);
        return view;
    }

    public void refreshRecyclerView() {
        mAdapter.refreshRecyclerView();
    }

    @OnClick(R.id.fabAdd)
    public void onClickAdd(View v) {
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