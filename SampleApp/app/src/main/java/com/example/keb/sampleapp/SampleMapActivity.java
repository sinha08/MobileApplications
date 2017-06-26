package com.example.keb.sampleapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;

public class SampleMapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {
    private static final String TAG = "SampleMapActivity";
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private ListView mListView;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mListView = (ListView)findViewById(R.id.list);
        String[] list = new String[] {"ScreenRecord","ScreenShot","logCApture"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1,list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch(pos) {
                    case 0:
                        final Dialog dialog = new Dialog(SampleMapActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.video_player);
                        dialog.show();
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.dimAmount = 0;
                        dialog.getWindow().setAttributes(lp);
                        final VideoView videoview = dialog.findViewById(R.id.video_view);
                        //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.livebooks);
                        Uri uri = Uri.fromFile(new File("/sdcard/jungBook.mp4"));
                        videoview.setVideoURI(uri);
                        videoview.start();
                        Toast.makeText(getApplicationContext(),"Recording",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        materialDesignFAM = findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = findViewById(R.id.material_design_floating_action_menu_item3);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                Toast.makeText(getApplicationContext(),"Menu 1",Toast.LENGTH_SHORT).show();
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Toast.makeText(getApplicationContext(),"Menu 2",Toast.LENGTH_SHORT).show();
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                //to share multiple files
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, "VOC report");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"abc@gmail.com"});
                intent.putExtra(Intent.EXTRA_TEXT, "We will get back to you asap." );
                intent.setType("message/rfc822"); /* This example is sharing email. */

                ArrayList<Uri> files = new ArrayList<Uri>();
                String [] filesToSend = new String[]{"/sdcard/jungBook.mp4","/sdcard/jungBook.mp4"};

                for(String path : filesToSend /* List of the files you want to send */) {
                    File file = new File(path);
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                startActivity(intent);

                // to share single file
//                Uri uri = Uri.fromFile(new File("/sdcard/jungBook.mp4"));
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("*/*");
//                share.putExtra(Intent.EXTRA_STREAM, uri);
//                startActivity(Intent.createChooser(share, "Share Sound File"));
                Toast.makeText(getApplicationContext(),"Sharing Files",Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(pos).title("Marker at your location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12.0f));
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);

        startLocationPermissionRequest();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {

            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
    }
}
