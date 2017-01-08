package ch.master.hes_so.alarmlocation.Maps;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import ch.master.hes_so.alarmlocation.Globals;
import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.R;

/**
 * Created by Warrior on 04.01.2017.
 */

public class MapViewFragmentSelectRule extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private SlidingUpPanelLayout slidingLayout;
    private int panelHeight;

    private TextView txt_title;
    private Switch sw_enabled;
    private EditText etxt_namePosition;
    private SeekBar sk_radius;
    private TextView txt_radius;
    private TextView time_start;
    private TextView time_end;
    private TextView alarmEnabled;
    private CheckBox restaurant, supermarket, foodStore, hairdresser, bar, cafe;

    private ImageView streetView;
    private Button add_position;
    private final static String LOGTAG = "MapViewFragment";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Location myLocation;

    private Element rule;

    OnMapRuleFragmentListener mCallback;


    public void modify_position(Element _rule) {
        rule = _rule;
    }

    public void add_new_position() {
        rule = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
        /*
         * If no resolution is available, display a dialog to the
         * user with the error.
         */
            Log.i(LOGTAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        handleNewLocation(location);
        Log.d(LOGTAG,"Location changed to " + location.toString());
    }

    public interface OnMapRuleFragmentListener {
        void OnReturnFromRule(Element _element);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMapRuleFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnReturnFromRule");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_select_rules_fragment, container, false);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout_rule);

        //Save half display height for sliding panel
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        panelHeight = size.y / 2;

        //Sliding layout hidden
        slidingLayout.setPanelHeight(panelHeight);

        //Save half display height for Mapview
        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.relLay_map);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = panelHeight;
        layout.setLayoutParams(params);

        //Create GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000) // 1 second, in milliseconds
                .setSmallestDisplacement(10);

        mGoogleApiClient.connect();

        //--- Get layout ---//
        txt_title = (TextView) rootView.findViewById(R.id.txt_title);
        sw_enabled = (Switch) rootView.findViewById(R.id.sw_enabled);
        etxt_namePosition = (EditText) rootView.findViewById(R.id.etxt_taskname);
        streetView = (ImageView) rootView.findViewById(R.id.imageViewStreetView);
        add_position = (Button) rootView.findViewById(R.id.btnAddPlace);
        sk_radius = (SeekBar) rootView.findViewById(R.id.sk_radius_pos);
        sk_radius.setMax(Globals.MAX_RADIUS);
        txt_radius = (TextView) rootView.findViewById(R.id.txt_radius);
        time_start = (TextView) rootView.findViewById(R.id.txt_start);
        time_end = (TextView) rootView.findViewById(R.id.txt_end);
        alarmEnabled = (TextView) rootView.findViewById(R.id.textViewAlarmEnabled);
        restaurant = (CheckBox) rootView.findViewById(R.id.checkBoxRuleRestaurant);
        supermarket = (CheckBox) rootView.findViewById(R.id.checkBoxRuleSupermarket);
        foodStore = (CheckBox) rootView.findViewById(R.id.checkBoxRuleFoodStore);
        hairdresser = (CheckBox) rootView.findViewById(R.id.checkBoxRuleHairdresser);
        bar = (CheckBox) rootView.findViewById(R.id.checkBoxRuleBar);
        cafe = (CheckBox) rootView.findViewById(R.id.checkBoxRuleCafe);

        if (sw_enabled.isChecked()) {
            alarmEnabled.setText(R.string.enabled);
        } else {
            alarmEnabled.setText(R.string.disabled);
        }


        if (rule != null) {
            sw_enabled.setChecked(rule.isEnabled());
            if (rule.isEnabled()) {
                alarmEnabled.setText(R.string.enabled);
            } else {
                alarmEnabled.setText(R.string.disabled);
            }
            etxt_namePosition.setText(rule.getElementName());
            sk_radius.setProgress(rule.getRadius());
            txt_radius.setText(String.valueOf(rule.getRadius()));

            restaurant.setChecked(rule.getRestaurant());
            supermarket.setChecked(rule.getSupermarket());
            foodStore.setChecked(rule.getFoodStore());
            hairdresser.setChecked(rule.getHairdresser());
            bar.setChecked(rule.getBar());
            cafe.setChecked(rule.getCafe());

            //TODO init start and end

            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            slidingLayout.setPanelHeight(panelHeight);
            txt_title.setText(R.string.information);
            add_position.setText(R.string.modify);
        } else {
            txt_title.setText(R.string.information);
        }

        /**
         *  ALL THE LISTENER
         */
        restaurant.setOnCheckedChangeListener(new checkBoxChangeClicker());
        supermarket.setOnCheckedChangeListener(new checkBoxChangeClicker());
        foodStore.setOnCheckedChangeListener(new checkBoxChangeClicker());
        hairdresser.setOnCheckedChangeListener(new checkBoxChangeClicker());
        bar.setOnCheckedChangeListener(new checkBoxChangeClicker());
        cafe.setOnCheckedChangeListener(new checkBoxChangeClicker());


        sk_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_radius.setText(String.valueOf(progress) + "meters");
                handleNewLocation(myLocation);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_start.setText(selectedHour + ":" + selectedMinute);
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(R.string.start_hour);

                mTimePicker.show();
            }
        });

        time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_end.setText(selectedHour + ":" + selectedMinute);
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(R.string.end_hour);
                mTimePicker.show();
            }
        });


        add_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check information before to go back
                if (rule == null) {
                    rule = new Element(etxt_namePosition.getText().toString(),
                            sw_enabled.isChecked(),
                            "",
                            "",
                            sk_radius.getProgress(),
                            restaurant.isChecked(),
                            supermarket.isChecked(),
                            foodStore.isChecked(),
                            hairdresser.isChecked(),
                            bar.isChecked(),
                            cafe.isChecked(),Globals.TYPE_RULE);
                } else {
                    rule.setElementName(etxt_namePosition.getText().toString());
                    rule.setEnable(sw_enabled.isChecked());
                    rule.setDescription("");
                    rule.setAddress("");
                    rule.setRadius(sk_radius.getProgress());
                    rule.setRestaurant(restaurant.isChecked());
                    rule.setSupermarket(supermarket.isChecked());
                    rule.setFoodStore(foodStore.isChecked());
                    rule.setHairdresser(hairdresser.isChecked());
                    rule.setBar(bar.isChecked());
                    rule.setCafe(cafe.isChecked());
                }

                mCallback.OnReturnFromRule(rule);
            }
        });

        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelCollapsed(View panel) {
                txt_title.setText(R.string.information);
            }

            @Override
            public void onPanelExpanded(View panel) {

                txt_title.setText(R.string.show_map);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });

        sw_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    alarmEnabled.setText(R.string.enabled);
                } else {
                    alarmEnabled.setText(R.string.disabled);
                }
            }
        });

        mMapView = (MapView) rootView.findViewById(R.id.mapView_rules);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                //Hide or not the sliding panel when we touch the map
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (slidingLayout.getPanelHeight() > 0) {
                            slidingLayout.setPanelHeight(0);
                        } else {
                            slidingLayout.setPanelHeight(panelHeight);
                        }

                    }
                });


            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void handleNewLocation(Location location) {
        Log.d(LOGTAG, location.toString());

        //Save actual location
        myLocation = location;


        //Move camera to our position
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(0)                 // Sets the orientation of the camera to north
                .tilt(40)                   // Sets the tilt of the camera to 40 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Mark all rule alarm in our area
        searchInArea(location, Integer.toString(sk_radius.getProgress()),
                typesToString(restaurant.isChecked(), supermarket.isChecked(), foodStore.isChecked(), hairdresser.isChecked(), bar.isChecked(), cafe.isChecked()), null);

    }

    private String typesToString(boolean restaurant, boolean supermarket, boolean foodStore, boolean hairdresser, boolean bar, boolean cafe) {
        StringBuilder sb = new StringBuilder();

        if (restaurant)
            sb.append("|restaurant");
        if (supermarket)
            sb.append("|grocery_or_supermarket");
        if (foodStore)
            sb.append("|store");
        if (hairdresser)
            sb.append("|hair_care");
        if (bar)
            sb.append("|bar");
        if (cafe)
            sb.append("|cafe");

        if(sb.length()>0)
            sb.deleteCharAt(0);

        return sb.toString();
    }


    private void searchInArea(Location location, String radius, String types, @Nullable String keyword) {
        //Search in area
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + location.getLatitude() + "," + location.getLongitude());
        sb.append("&radius=" + radius);
        sb.append("&types=" + types);
        if (keyword != null) {
            sb.append("&keyword=" + keyword);
        }
        sb.append("&sensor=true");
        sb.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("URL: ", sb.toString());

        // Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    /**
     * All Tasks in background
     */
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {

            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            // Clears all the existing markers
            googleMap.clear();

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d("Name: ", name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name + " : " + vicinity);

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);

            }

        }


    }

    private class checkBoxChangeClicker implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            handleNewLocation(myLocation);
        }
    }
}
