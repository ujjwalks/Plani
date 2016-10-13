package in.co.murs.plani;

import android.app.Application;

/**
 * Created by Ujjwal on 7/11/2016.
 */
public class PlanIApplication extends Application{
    private static String mRegId;
    public static PlanIApplication sInstance;
    public static DatabaseHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        db = new DatabaseHelper(this);
    }

    public static DatabaseHelper getDb(){
        return db;
    }

}
