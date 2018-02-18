package com.example.balaji.demo;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String email;
    DatabaseReference locations;
    double lat,lng;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //reference to firebase

        locations = FirebaseDatabase.getInstance().getReference("Locations");

        //intenet contents......

        if(getIntent()!=null){
            Toast.makeText(this, "Intent if", Toast.LENGTH_SHORT).show();
            email = getIntent().getStringExtra("email");
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
            lat = getIntent().getDoubleExtra("lat",0);
            lng = getIntent().getDoubleExtra("lng",0);
        }

        if(!TextUtils.isEmpty(email)){

            Toast.makeText(this, "calling texutis",Toast.LENGTH_SHORT).show();
            loadLocationForThisUser(email);
        }
    }

    private void loadLocationForThisUser(String email) {
        Query user_location = locations.orderByChild("email").equalTo(email);
        Toast.makeText(this, email,Toast.LENGTH_SHORT).show();
        user_location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()) {
                    Tracking tracking = postSnapShot.getValue(Tracking.class);

                    //adding marker
                    LatLng friendlocation = new LatLng(Double.parseDouble(tracking.getLat()),Double.parseDouble(tracking.getLng()));

                    //current userlocation

                    Location currentuser = new Location("");
                    currentuser.setLatitude(lat);
                    currentuser.setLongitude(lng);

                    //location for friend cordinates

                    Location friend = new Location("");
                    friend.setLatitude(Double.parseDouble(tracking.getLat()));
                    friend.setLongitude(Double.parseDouble(tracking.getLng()));
                    
                    //distance
                    distance(currentuser,friend);

                    //adding freind marker on map

                    mMap.addMarker(new MarkerOptions()
                                        .position(friendlocation)
                                        .title(tracking.getEmail())
                                        .snippet("Distance "+new DecimalFormat("#.#").format(distance(currentuser,friend)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12.0f));
                }
                LatLng current = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(current).title(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private double distance(Location currentuser, Location friend) {
        double theta = currentuser.getLongitude() - friend.getLongitude();
        double dist = Math.sin(deg2rad(currentuser.getLatitude()))
                        * Math.sin(deg2rad(friend.getLatitude()))
                        * Math.cos(deg2rad(currentuser.getLatitude()))
                        * Math.cos(deg2rad(friend.getLatitude()))
                        * Math.cos(deg2rad(theta));

        dist = Math.cos(dist);
        dist = rad2degree(dist);
        dist = dist * 60 * 1.1515;
        return dist;

    }

    private double rad2degree(double rad) {
        return (rad * Math.PI / 180);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
     /*   LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


    }
}
