package com.example.balaji.demo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Options Activity");
        setSupportActionBar(toolbar);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater =  getMenuInflater();
//        inflater.inflate(R.menu.main_menu_options,menu);
//                return true;
//    }


    public void onclickfriends(View view){
        Intent i = new Intent(this,ListOnline.class);
        startActivity(i);
    }

    public void onclickpetrol(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","gas_station");
        startActivity(it);
    }
    public void onclickparking(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","parking");
        startActivity(it);
    }
    public void onclickcarrepair(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","car_repair");
        startActivity(it);
    }
    public void onclickCardealer(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","car_dealer");
        startActivity(it);
    }
    public void onclickcarwash(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","car_wash");
        startActivity(it);
    }
    public void onclickincurance(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","insurance_agency");
        startActivity(it);
    }
    public void onclickhospital(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","hospital");
        startActivity(it);
    }
    public void onclickatm(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","atm");
        startActivity(it);
    }
    public void onclickrest(View view){
        Intent it = new Intent(this,MapsActivity2.class);
        it.putExtra("gas","restaurant");
        startActivity(it);
    }
    public void onClicklogout(View v) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(Options.this, MainActivity.class));
                            finish();
                        }
                    });

    }

}
