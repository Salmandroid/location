package location.salmandroid.com.locationproject;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import location.salmandroid.com.locationutils.LocationApi;

public class MainActivity extends AppCompatActivity {
    LocationApi locationApi;
    Button btnLastLocation, btnIslocationAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initForm();
        locationApi = LocationApi.getInstance(this, new LocationApi.mLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("gholampour", "location lat= " + location.getLatitude() + " location lng= " + location.getLongitude());
            }

            @Override
            public void onPermissionError() {
                Log.e("gholampour", "error");
            }
        });
    }

    private void initForm() {
        btnIslocationAvailable = (Button) findViewById(R.id.btnIsLocationAvailable);
        btnLastLocation = (Button) findViewById(R.id.btnLastLocation);
        btnIslocationAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationAvailable();

            }
        });
        btnLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getLastLocation().toString() + "", Toast.LENGTH_LONG).show();

            }
        });

    }

    private Location getLastLocation() {
        return locationApi.lastLocation().blockingFirst();


    }


    private void checkLocationAvailable() {
        locationApi.checkHighAccuracyLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LocationSettingsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LocationSettingsResult locationSettingsResult) {
                        if (locationSettingsResult.getStatus().getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            Toast.makeText(MainActivity.this, "Location is not available", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Location is available", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
//                .subscribe(new Observer<LocationSettingsResult>() {
//                               @Override
//                               public void onCompleted() {
//
//                               }
//
//                               @Override
//                               public void onError(Throwable e) {
//                                   e.printStackTrace();
//
//                               }
//
//                               @Override
//                               public void onNext(LocationSettingsResult locationSettingsResult) {
//                                   if (locationSettingsResult.getStatus().getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
//                                       Toast.makeText(MainActivity.this, "Location is not available", Toast.LENGTH_LONG).show();
//                                   } else {
//                                       Toast.makeText(MainActivity.this, "Location is available", Toast.LENGTH_LONG).show();
//                                   }
//                               }
//
//
//                           }
//
//                );
    }

}
