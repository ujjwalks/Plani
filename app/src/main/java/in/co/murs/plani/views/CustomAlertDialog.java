package in.co.murs.plani.views;

/**
 * Created by Ujjwal on 7/13/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.murs.plani.R;

public class CustomAlertDialog extends AlertDialog.Builder {

    private final Context mContext;
    private TextView mTitle;
    private TextView mSubTitle;
    private ImageView mIcon;
    private TextView mPrimaryMessage;
    private TextView mSecondaryMessage;


    public CustomAlertDialog(Context context) {
        super(context);
        mContext = context;

        View customTitle = View.inflate(mContext, R.layout.dialog_alert_title, null);
        mTitle = (TextView) customTitle.findViewById(R.id.tvTitle);
        mSubTitle = (TextView) customTitle.findViewById(R.id.tvSubTitle);
        mIcon = (ImageView) customTitle.findViewById(R.id.ivIcon);
        setCustomTitle(customTitle);

        View customMessage = View.inflate(mContext, R.layout.dialog_alert_message, null);
        mPrimaryMessage = (TextView) customMessage.findViewById(R.id.tvMessage1);
        mSecondaryMessage = (TextView) customMessage.findViewById(R.id.tvMessage2);
        setView(customMessage);
    }

    public void setNoteData(Drawable icon, String title, String subTitle, String pMessage, String sMessage){
        if(icon != null)
            this.mIcon.setImageDrawable(icon);
        else
            this.mIcon.setVisibility(View.GONE);
        this.mTitle.setText(title);
        this.mSubTitle.setText(subTitle);
        this.mPrimaryMessage.setText(pMessage);
        this.mSecondaryMessage.setText(sMessage);
    }

    @Override
    public CustomAlertDialog setTitle(int textResId) {
        mTitle.setText(textResId);
        return this;
    }
    @Override
    public CustomAlertDialog setTitle(CharSequence text) {
        mTitle.setText(text);
        return this;
    }

    @Override
    public CustomAlertDialog setMessage(int textResId) {
        mPrimaryMessage.setText(textResId);
        return this;
    }

    @Override
    public CustomAlertDialog setMessage(CharSequence text) {
        mPrimaryMessage.setText(text);
        return this;
    }

    @Override
    public CustomAlertDialog setIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public CustomAlertDialog setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public CustomAlertDialog setSecondaryMessage(CharSequence text){
        mSecondaryMessage.setText(text);
        return this;
    }

}
