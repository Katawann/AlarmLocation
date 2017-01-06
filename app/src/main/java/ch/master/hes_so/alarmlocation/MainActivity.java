package ch.master.hes_so.alarmlocation;

import android.content.pm.PackageManager;
import android.os.Bundle;
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

import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.List.ListMenuFragment;
import ch.master.hes_so.alarmlocation.List.Position;
import ch.master.hes_so.alarmlocation.List.Rule;
import ch.master.hes_so.alarmlocation.Maps.MapViewFragmentSelectPosition;
import ch.master.hes_so.alarmlocation.Maps.MapViewFragmentSelectRule;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ListMenuFragment.OnListMenuFragmentListener,
        MapViewFragmentSelectPosition.OnMapPositionFragmentListener,
        MapViewFragmentSelectRule.OnMapRuleFragmentListener{

    private FragmentManager fragmentManager;
    private ListMenuFragment listElementFragment = new ListMenuFragment();
    private MapViewFragmentSelectPosition mapViewFragmentSelectPosition = new MapViewFragmentSelectPosition();
    private MapViewFragmentSelectRule mapViewFragmentSelectRules = new MapViewFragmentSelectRule();

    /*listElementFragment = ;
    mapViewFragmentSelectPosition = new MapViewFragmentSelectPosition();
    mapViewFragmentSelectRules = new MapViewFragmentSelectRule();*/

    private FeedElementDbHelper taskDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDbHelper = new FeedElementDbHelper(this);

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

    public void OnInteractionListMenu(int fragmentCaller, int id) {

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
            frag.modify_position((Position) taskDbHelper.getElementWithId(id));
            fragmentManager.beginTransaction().replace(R.id.content_main, frag)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == Globals.OPEN_RULE) {
            Log.d("TODO", "Open an existing rule");

            MapViewFragmentSelectRule frag = new MapViewFragmentSelectRule();
            frag.modify_position((Rule) taskDbHelper.getElementWithId(id));
            fragmentManager.beginTransaction().replace(R.id.content_main, frag)
                    .addToBackStack(null)
                    .commit();
        }

        if (fragmentCaller == Globals.DELETE_ELEMENT){
            taskDbHelper.deleteElement(id);
            refreshList();
            Toast.makeText(getApplicationContext(),R.string.delete_element,Toast.LENGTH_SHORT).show();
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
    }

    public void refreshList(){

        listElementFragment.updateList(taskDbHelper.getElementFromDB());
    }
}
