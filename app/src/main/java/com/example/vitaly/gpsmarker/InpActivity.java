package com.example.vitaly.gpsmarker;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class InpActivity extends ActionBarActivity implements View.OnClickListener {

    EditText et_txt;
    TextView txt_crd;
    TextView txt_sts;

    Button btn_sv;
    Button btn_gps;

    //public String mode = "insert";

    public final static String MARK_INPUT = "name";

    public final static String MARK_LOC = "cord";

    public final static String MARK_ID = "id";

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        et_txt = (EditText) findViewById(R.id.et_txt);
        txt_crd = (TextView) findViewById(R.id.txt_crd);
        txt_sts = (TextView) findViewById(R.id.txt_sts);

        btn_sv = (Button) findViewById(R.id.btn_sv);
        btn_sv.setOnClickListener(this);

        btn_gps = (Button) findViewById(R.id.btn_gps);
        btn_gps.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        checkEnabled();
    }

    private void checkEnabled() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
            txt_sts.setText("включен");
        } else {
            txt_sts.setText("выключен");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            }

        private void showLocation(Location location) {

            if (location == null) return;

            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

                txt_crd.setText(formatLocation(location));
            }
        }

        private String formatLocation(Location location) {

            if (location == null) return "";

            return String.format("lat = %1$.4f, lon = %2$.4f", location.getLatitude(), location.getLongitude());
        }
    };

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.btn_gps:

                intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivity(intent);
                break;

            case R.id.btn_sv:

                String name = et_txt.getText().toString();
                String crd = txt_crd.getText().toString();

                intent = new Intent(this,MainActivity.class);

                intent.putExtra("name", name);
                intent.putExtra("cord", crd);

                setResult(RESULT_OK, intent);

                finish();
                break;
        }

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}



