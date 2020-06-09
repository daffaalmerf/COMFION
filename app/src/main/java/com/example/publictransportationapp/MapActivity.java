package com.example.publictransportationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MaterialSearchBar mapBar;
    private FusedLocationProviderClient mFusedLocation;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private View mapView;
    private RippleBackground rippleBackground;

    private Location mLastLocation;
    private LocationCallback mCallback;

    private final float DEFAULT_ZOOM = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapBar = findViewById(R.id.mapBar);
        SupportMapFragment mapGenerator = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGenerator);
        Button mapButton = findViewById(R.id.mapButton);

        rippleBackground = findViewById(R.id.mapRipple);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this, "AIzaSyDJvhG8vymI8fnZK0deQ3UI5SxcNhjn-_s");
        placesClient = Places.createClient(this);

        final String description = getIntent().getStringExtra("description");
        final String crime = getIntent().getStringExtra("crime");

        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        mapGenerator.getMapAsync(this);

        mapBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text.toString(), true, null, true);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

                if(buttonCode == MaterialSearchBar.BUTTON_NAVIGATION){

                    mapBar.enableSearch();

                } else if(buttonCode == MaterialSearchBar.BUTTON_BACK){

                    mapBar.disableSearch();

                }

            }
        });

        mapBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("id")
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();

                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {

                        if(task.isSuccessful()){

                         FindAutocompletePredictionsResponse predictionsResponse = task.getResult();

                         if(predictionsResponse != null){

                             predictionList = predictionsResponse.getAutocompletePredictions();

                             List<String> suggestionList = new ArrayList<>();

                             for(int i = 0; i < predictionList.size(); i++){

                                 AutocompletePrediction prediction = predictionList.get(i);
                                 suggestionList.add(prediction.getFullText(null).toString());

                             }

                             mapBar.updateLastSuggestions(suggestionList);

                             if(!mapBar.isSuggestionsVisible()){

                                 mapBar.showSuggestionsList();

                             }

                         }

                        }

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mapBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {

                if(position < predictionList.size()){

                    AutocompletePrediction selectedPrediction = predictionList.get(position);
                    String suggestion = mapBar.getLastSuggestions().get(position).toString();
                    mapBar.setText(suggestion);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mapBar.clearSuggestions();

                        }
                    }, 1000);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                    if(inputMethodManager != null){

                        inputMethodManager.hideSoftInputFromWindow(mapBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                        String placeId = selectedPrediction.getPlaceId();

                        List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG);

                        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeField).build();

                        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {

                                Place place = fetchPlaceResponse.getPlace();

                                Log.i("locationTag", "place found: " + place.getName());

                                LatLng latLng = place.getLatLng();

                                if(latLng != null){

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                if(e instanceof ApiException){

                                    ApiException apiException = (ApiException) e;

                                    apiException.getStackTrace();

                                    int statusCode = apiException.getStatusCode();

                                    Log.i("locationTag", "place not found: " + e.getMessage());
                                    Log.i("locationTag", "status code: " + statusCode);

                                }

                            }
                        });

                    }

                }

            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng currentMap = mMap.getCameraPosition().target;

                final double latitude = currentMap.latitude;

                final double longitude = currentMap.longitude;

                final String address = getCompleteAddress(latitude, longitude);

                final String strLatitude = String.valueOf(latitude);

                final String strLongitude = String.valueOf(longitude);

                rippleBackground.startRippleAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        rippleBackground.stopRippleAnimation();
                        Intent reportIntent = new Intent(MapActivity.this, ReportActivity.class);
                        reportIntent.putExtra("address", address);
                        reportIntent.putExtra("latitude", strLatitude);
                        reportIntent.putExtra("longitude", strLongitude);
                        reportIntent.putExtra("description", description);
                        reportIntent.putExtra("crime", crime);
                        startActivity(reportIntent);
                        finish();

                    }
                }, 3000);



            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){

            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 32, 180);


        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                getCurrentLocation();

            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof ResolvableApiException){

                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;

                    try {

                        resolvableApiException.startResolutionForResult(MapActivity.this, 0);

                    } catch (IntentSender.SendIntentException ex) {

                        ex.printStackTrace();

                    }

                }

            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if(mapBar.isSuggestionsVisible()){

                    mapBar.clearSuggestions();

                }

                if(mapBar.isSearchEnabled()){

                    mapBar.disableSearch();

                }

                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){

            if(resultCode == RESULT_OK){

                getCurrentLocation();

            }
        }

    }

    private void getCurrentLocation(){

        mFusedLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if(task.isSuccessful()){

                    mLastLocation = task.getResult();

                    if(mLastLocation != null){

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), DEFAULT_ZOOM));

                    } else {

                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        mCallback = new LocationCallback(){

                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                if(locationResult != null){

                                    mLastLocation = locationResult.getLastLocation();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), DEFAULT_ZOOM));
                                    mFusedLocation.removeLocationUpdates(mCallback);
                                }

                            }
                        };

                        mFusedLocation.requestLocationUpdates(locationRequest, mCallback, null);

                    }

                } else {

                    Toast.makeText(MapActivity.this, "Unable to get last location", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private String getCompleteAddress(double latitude, double longitude){

        String address = "";

        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

        try{

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if(address != null){

                Address returnAddress = addresses.get(0);
                StringBuilder stringBuilder = new StringBuilder("");

                for(int i = 0; i <= returnAddress.getMaxAddressLineIndex(); i++){

                    stringBuilder.append(returnAddress.getAddressLine(i)).append("\n");

                }

                address = stringBuilder.toString();

            } else {

                Toast.makeText(MapActivity.this, "Address Not Found", Toast.LENGTH_SHORT).show();

            }


        } catch (Exception e){

            Toast.makeText(MapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        return address;

    }

}