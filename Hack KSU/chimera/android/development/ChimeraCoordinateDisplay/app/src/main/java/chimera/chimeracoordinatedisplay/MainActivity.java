package chimera.chimeracoordinatedisplay;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;
import java.lang.Runnable;
import static java.util.concurrent.TimeUnit.*;

import com.loopj.android.http.*;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    EditText editName;
    EditText editPhone;
    LocationManager lm;
    LocationListener locationListener;
    String name = "";
    String phone = "";
    private ScheduledExecutorService scheduleTaskExecutor;
    Location location;

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(locationListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long timer = 0;
        super.onCreate(savedInstanceState);
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                postLocationData(location);
            }
        }, 0, 5, TimeUnit.SECONDS);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        editName = (EditText) findViewById(R.id.editName);
        editPhone = (EditText) findViewById(R.id.editPhone);
        assert button != null;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                postLocationData(location);
            }
        });

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getBaseContext(), "Allow Location in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                postLocationData(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    public void postLocationData(Location location) {
        if (true) { //location != null && !name.isEmpty() && !phone.isEmpty()
            name = editName.getText().toString();
            phone = editPhone.getText().toString();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            HashMap<String, String> hashMap = new HashMap<>();
            String urlString = "http://1417f1c8.ngrok.io/?name=" + name + "&num=" + phone + "&lat="
                    + latitude + "&long=" + longitude;
            //textLatitude.setText(Double.toString(latitude));
           // textLongitude.setText(Double.toString(longitude));
            //params.put("name", name);
            //params.put("num", phone);
            //params.put("lat", Double.toString(latitude));
            //params.put("long", Double.toString(longitude));
//            hashMap.put("lat", "30");
//            hashMap.put("long", "-69");
//            hashMap.put("name", "Mitchell");
//            hashMap.put("num", "9139046044");

            //RequestParams params = new RequestParams(hashMap);
            AsyncHttpClient client = new AsyncHttpClient();
//
            client.get(urlString, new AsyncHttpResponseHandler() {
                    @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getBaseContext(), "Data sent.",
                                Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getBaseContext(), "Data sent.",
                            Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            Toast.makeText(getBaseContext(), "No GPS Data", Toast.LENGTH_SHORT).show();
        }
    }
}