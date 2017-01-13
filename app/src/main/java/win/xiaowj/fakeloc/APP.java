package win.xiaowj.fakeloc;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by user on 2017/1/10 010.
 */

public class APP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
