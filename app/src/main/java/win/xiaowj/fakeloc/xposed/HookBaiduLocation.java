package win.xiaowj.fakeloc.xposed;

import com.baidu.location.BDLocation;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by user on 2017/1/4 004.
 */

public class HookBaiduLocation implements IXposedHookLoadPackage {

    private final String CLAZZ_NAME = "com.baidu.location.LocationClient";
    private XSharedPreferences xSharedPreferences;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("load app : " + loadPackageParam.packageName);

        String packageName = loadPackageParam.packageName;

        xSharedPreferences = new XSharedPreferences("win.xiaowj.fakeloc", "app_conf");
        xSharedPreferences.makeWorldReadable();
        xSharedPreferences.reload();
        XposedBridge.log("packageName hashCode " + loadPackageParam.packageName.hashCode());
        XposedBridge.log("package is openHook " + xSharedPreferences.getBoolean(loadPackageParam.packageName.hashCode() + "_openHook", false));
        if (!xSharedPreferences.getBoolean(loadPackageParam.packageName.hashCode() + "_openHook", false)) {
            return;
        }

        String latStr = xSharedPreferences.getString(packageName.hashCode() + "_latitude", "0");
        String longStr = xSharedPreferences.getString(packageName.hashCode() + "_longitude", "0");
        XposedBridge.log("from sp latitude" + latStr);
        XposedBridge.log("from sp longitude" + longStr);
        final double latitude = Double.parseDouble(latStr);
        final double longitude = Double.parseDouble(longStr);
        XposedBridge.log("from parse latitude" + latitude);
        XposedBridge.log("from parse longitude" + longitude);

        if (latitude == 0.0 | longitude == 0.0) {
            XposedBridge.log("不是有效的位置");
            return;
        }

        XposedHelpers.findAndHookMethod(CLAZZ_NAME, loadPackageParam.classLoader, "callListeners", Integer.TYPE, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("开始Hook  callListeners");
                Object location = XposedHelpers.getObjectField(param.thisObject, "mLastLocation");
                double mLatitude = XposedHelpers.getDoubleField(location, "mLatitude");
                double mLongitude = XposedHelpers.getDoubleField(location, "mLongitude");
                XposedBridge.log("原始纬度" + mLatitude);
                XposedBridge.log("原始经度" + mLongitude);
                XposedHelpers.setObjectField(location, "mLatitude", latitude);
                XposedHelpers.setObjectField(location, "mLongitude", longitude);
                XposedHelpers.setObjectField(param.thisObject, "mLastLocation", location);
            }
        });

        XposedHelpers.findAndHookMethod(CLAZZ_NAME, loadPackageParam.classLoader, "sendFirstLoc", BDLocation.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("开始Hook  sendFirstLoc");
                BDLocation location = (BDLocation) param.args[0];
                double mLatitude = location.getLatitude();
                double mLongitude = location.getLongitude();
                XposedBridge.log("原始纬度" + mLatitude);
                XposedBridge.log("原始经度" + mLongitude);
                location.setLongitude(longitude);
                location.setLatitude(latitude);
                param.args[0] = location;
            }
        });

//        XposedHelpers.findAndHookMethod(CLAZZ_NAME, loadPackageParam.classLoader, "onNewNotifyLocation", Message.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                XposedBridge.log("开始Hook  onNewNotifyLocation");
//            }
//        });
    }
}
