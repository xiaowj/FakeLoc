package win.xiaowj.fakeloc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by user on 2017/1/10 010.
 */

public class Utils {

    public static Drawable getIconByPackageName(PackageManager packageManager, String packName, Context mContext) {
        try {
            return packageManager.getApplicationIcon(packName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return mContext.getResources().getDrawable(R.mipmap.ic_launcher);
        }
    }
}
