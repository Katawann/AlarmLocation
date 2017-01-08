package ch.master.hes_so.alarmlocation.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ch.master.hes_so.alarmlocation.Globals;
import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.Maps.PlaceJSONParser;
import ch.master.hes_so.alarmlocation.R;

/**
 * Created by Warrior on 08.01.2017.
 */

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int LOCATION_INTERVAL_STANDBY = 1000 * 60 * 1; //Every 5 minutes
    private static final int LOCATION_INTERVAL_ON_USE = 1000;  //Every second
    private static final float LOCATION_DISTANCE = 10; //10 meters
    private static boolean isRunning = false;
    private boolean isReady = false;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_UPDATE_ELEMENTS = 3;
    public static final int MSG_DELETE_ELEMENT = 4;
    public static final int MSG_UPDATE_ONE_ELEMENT = 5;

    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler()); // Target we publish for clients to send messages to IncomingHandler.
    private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.

    private static final String LOGTAG = "MyService";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private ArrayList<Element> elements;
    private ServiceDbHelper serviceDbHelper;


    @Override
    public IBinder onBind(Intent intent) {

        Log.i(LOGTAG, "onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOGTAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(LOGTAG, "onCreat");

        //Create GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_INTERVAL_STANDBY)
                .setFastestInterval(LOCATION_INTERVAL_ON_USE)
                .setSmallestDisplacement(LOCATION_DISTANCE);

        mGoogleApiClient.connect();

        //Load Service Database
        serviceDbHelper = new ServiceDbHelper(this);
        elements = serviceDbHelper.getElementFromDB();

        //Now service is running
        isRunning = true;

    }

    @Override
    public void onDestroy() {
        Log.i(LOGTAG, "onDestroy");
        super.onDestroy();

        isRunning = false;
    }

    private void alarm(Location currentLocation, ArrayList<Element> elements) {

        for (int i = 0; i < elements.size(); i++) {

            if (elements.get(i).isEnabled()) {
                //Check if we are near an alarm point position
                if (elements.get(i).getType() == Globals.TYPE_POSITION) {
                    int radius = elements.get(i).getRadius();

                    Location elementLocation = new Location("elementLOcation");
                    elementLocation.setLatitude(elements.get(i).getLatLng().latitude);
                    elementLocation.setLongitude(elements.get(i).getLatLng().longitude);

                    float distance = currentLocation.distanceTo(elementLocation);

                    if ((int) distance <= radius) {
                        Log.d(LOGTAG, "TO DO ALARM POSITION");
                    }
                }

                //Check if there are some rule point near this location
                if (elements.get(i).getType() == Globals.TYPE_RULE) {
                    boolean restaurant = elements.get(i).getRestaurant();
                    boolean bar = elements.get(i).getBar();
                    boolean cafe = elements.get(i).getCafe();
                    boolean foodStore = elements.get(i).getFoodStore();
                    boolean hairdresser = elements.get(i).getHairdresser();
                    boolean supermarket = elements.get(i).getSupermarket();

                    searchInArea(currentLocation, Integer.toString(elements.get(i).getRadius()),
                            typesToString(restaurant,
                                    supermarket,
                                    foodStore,
                                    hairdresser,
                                    bar,
                                    cafe), null);
                }
            }
        }
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

        if (sb.length() > 0)
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
        LocationService.PlacesTask placesTask = new LocationService.PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOGTAG, "onConnected");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        //Log.d(LOGTAG, "Location: " + location.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOGTAG, "Location changed: " + location.toString());
        alarm(location, elements);
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

            LocationService.ParserTask parserTask = new LocationService.ParserTask();

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
            if (list.size()>0) {
                for(int i = 0; i<list.size();i++){
                    HashMap<String, String> hmPlace = list.get(i);
                    Log.d(LOGTAG, "TO DO ALARM RULE: " + hmPlace.get("place_name"));
                }

            }

        }
    }

        /**
         * Send the data to all clients.
         *
         * @param
         */
        private void sendMessageToUI(int command, Location location) {
            Iterator<Messenger> messengerIterator = mClients.iterator();
            while (messengerIterator.hasNext()) {
                Messenger messenger = messengerIterator.next();
                try {
                    // Send data
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("msg", (Parcelable) location);
                    Message msg = Message.obtain(null, command);
                    msg.setData(bundle);
                    messenger.send(msg);

                } catch (RemoteException e) {
                    // The client is dead. Remove it from the list.
                    mClients.remove(messenger);
                }
            }
        }

        /**
         * Send the data to all clients.
         *
         * @param
         */
        private void sendMessageToUI(int command, String data) {
            Iterator<Messenger> messengerIterator = mClients.iterator();
            while (messengerIterator.hasNext()) {
                Messenger messenger = messengerIterator.next();
                try {
                    // Send data
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", data);
                    Message msg = Message.obtain(null, command);
                    msg.setData(bundle);
                    messenger.send(msg);

                } catch (RemoteException e) {
                    // The client is dead. Remove it from the list.
                    mClients.remove(messenger);
                }
            }
        }

        public static boolean isRunning() {

            return isRunning;
        }

        /**
         * Handle incoming messages from MainActivity
         */
        private class IncomingMessageHandler extends Handler { // Handler of incoming messages from clients.
            @Override
            public void handleMessage(Message msg) {
                Log.d(LOGTAG, "handleMessage: " + msg.what);
                switch (msg.what) {
                    case MSG_REGISTER_CLIENT:
                        mClients.add(msg.replyTo);
                        break;
                    case MSG_UNREGISTER_CLIENT:
                        mClients.remove(msg.replyTo);
                        break;
                    case MSG_UPDATE_ELEMENTS:
                        msg.getData().setClassLoader(Element.class.getClassLoader());
                        elements = msg.getData().getParcelableArrayList("msg");
                        updateElementToDB(elements);
                        break;
                    case MSG_DELETE_ELEMENT:
                        int id = msg.getData().getInt("msg");
                        serviceDbHelper.deleteElement(id);
                        Log.d(LOGTAG,"Task deleted: " + id);
                        break;
                    case MSG_UPDATE_ONE_ELEMENT:
                        msg.getData().setClassLoader(Element.class.getClassLoader());
                        ArrayList<Element> elem = msg.getData().getParcelableArrayList("msg");
                        serviceDbHelper.modifyElement(elem.get(0));
                    default:
                        super.handleMessage(msg);
                }
            }
        }

    private void updateElementToDB(ArrayList<Element> elements) {
        for(int i= 0; i<elements.size(); i++){
            //Check if element exist in DataBase
            if(serviceDbHelper.getElementWithId(elements.get(i).getId())==null){
                serviceDbHelper.addNewElement(elements.get(i));
            }
            else {
                serviceDbHelper.modifyElement(elements.get(i));
            }
        }
    }
}
