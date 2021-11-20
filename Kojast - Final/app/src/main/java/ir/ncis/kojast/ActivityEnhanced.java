package ir.ncis.kojast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

@SuppressLint("Registered")
public class ActivityEnhanced extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private boolean exit = false;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.INFLATER == null) {
            App.INFLATER = getLayoutInflater();
        }

        locationRequest = createLocationRequest();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setSmallestDisplacement(2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void runActivity(Class targetActivityClass) {
        runActivity(targetActivityClass, false);
    }

    public void runActivity(Class targetActivityClass, boolean finish) {
        Intent intent = new Intent(this, targetActivityClass);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    public void checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog errorDialog = apiAvailability.getErrorDialog(this, resultCode, 1);
                errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        System.exit(0);
                    }
                });
                errorDialog.show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.error_not_supported))
                        .setMessage(getString(R.string.message_not_supported))
                        .setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.exit(0);
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        if (App.PERMISSION_ACL && App.PERMISSION_AFL) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        App.LOCATION = location;
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            System.exit(0);
        } else {
            exit = true;
            App.toast(getString(R.string.message_exit));
            App.HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }
}
