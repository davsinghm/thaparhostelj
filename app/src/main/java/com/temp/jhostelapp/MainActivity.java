package com.temp.jhostelapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.temp.jhostelapp.ui.LoginFragment;
import com.temp.jhostelapp.ui.MainFragment;

import java.io.File;

/**
 * Created by DSM_ on 1/29/16.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Interface {

    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;

    @Override
    public void loginSuccessful() {
        showMain();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String token = PreferenceHelper.getToken(this);

        if (token == null) {
            promptLogin();
        } else
            showMain();

    }

    private void promptLogin() {
        Fragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

        mActionBar = getSupportActionBar();
        if (mActionBar != null)
            mActionBar.hide();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void showMain() {
        Fragment fragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
        if (mActionBar != null)
            mActionBar.show();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            PreferenceHelper.putRollNo(this, null);
            PreferenceHelper.putToken(this, null);
            PreferenceHelper.putLong(this, "lastTimestamp", 0);
            File file = new File(getCacheDir(), "notifications");
            file.delete();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            promptLogin();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
