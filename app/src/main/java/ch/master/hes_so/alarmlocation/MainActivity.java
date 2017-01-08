package ch.master.hes_so.alarmlocation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.List.ListMenuFragment;
import ch.master.hes_so.alarmlocation.Maps.MapViewFragmentSelectPosition;
import ch.master.hes_so.alarmlocation.Maps.MapViewFragmentSelectRule;
import ch.master.hes_so.alarmlocation.Service.LocationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ListMenuFragment.OnListMenuFragmentListener,
        MapViewFragmentSelectPosition.OnMapPositionFragmentListener,
        MapViewFragmentSelectRule.OnMapRuleFragmentListener{

    private FragmentManager fragmentManager;
    private ListMenuFragment listElementFragment = new ListMenuFragment();
    private MapViewFragmentSelectPosition mapViewFragmentSelectPosition = new MapViewFragmentSelectPosition();
    private MapViewFragmentSelectRule mapViewFragmentSelectRules = new MapViewFragmentSelectRule();

    private boolean mIsBound;
    private Messenger mServiceMessenger = null;
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private String LOGTAG = "MainActivity";

    /*listElementFragment = ;
    mapViewFragmentSelectPosition = new MapViewFragmentSelectPosition();
    mapViewFragmentSelectRules = new MapViewFragmentSelectRule();*/

    private FeedElementDbHelper taskDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise DataBase
        taskDbHelper = new FeedElementDbHelper(this);

        //Initialise LocationService
        if(LocationService.isRunning()) {
            doBindService();
        }
        else {
            startService(new Intent(MainActivity.this, LocationService.class));
            doBindService();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Drawer contenant toutes les options propre à l'application
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Ask for permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //Instantiate all fragments
        fragmentManager = getSupportFragmentManager();

        // Add the fragment by default
        fragmentManager.beginTransaction()
                .add(R.id.content_main, listElementFragment)
                .commit();

        refreshList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //TODO obligé de mettre ce test sinon erreur "Fragment Already Added". Corriger si on a du temps
            if(listElementFragment.isAdded()){
                fragmentManager.beginTransaction().remove(listElementFragment).commit();
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OnInteractionListMenu(int fragmentCaller, int id, boolean state ) {

        if (fragmentCaller == Globals.ADD_POSITION) {
            Log.d("LOG", "Add a new position");

            MapViewFragmentSelectPosition frag = new MapViewFragmentSelectPosition();
            frag.add_new_position();
            fragmentManager.beginTransaction().replace(R.id.content_main, frag)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == Globals.ADD_RULE) {
            Log.d("LOG", "Add a rule");

            MapViewFragmentSelectRule frag = new MapViewFragmentSelectRule();
            frag.add_new_position();
            fragmentManager.beginTransaction().replace(R.id.content_main, frag)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == Globals.OPEN_POSITION) {
            Log.d("TODO", "Open an existing position");

            MapViewFragmentSelectPosition frag = new MapViewFragmentSelectPosition();
            frag.modify_position(taskDbHelper.getElementWithId(id));
            fragmentManager.beginTransaction().replace(R.id.content_main, frag)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == Globals.OPEN_RULE) {
            Log.d("TODO", "Open an existing rule");

            MapViewFragmentSelectRule frag = new MapViewFragmentSelectRule();
            frag.modify_position(taskDbHelper.getElementWithId(id));
            fragmentManager.beginTransaction().replace(R.id.content_main, frag)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == Globals.DELETE_ELEMENT){
            taskDbHelper.deleteElement(id);
            sendMessageToService(LocationService.MSG_DELETE_ELEMENT,id);
            refreshList();
            Toast.makeText(getApplicationContext(),R.string.delete_element,Toast.LENGTH_SHORT).show();
        }

        if(fragmentCaller == Globals.UPDATE_ELEMENT){
            Element element = taskDbHelper.getElementWithId(id);
            element.setEnable(state);
            taskDbHelper.modifyElement(element);
            listElementFragment.updateList(taskDbHelper.getElementFromDB());
        }
    }

    @Override
    public void OnReturnFromPosition(Element _element) {

        if (_element.getId() == -1){ //we create a new element
            taskDbHelper.addNewElement(_element);
        }else{ //otherwise the element already exist so we just modify it
            taskDbHelper.modifyElement(_element);
        }

        if(listElementFragment != null){
            listElementFragment.updateList(taskDbHelper.getElementFromDB());
        }

        fragmentManager.beginTransaction().replace(R.id.content_main, listElementFragment)
                .commit();

        //Update service
        ArrayList<Element> elem = new ArrayList<>();
        elem.add(_element);
        sendMessageToService(LocationService.MSG_UPDATE_ONE_ELEMENT,elem);
    }

    @Override
    public void OnReturnFromRule(Element _element) {

        if (_element.getId() == -1){ //we create a new element
            taskDbHelper.addNewElement(_element);
        }else{ //otherwise the element already exist so we just modify it
            taskDbHelper.modifyElement(_element);
        }

        if(listElementFragment != null){
            listElementFragment.updateList(taskDbHelper.getElementFromDB());
        }

        fragmentManager.beginTransaction().replace(R.id.content_main, listElementFragment)
                .commit();

        //Update service
        ArrayList<Element> elem = new ArrayList<>();
        elem.add(_element);
        sendMessageToService(LocationService.MSG_UPDATE_ONE_ELEMENT,elem);
    }

    public void refreshList(){

        listElementFragment.updateList(taskDbHelper.getElementFromDB());

        //Update Service
        sendMessageToService(LocationService.MSG_UPDATE_ELEMENTS,taskDbHelper.getElementFromDB());
    }

    /**
     * LocationService communication
     */

    /**
     * Send data to the service
     *
     * @param §
     */
    private void sendMessageToService(int command, int param) {
        if (mIsBound) {
            if (mServiceMessenger != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt("msg",param);
                    Message msg = Message.obtain(null, command);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessageToService(int command, ArrayList<Element> elements) {
        if (mIsBound) {
            if (mServiceMessenger != null) {
                try {
                    // Send data
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("msg", elements);
                    Message msg = Message.obtain(null,command);
                    msg.setData(bundle);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Bind this Activity to LocationService
     */
    private void doBindService() {
        Log.d(LOGTAG, "do bind service");
        bindService(new Intent(this, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * Un-bind this Activity to LocationService
     */
    private void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, LocationService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
            Log.i(LOGTAG, "Attached");
            try {
                Message msg = Message.obtain(null, LocationService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
                e.printStackTrace();
            }

            //Send DataBase to service
            sendMessageToService(LocationService.MSG_UPDATE_ELEMENTS,taskDbHelper.getElementFromDB());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mServiceMessenger = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e(LOGTAG, "Failed to unbind from the service", t);
        }
    }

    /**
     * Handle incoming messages from MyService
     */
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOGTAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
