package ch.master.hes_so.alarmlocation.Maps;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import ch.master.hes_so.alarmlocation.R;

/**
 * Created by Warrior on 04.01.2017.
 */

public class MapViewFragmentSelectPosition extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private SlidingUpPanelLayout slidingLayout;
    private int panelHeight;
    private TextView placeAddress, placeLocation, placeCountry;
    private ImageView streetView;
    private final static String LOGTAG = "MapViewFragment";

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

        placeAddress = (TextView) rootView.findViewById(R.id.textViewPlaceAddress);
        placeLocation = (TextView) rootView.findViewById(R.id.textViewPlaceLocation);
        placeCountry = (TextView) rootView.findViewById(R.id.textViewPlaceCountry);
        streetView = (ImageView) rootView.findViewById(R.id.imageViewStreetView);

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


                //Set LongClick listener
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        //Clear precedent marker
                        googleMap.clear();

                        //Add new marker
                        Marker locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Set this point"));
                        locationMarker.showInfoWindow();
                        //locationMarker.setDraggable(true);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

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

                        placeAddress.setText(address);
                        placeLocation.setText(postalCode + " " + city);
                        placeCountry.setText(country);


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
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //Hide the sliding layout if we only touch the map
                        slidingLayout.setPanelHeight(0);
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
            // TODO Auto-generated method stub
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
