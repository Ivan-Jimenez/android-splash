package com.ivan.splash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the ActionBar and the Spiner in the ActionBar
//        getActionBar().setDisplayShowTitleEnabled(true);
  //      getActionBar().setSubtitle("Splash Test");
    //    getActionBar().setTitle("Splash");
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        new MenuInflater(this).inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
