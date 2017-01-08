package ch.master.hes_so.alarmlocation.Maps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import ch.master.hes_so.alarmlocation.Globals;
import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.R;

/**
 * Created by Warrior on 04.01.2017.
 */

public class MapViewFragmentSelectPosition extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private SlidingUpPanelLayout slidingLayout;
    private int panelHeight;
    //private TextView placeAddress, placeLocation, placeCountry;

    private TextView txt_title;
    private Switch sw_enabled;
    private EditText etxt_namePosition;
    private EditText descriptionPosition;
    private EditText etxt_addressLocation;
    private SeekBar sk_radius;
    private TextView txt_radius;

    private ImageView streetView;
    private Button add_position;
    private Element position;
    private LatLng mLatLng;
    private final static String LOGTAG = "MapViewFragment";

    OnMapPositionFragmentListener mCallback;

    public interface OnMapPositionFragmentListener{
        void OnReturnFromPosition(Element _element);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMapPositionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnReturnFromPosition"); }
    }

    public void add_new_position(){
        position = null;
    }
    public void modify_position(Element _position){
        position = _position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_select_position_fragment, container, false);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout_pos);

        //Save original panel height
        panelHeight = slidingLayout.getPanelHeight();

        //Sliding layout hidden
        slidingLayout.setPanelHeight(0);

        //--- Get layout ---//

        /*placeAddress = (TextView) rootView.findViewById(R.id.textViewPlaceAddress);
        placeLocation = (TextView) rootView.findViewById(R.id.textViewPlaceLocation);
        placeCountry = (TextView) rootView.findViewById(R.id.textViewPlaceCountry);*/
        txt_title = (TextView) rootView.findViewById(R.id.txt_title);
        sw_enabled = (Switch) rootView.findViewById(R.id.sw_enabled);
        etxt_namePosition = (EditText) rootView.findViewById(R.id.etxt_taskname);
        etxt_addressLocation = (EditText) rootView.findViewById(R.id.etxt_address);
        streetView = (ImageView) rootView.findViewById(R.id.imageViewStreetView);
        add_position = (Button) rootView.findViewById(R.id.btnAddPlace);
        sk_radius = (SeekBar) rootView.findViewById(R.id.sk_radius_pos);
        sk_radius.setMax(Globals.MAX_RADIUS);
        txt_radius = (TextView) rootView.findViewById(R.id.txt_radius);


        if(position != null){
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            slidingLayout.setPanelHeight(panelHeight);

            sw_enabled.setChecked(position.isEnabled());
            etxt_namePosition.setText(position.getElementName());
            etxt_addressLocation.setText(position.getAddress());
            sk_radius.setProgress(position.getRadius());
            txt_radius.setText(String.valueOf(position.getRadius()));

            //Restore last saved point
            mLatLng = position.getLatLng();

            txt_title.setHeight(panelHeight);
            txt_title.setText(R.string.information);
            add_position.setText(R.string.modify);
        }else{
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            slidingLayout.setPanelHeight(0);
            mLatLng = null;
        }

        sk_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_radius.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        add_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check information before to go back
                if(position == null){
                    position = new Element(etxt_namePosition.getText().toString(),
                            sw_enabled.isChecked(),
                            "",
                            etxt_addressLocation.getText().toString(),
                            sk_radius.getProgress(), mLatLng,Globals.TYPE_POSITION);
                    Log.d(LOGTAG,"Position added: " + mLatLng.toString());
                }else{
                    position.setElementName(etxt_namePosition.getText().toString());
                    position.setEnable(sw_enabled.isChecked());
                    position.setDescription("");
                    position.setAddress(etxt_addressLocation.getText().toString());
                    position.setRadius(sk_radius.getProgress());
                    position.setLatLng(mLatLng);
                    Log.d(LOGTAG,"Position modified: " + mLatLng.toString());
                }

                mCallback.OnReturnFromPosition(position);
            }
        });

        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelCollapsed(View panel) {
                txt_title.setHeight(panelHeight);
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

        mMapView = (MapView) rootView.findViewById(R.id.mapView_pos);
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

                //Zoom to last location saved and add marker
                if(mLatLng!=null){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(mLatLng.latitude, mLatLng.longitude))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(0)                 // Sets the orientation of the camera to north
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    googleMap.addMarker(new MarkerOptions().position(mLatLng).title("Your alarm location point"));
                }

                //Add StreetView Image in the sliding panel
                if(mLatLng!=null){
                    //--- Download StreetView image ---//
                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/streetview?");
                    sb.append("size=600x300");
                    sb.append("&location=" + mLatLng.latitude +"," + mLatLng.longitude);
                    sb.append("&key=AIzaSyDM4I3MDC7y8dpcz9NJCpeAYCj0mTWGXT4");

                    Log.d(LOGTAG,"URL: " + sb.toString());

                    new DownloadImageTask(streetView).execute(sb.toString());   //Download StreetView image in a new Task
                }


                //Set LongClick listener
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        //Save latitude and longitude
                        mLatLng = latLng;

                        //Clear precedent marker
                        googleMap.clear();

                        //Add new marker
                        Marker locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Set this point"));
                        locationMarker.showInfoWindow();
                        //locationMarker.setDraggable(true);
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        //Fill address information before we can show the sliding panel
                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(getContext(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                        /*placeAddress.setText(address);
                        placeLocation.setText(postalCode + " " + city);
                        placeCountry.setText(country);*/
                        etxt_addressLocation.setLines(3);

                        etxt_addressLocation.setText(address + "\n"
                                                + postalCode + " " + city + "\n"
                                                + country);

                        Log.d(LOGTAG, "address : " + address + " city: " + city + " state: " + state + " country: " + country + " postal code: " + postalCode + " Name: " + knownName);

                        //--- Download StreetView image ---//
                        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/streetview?");
                        sb.append("size=600x300");
                        sb.append("&location=" + latLng.latitude +"," + latLng.longitude);
                        sb.append("&key=AIzaSyDM4I3MDC7y8dpcz9NJCpeAYCj0mTWGXT4");

                        Log.d(LOGTAG,"URL: " + sb.toString());

                        new DownloadImageTask(streetView).execute(sb.toString());   //Download StreetView image in a new Task

                        //show sliding layout in bottom of screen (not expand it)
                        slidingLayout.setPanelHeight(panelHeight);
                        txt_title.setHeight(panelHeight);
                        txt_title.setText(R.string.information);
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (slidingLayout.getPanelHeight() > 0) {
                            //Hide the sliding layout if we only touch the map
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
}
