package activities;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import Async.AsyncPoints;
import Async.AsyncTypes;
import Dialogs.DialogCitySelect;
import Dialogs.DialogProgress;
import Models.StructPoint;
import Models.StructType;
import Web.TypePhotoDownloader;
import ir.ncis.kojast.ActivityEnhanced;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class MainActivity extends ActivityEnhanced implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
    public static int cityId;
    private int typeId;
    private AppCompatButton btnViewRoad, btnViewSatellite;
    private ArrayList<Polyline> routes = new ArrayList<>();
    private ImageView imgMarkerType;
    private Marker touchMarker;
    private TextView txtMarkerName, txtMarkerDescription, txtMarkerDriving, txtMarkerWalking;
    private View infoWindowContent;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnViewRoad = findViewById(R.id.btnViewRoad);
        btnViewSatellite = findViewById(R.id.btnViewSatellite);

        findViewById(R.id.imgCity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogCitySelect(MainActivity.this, R.style.TransparentDialog).show();
            }
        });

        infoWindowContent = App.INFLATER.inflate(R.layout.marker_info, null);
        imgMarkerType = infoWindowContent.findViewById(R.id.imgMarkerType);
        txtMarkerName = infoWindowContent.findViewById(R.id.txtMarkerName);
        txtMarkerDescription = infoWindowContent.findViewById(R.id.txtMarkerDescription);
        txtMarkerDriving = infoWindowContent.findViewById(R.id.txtMarkerDriving);
        txtMarkerWalking = infoWindowContent.findViewById(R.id.txtMarkerWalking);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Resources r = getResources();
        final int s48 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());
        final int s8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        googleMap.setMyLocationEnabled(true);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);
        if (App.LOCATION != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(App.LOCATION.getLatitude(), App.LOCATION.getLongitude()), 15));
        }

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCameraIdle() {
                LatLng center = googleMap.getCameraPosition().target;
                ((TextView) findViewById(R.id.txtCenter)).setText(String.format("%8.4f , %8.4f", center.latitude, center.longitude));
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onMapClick(LatLng latLng) {
                ((TextView) findViewById(R.id.txtTouch)).setText(String.format("%8.4f , %8.4f", latLng.latitude, latLng.longitude));
                if (touchMarker != null) {
                    touchMarker.remove();
                    touchMarker = null;
                }
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getString(R.string.label_touch));
                touchMarker = googleMap.addMarker(markerOptions);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {
                    StructPoint point = (StructPoint) marker.getTag();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(point.lat, point.lng)));
                }
                if (App.LOCATION != null) {
                    showRoutes(new LatLng(App.LOCATION.getLatitude(), App.LOCATION.getLongitude()), marker, googleMap);
                } else {
                    App.toast(getString(R.string.error_location));
                }
                return true;
            }
        });

        btnViewRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                btnViewRoad.setBackgroundResource(R.drawable.button_dark);
                btnViewRoad.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryLight));
                btnViewSatellite.setBackgroundResource(R.drawable.button_light);
                btnViewSatellite.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
            }
        });

        btnViewSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                btnViewRoad.setBackgroundResource(R.drawable.button_light);
                btnViewRoad.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                btnViewSatellite.setBackgroundResource(R.drawable.button_dark);
                btnViewSatellite.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryLight));
            }
        });

        ((AppCompatCheckBox) findViewById(R.id.chkTraffic)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean fromUser) {
                googleMap.setTrafficEnabled(checkBox.isChecked());
            }
        });

        new AsyncTypes()
                .setOperations(new AsyncTypes.Operations() {
                    @Override
                    public void before() {
                    }

                    @Override
                    public void failure(String error) {
                        App.toast(error);
                    }

                    @Override
                    public void after(ArrayList<StructType> result) {
                        final ViewGroup lytButtons = findViewById(R.id.lytButtons);
                        lytButtons.removeAllViews();
                        for (final StructType type : result) {
                            final ImageView imageView = new ImageView(MainActivity.this);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            imageView.setLayoutParams(new ViewGroup.LayoutParams(s48, s48));
                            imageView.setPadding(s8, s8, s8, s8);
                            imageView.setImageResource(R.drawable.logo);
                            imageView.setTag(type);
                            new TypePhotoDownloader(type.id)
                                    .setOnCompleteListener(new TypePhotoDownloader.OnCompleteListener() {
                                        @Override
                                        public void OnComplete() {
                                            imageView.setImageDrawable(Drawable.createFromPath(App.DIR_DATA + "/photos/" + type.id + "_off.png"));
                                        }
                                    })
                                    .download();
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    typeId = type.id;
                                    int childCount = lytButtons.getChildCount();
                                    for (int i = 0; i < childCount; i++) {
                                        ImageView child = (ImageView) lytButtons.getChildAt(i);
                                        StructType type = (StructType) child.getTag();
                                        child.setImageDrawable(Drawable.createFromPath(App.DIR_DATA + "/photos/" + type.id + "_off.png"));
                                    }
                                    imageView.setImageDrawable(Drawable.createFromPath(App.DIR_DATA + "/photos/" + type.id + "_on.png"));
                                    if (cityId == 0) {
                                        App.toast(getString(R.string.message_city_select));
                                    } else {
                                        final DialogProgress dialogProgress = new DialogProgress(MainActivity.this);
                                        new AsyncPoints()
                                                .setOperations(new AsyncPoints.Operations() {
                                                    @Override
                                                    public void before() {
                                                        dialogProgress.show();
                                                    }

                                                    @Override
                                                    public void failure(String error) {
                                                        dialogProgress.dismiss();
                                                        App.toast(error);
                                                    }

                                                    @Override
                                                    public void after(ArrayList<StructPoint> result) {
                                                        dialogProgress.dismiss();
                                                        googleMap.clear();
                                                        if (App.LOCATION != null) {
                                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(App.LOCATION.getLatitude(), App.LOCATION.getLongitude()), 15));
                                                        }
                                                        if (result != null && result.size() > 0) {
                                                            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                                                            if (App.LOCATION != null) {
                                                                boundsBuilder.include(new LatLng(App.LOCATION.getLatitude(), App.LOCATION.getLongitude()));
                                                            }
                                                            for (StructPoint point : result) {
                                                                LatLng pointLatLng = new LatLng(point.lat, point.lng);
                                                                boundsBuilder.include(pointLatLng);
                                                                MarkerOptions markerOptions = new MarkerOptions()
                                                                        .icon(BitmapDescriptorFactory.fromPath(App.DIR_DATA + "/photos/" + type.id + "_marker.png"))
                                                                        .position(pointLatLng)
                                                                        .title(point.name);
                                                                googleMap.addMarker(markerOptions).setTag(point);
                                                            }
                                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 128));
                                                        } else {
                                                            App.toast(type.title + " " + getString(R.string.message_not_found));
                                                        }
                                                    }
                                                })
                                                .execute(cityId, typeId);
                                    }
                                }
                            });
                            lytButtons.addView(imageView);
                        }
                    }
                })
                .execute();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        StructPoint point = (StructPoint) marker.getTag();
        if (point != null) {
            imgMarkerType.setImageBitmap(BitmapFactory.decodeFile(App.DIR_DATA + "/photos/" + typeId + "_on.png"));
            txtMarkerName.setText(point.name);
            txtMarkerDescription.setText(point.description);
            txtMarkerDescription.setVisibility(View.VISIBLE);
        } else {
            imgMarkerType.setImageResource(R.drawable.logo);
            txtMarkerName.setText(String.format("%s %s", getString(R.string.label_location), getString(R.string.label_touch)));
            txtMarkerDescription.setText("");
            txtMarkerDescription.setVisibility(View.GONE);
        }
        return infoWindowContent;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        StructPoint point = (StructPoint) marker.getTag();
        if (point != null) {
            App.toast(point.name + "\n" + point.description);
        } else {
            App.toast(String.format("%s %s", getString(R.string.label_location), getString(R.string.label_touch)));
        }
    }

    private void showRoutes(LatLng start, final Marker marker, final GoogleMap map) {
        for (Polyline route : routes) {
            route.remove();
        }
        routes.clear();
        final boolean[] calculated = new boolean[]{false, false};
        GoogleDirection.withServerKey(getString(R.string.api_key))
                .from(start)
                .to(marker.getPosition())
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            txtMarkerDriving.setText(getRouteInfo(route.getLegList().get(0)));
                            calculated[0] = true;
                            if (calculated[1]) {
                                marker.showInfoWindow();
                            }
                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .color(ContextCompat.getColor(App.CONTEXT, R.color.colorPrimaryDark))
                                    .width(10);
                            for (LatLng point : route.getOverviewPolyline().getPointList()) {
                                polylineOptions.add(point);
                            }
                            routes.add(map.addPolyline(polylineOptions));
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
        GoogleDirection.withServerKey(getString(R.string.api_key))
                .from(start)
                .to(marker.getPosition())
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            txtMarkerWalking.setText(getRouteInfo(route.getLegList().get(0)));
                            calculated[1] = true;
                            if (calculated[0]) {
                                marker.showInfoWindow();
                            }
                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .color(ContextCompat.getColor(App.CONTEXT, R.color.colorPrimaryLight))
                                    .width(5);
                            for (LatLng point : route.getOverviewPolyline().getPointList()) {
                                polylineOptions.add(point);
                            }
                            routes.add(map.addPolyline(polylineOptions));
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private String getRouteInfo(Leg leg) {
        String distance = getString(R.string.label_distance) + " : " + leg.getDistance().getText().replace("km", "کیلومتر").trim();
        String duration = getString(R.string.label_duration) + " : " + leg.getDuration().getText()
                .replace("days", "روز")
                .replace("day", "روز")
                .replace("hours", "ساعت")
                .replace("hour", "ساعت")
                .replace("mins", "دقیقه")
                .replace("min", "دقیقه")
                .trim();
        if (duration.contains("روز")) {
            duration = duration.substring(0, duration.indexOf("روز") + 3) + " و " + duration.substring(duration.indexOf("روز") + 3, duration.length());
        }
        if (duration.contains("ساعت")) {
            duration = duration.substring(0, duration.indexOf("ساعت") + 4) + " و " + duration.substring(duration.indexOf("ساعت") + 4, duration.length());
        }
        return distance + "\n" + duration;
    }
}
