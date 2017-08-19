package location.salmandroid.com.locationutils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * Created by mushtu on 9/6/16.
 */
public class LocationApi implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LocationApi";

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private final Context context;
    private Observable<Location> lastKnownLocationObservable;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    mLocationListener listener;

    private static LocationApi instance = null;

    //a private constructor so no instances can be made outside this class
    private LocationApi(Context context) {
        this.context = context;
    }

    //Everytime you need an instance, call this
    //synchronized to make the call thread-safe
    public static  LocationApi getInstance(Context context, mLocationListener listener) {
        if (instance == null)
            instance = new LocationApi(context, listener);

        return instance;
    }


    private LocationApi(Context context, mLocationListener listener) {
        this.context = context;
        this.listener = listener;
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
//        lastKnownLocationObservable = locationProvider.getLastKnownLocation();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) != null) {
//                lastKnownLocationObservable = Observable.create(new Observable.OnSubscribe<Location>() {
//                    @Override
//                    public void call(Subscriber<? super Location> subscriber) {
//                        try {
//                            subscriber.onNext(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
//                            subscriber.onCompleted();
//                        } catch (Exception e) {
//                            subscriber.onError(e);
//                        }
//
//                    }
//                });
//            }
            } else {
                mGoogleApiClient.connect();
            }

        } else {
            listener.onPermissionError();

        }


        Log.d(TAG, "Location update started ..............: ");
    }

    public boolean isGoogleApiConnected() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
//            lastKnownLocationObservable = Observable.create(new Observable.OnSubscribe<Location>() {
//                @Override
//                public void call(Subscriber<? super Location> subscriber) {
//                    try {
//                        Log.d(TAG, "Firing onLocationChanged..............................................");
//
//                        subscriber.onNext(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
//                        subscriber.onCompleted();
//                    } catch (Exception e) {
//                        subscriber.onError(e);
//                    }
//
//                }
//            });
            listener.onLocationChanged(location);

        }
    }
//    public Observable<List<Address>> address(final Double lat, final Double lng) {
//        return Observable.create(new Observable.OnSubscribe<List<Address>>() {
//            @Override
//            public void call(Subscriber<? super List<Address>> subscriber) {
//                Geocoder geoCoder = new Geocoder(context, locale);
//                try {
//                    List<Address> fromLocation = geoCoder.getFromLocation(lat, lng, 1);
//                    subscriber.onNext(fromLocation);
//                    subscriber.onCompleted();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    subscriber.onError(e);
//                }
//            }
//        });
//    }

//    public Observable<Location> locationUpdate(long interval) {
//        final LocationRequest locationRequest = LocationRequest.create()
//                .setSmallestDisplacement(10)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setFastestInterval(interval)
//                .setInterval(interval);
//        return locationProvider.checkLocationSettings(
//                new LocationSettingsRequest.Builder()
//                        .addLocationRequest(locationRequest)
//                        .setAlwaysShow(true)  //Refrence: http://stackoverflow.com/questions/29824408/google-play-services-locationservices-api-new-option-never
//                        .build())
//                .flatMap(new Func1<LocationSettingsResult, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(final LocationSettingsResult locationSettingsResult) {
//                        Status status = locationSettingsResult.getStatus();
//                        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
//                            LocationResolutionRequiredException ex = new LocationResolutionRequiredException();
//                            return Observable.error(ex);
//                        } else
//                            return Observable.create(new Observable.OnSubscribe<LocationSettingsResult>() {
//                                @Override
//                                public void call(Subscriber<? super LocationSettingsResult> subscriber) {
//                                    subscriber.onNext(locationSettingsResult);
//                                    subscriber.onCompleted();
//                                }
//                            });
//                    }
//                }).flatMap(new Func1<Object, Observable<Location>>() {
//                    @Override
//                    public Observable<Location> call(Object o) {
//                        System.out.println("Request Location Update!!!!!");
//                        Log.e("gholampour", "Request Location Update!!!!!");
//
//                        return locationProvider.getUpdatedLocation(locationRequest);
//                    }
//                });
//        //return locationUpdatesObservable;
//    }

    public Observable<Location> lastLocation() {
        return Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> e) throws Exception {
                try {
                    Log.e("gholampour googleApi",LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
                    e.onNext(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
                    e.onComplete();
                } catch (Exception e1) {
                    e.onError(e1);
                }
            }
        });

//        return Observable.create(new Observable.OnSubscribe<Location>() {
//            @Override
//            public void call(Subscriber<? super Location> subscriber) {
//                try {
//                    Log.e("gholampour googleApi",LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
//                    subscriber.onNext(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
//
//            }
//        });
    }

//    public boolean isLocationAvailable() {
//        return checkHighAccuracyLocation().blockingFirst();
//
//
////    return checkHighAccuracyLocation().toBlocking().first();
//    }

    public Observable<LocationSettingsResult> checkHighAccuracyLocation() {
        return Observable.create(new ObservableOnSubscribe<LocationSettingsResult>() {
            @Override
            public void subscribe(final ObservableEmitter<LocationSettingsResult> e) throws Exception {
                try {

                    final LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest)
                            .setAlwaysShow(true)
                            .build();
                    final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest);
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(LocationSettingsResult locationSettingsResult) {
                            e.onNext(locationSettingsResult);
                            e.onComplete();
                        }
                    });


                } catch (Exception e1) {
                    e.onError(e1);
                }
            }
        });

//        return Observable.create(new Observable.OnSubscribe<LocationSettingsResult>() {
//            @Override
//            public void call(final Subscriber<? super LocationSettingsResult> subscriber) {
//                try {
//
//                    final LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
//                            .addLocationRequest(mLocationRequest)
//                            .setAlwaysShow(true)
//                            .build();
//                    final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest);
//                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//                        @Override
//                        public void onResult(LocationSettingsResult locationSettingsResult) {
//                            subscriber.onNext(locationSettingsResult);
//                            subscriber.onCompleted();
//                        }
//                    });
//
//
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
//            }
//        });


    }
    public Context getContext() {
        return context;
    }

    public interface mLocationListener {
        void onLocationChanged(Location location);

        void onPermissionError();
    }
}
