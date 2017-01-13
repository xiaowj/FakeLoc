package win.xiaowj.fakeloc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class PerAppSettingActivity extends AppCompatActivity {

    public static final String PACKAGE_INFO = "package_info";
    private PackageInfo info;
    private PackageManager packageManager;
    private EditText longitudeEdit;
    private EditText latitudeEdit;
    private ConstraintLayout layout;
    private SharedPreferences sharedPreferences;
    private boolean openHook;
    private Switch switchWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_app_setting);

        packageManager = getPackageManager();
        fetchIntent();
        initView();
        initData();
    }

    private void fetchIntent() {
        Intent intent = getIntent();
        info = intent.getParcelableExtra(PACKAGE_INFO);
    }

    private void initView() {
        ImageView appIcon = (ImageView) findViewById(R.id.appIcon);
        appIcon.setImageDrawable(Utils.getIconByPackageName(packageManager, info.packageName, this));
        TextView appName = (TextView) findViewById(R.id.appName);
        appName.setText(packageManager.getApplicationLabel(info.applicationInfo));
        TextView appPackageName = (TextView) findViewById(R.id.appPackage);
        appPackageName.setText(info.packageName);

        layout = (ConstraintLayout) findViewById(R.id.location_detail_layout);
        longitudeEdit = (EditText) findViewById(R.id.longitudeEdit);
        latitudeEdit = (EditText) findViewById(R.id.latitudeEdit);

        findViewById(R.id.baiduMapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerAppSettingActivity.this, BaiduMapActivity.class);
                intent.putExtra(BaiduMapActivity.LONGITUDE, longitudeEdit.getText().toString().trim());
                intent.putExtra(BaiduMapActivity.LATITUDE, latitudeEdit.getText().toString().trim());
                startActivityForResult(intent, 1);
            }
        });

        switchWidget = (Switch) findViewById(R.id.switch1);
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layout.setVisibility(View.VISIBLE);
                    openHook = true;
                } else {
                    layout.setVisibility(View.GONE);
                    openHook = false;
                }
            }
        });
    }

    private void initData() {
        sharedPreferences = getSharedPreferences("app_conf", MODE_PRIVATE);
        if (sharedPreferences.getBoolean(info.packageName.hashCode() + "_openHook", false)) {
            switchWidget.setChecked(true);
            layout.setVisibility(View.VISIBLE);
            latitudeEdit.setText(sharedPreferences.getString(info.packageName.hashCode() + "_latitude", "0.0"));
            longitudeEdit.setText(sharedPreferences.getString(info.packageName.hashCode() + "_longitude", "0.0"));
        } else {
            switchWidget.setChecked(false);
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            double longitude = data.getDoubleExtra(BaiduMapActivity.LONGITUDE, 0);
            double latitude = data.getDoubleExtra(BaiduMapActivity.LATITUDE, 0);

            longitudeEdit.setText(String.valueOf(longitude));
            latitudeEdit.setText(String.valueOf(latitude));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(info.packageName.hashCode() + "_openHook", openHook);
        if (openHook) {
            editor.putString(info.packageName.hashCode() + "_longitude", longitudeEdit.getText().toString().trim());
            editor.putString(info.packageName.hashCode() + "_latitude", latitudeEdit.getText().toString().trim());
        }
        editor.apply();
    }
}
