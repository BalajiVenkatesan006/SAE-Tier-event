package com.example.balaji.demo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.Manifest.permission;

public class ListOnline extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
                                                GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    DatabaseReference onlineRef, currentUserRef, counterRef, locations;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FAST_INTERVAL = 3000;
    private static int DISTANCE = 10;
    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        listOnline = (RecyclerView) findViewById(R.id.listOnline);
        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);

        //view.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Presence System");
        setSupportActionBar(toolbar);

        locations = FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("last online");
        currentUserRef = FirebaseDatabase.getInstance().getReference("last online").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        //location
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }

        }

        setupSystem();
        updateList();
    }







    private void updateList() {
        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(
                User.class,
                R.layout.user_layout,
                ListOnlineViewHolder.class,
                counterRef
        ) {
            @Override
            protected void populateViewHolder(ListOnlineViewHolder viewHolder, final User model, int position) {
                viewHolder.txtEmail.setText(model.getEmail());
                Log.d("Test","Entered updated list");

//                //,Toast.LENGTH_SHORT).show();
//                if(!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
//                    Log.d("Test","Entered the if in onclick listerner");
//
//                    Intent map = new Intent(ListOnline.this,MapsActivity.class);
//                    map.putExtra("email", model.getEmail());
//                    map.putExtra("lat",mLastLocation.getLatitude());
//                    map.putExtra("lng",mLastLocation.getLongitude());
//                    startActivity(map);
//                }
                //create the onclick action for the recicler view here

                // This part of the code is not working so created an onclick activity outsiede the class which directs it to anthoer activity





                viewHolder.itemClickListener = new ItemClickListener() {
                      //Log.d("Test"'"item on click"');  noow
                    //Log.d("Test","Entered updated list");
                    @Override
                    public void onClick(View view, int position) {

                        if(!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            Log.d("Test","Entered the if in onclick listerner");

                            Intent map = new Intent(ListOnline.this,MapsActivity.class);
                            map.putExtra("email",model.getEmail());
                            map.putExtra("lat",mLastLocation.getLatitude());
                            map.putExtra("lng",mLastLocation.getLongitude());
                            startActivity(map);
                        }
                        else{
                          //  Toast.makeText(this, "fetchinf the location",Toast.LENGTH_SHORT).show();
                            Log.d("Test","the else ");
                        }
                    }
                };
            }
        };
        adapter.notifyDataSetChanged();
        listOnline.setAdapter(adapter);
    }

    private void setupSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class)){
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot :dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    Log.d("LOG",""+user.getEmail()+" is "+user.getStatus());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void displayLocation() {
        if((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED )&&
                (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)){
            return;

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            //update to firebase
            Log.d("Test","able to load the location");
            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude())));

        }
        else{
        //    Toast.makeText(this, "couldn't get the location",Toast.LENGTH_SHORT).show();
            Log.d("Test","Couldt load location");
        }
        
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();


    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RES_REQUEST).show();
            }
            else{
                Toast.makeText(this," this device is not suppoerted",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                break;
            case  R.id.action_logout:
                currentUserRef.removeValue();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
            mLastLocation = location;
            displayLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED )&&
                (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)){
            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,(PendingIntent)null);

       // LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length> 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}
