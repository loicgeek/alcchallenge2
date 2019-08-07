package com.loicngou.alcchallenge2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity
        implements FeedFragment.OnFragmentInteractionListener,
                   AddProductFragment.OnFragmentInteractionListener ,
                   ProfileFragment.OnFragmentInteractionListener{

    ActionBar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()  {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Feeds");
                    fragment = new FeedFragment();
                    break;
                case R.id.navigation_notifications:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Add Product");
                    fragment = new AddProductFragment();
                    break;
                case R.id.navigation_dashboard:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Your Dashboard");
                    fragment = new ProfileFragment();
                    break;
            }
            return loadFrament(fragment);
        }
    };
    public  boolean loadFrament(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.toolbar = getSupportActionBar();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFrament(new FeedFragment());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("td",uri.toString());
        System.out.println(uri.toString());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
