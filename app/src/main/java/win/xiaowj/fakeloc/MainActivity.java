package win.xiaowj.fakeloc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<PackageInfo> appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        getAppData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.id_recycleView);
    }


    private void getAppData() {
        if (appList == null) {
            appList = new ArrayList<PackageInfo>();
        }
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> infos = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES);
        for (PackageInfo info : infos) {
            String[] permissions = info.requestedPermissions;
            if (permissions == null || permissions.length == 0) {
                continue;
            }

            boolean flag = false;
            //遍历权限，如果有请求定位的APP就加入列表
            for (String permission : permissions) {
                if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)
                        || Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)
                        || Manifest.permission.LOCATION_HARDWARE.equals(permission)) {
                    ServiceInfo[] serviceInfos = info.services;
                    if (serviceInfos == null) {
                        break;
                    }
                    for (ServiceInfo serviceInfo : serviceInfos) {
                        if ("com.baidu.location.f".equals(serviceInfo.name)) {
                            appList.add(info);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            }
        }
        Log.e("Xposed", String.valueOf(appList.size()));

        initAdapter(packageManager);
    }

    private void initAdapter(PackageManager packageManager) {
        RecycleViewAdapter adapter = new RecycleViewAdapter(appList, this, packageManager);
        adapter.setOnItemClickListener(new RecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(PackageInfo packageInfo, int position) {
                startActivity(new Intent(MainActivity.this, PerAppSettingActivity.class).putExtra(PerAppSettingActivity.PACKAGE_INFO, packageInfo));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

}
