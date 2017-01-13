package win.xiaowj.fakeloc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class BaiduMapActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;

    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    private String longitude;
    private String latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map);

        fetchIntent();
        initView();
    }

    private void fetchIntent(){
        Intent intent  = getIntent();
        longitude = intent.getStringExtra(LONGITUDE);
        latitude = intent.getStringExtra(LATITUDE);
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        try {
            MapStatus.Builder builder = new MapStatus.Builder();
            MapStatus status = builder.target(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude))).build();
            MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(status);
            baiduMap.setMapStatus(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapStatus status = baiduMap.getMapStatus();
                LatLng latLng = status.target;
                setResult(1,new Intent().putExtra(LONGITUDE,latLng.longitude).putExtra(LATITUDE,latLng.latitude));
                BaiduMapActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
