package com.example.inventorymapper;

import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.inventorymapper.ui.forms.ItemCreationForm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymapper.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int MAP_PERMISSION_REQUEST = 6969;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    static boolean tmp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemCreationForm form = new ItemCreationForm();
                form.show(getSupportFragmentManager(), "Item-form");
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_households, R.id.nav_map, R.id.nav_user)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        LocationHelper.getLocationPermission(this, MainActivity.this);


        ///

        ImageRecognitionHelper helper = new ImageRecognitionHelper();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.asdf);

        helper.recognizeImage(bitmap, new ImageRecognitionHelper.RecognitionCallback() {
            @Override
            public void onSuccess(List<String> labels) {
                Log.d("ImageRecognition", "Success: " + labels);
            }

            @Override
            public void onError(String error) {
                Log.e("ImageRecognition", "Error: " + error);
            }
        });

    }

    public FloatingActionButton getActionButton() {
        return binding.appBarMain.fab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LocationHelper.REQUEST_LOCATION_PERMISSION:
                if (Arrays.stream(grantResults).anyMatch(perm -> perm == PackageManager.PERMISSION_GRANTED)) {
                    LocationHelper.getLocationPermission(this, MainActivity.this);
                    Log.d("Permission", "Handling permission result for location");
                } else {
                    Log.e("Permission", "Unable to get location permissions");
                }
                break;
            default:
                Log.w("Permission", "Unknown permission request");
        }
    }


}