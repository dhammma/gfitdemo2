package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleFitManager mGoogleFitManager;
    private LayoutFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction
                    .add(R.id.contentFrame, mFragment)
                    .addToBackStack(null)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mGoogleFitManager = new GoogleFitManager(this);

        refreshMainContentLayout();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

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

        if (id == R.id.nav_activities) {
            mFragment = new ActivitiesFragment();
        } else if (id == R.id.nav_camera) {
            mFragment = new DistancessFragment();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if (mFragment != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.contentFrame, mFragment)
                    .commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mGoogleFitManager.PERMISSION_REQUEST_CODE) {
            refreshMainContentLayout();
            mGoogleFitManager.connectGoogleFitCompleted();

            if (resultCode == RESULT_OK) {
                Logger.log("Google fit connected ok");
            } else {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                        @Override
                        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                            Logger.log("Get account +" + googleSignInAccount.getDisplayName());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Logger.log("Failure get account");
                        }
                    });
                Logger.log("Google fit connected failure " + Integer.toString(resultCode));
            }
        }
    }

    // BLOCK START: public methods
    //
    //
    public GoogleFitManager getGoogleFitManager() {
        return mGoogleFitManager;
    }

    //
    // BLOCK START: handlers
    //

    public void connectGoogleFit(View m) {
        mGoogleFitManager.connectGoogleFit();
    }

    public void disconnectGoogleFit(View m) {
        mGoogleFitManager.disconnectGoogleFit();
    }

    // BLOCK END

    // BLOCK START: render

    public void refreshMainContentLayout() {
        if (mFragment != null) {
            mFragment.refreshLayout();
        }
    }
}
