package com.example.balaji.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    private final static int LOGIN_PERMISSION=1000;
    LinearLayout l1,l2;
    Button btnsub;
    Animation uptodown,downtoup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnsub =  (Button)findViewById(R.id.buttonsub);
        btnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                        .setAllowNewEmailAccounts(true).build(),LOGIN_PERMISSION
                );
            }
        });

        //btnsub = (Button)findViewById(R.id.buttonsub);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        // prefs.edit().putBoolean(AppPreferences.LOGGED_IN, true);
//        // prefs.edit().putString(AppPreferences.USER_EMAIL, input.getEmail()).apply();
//        if(prefs.getBoolean(AppPreferences.LOGGED_IN, false)) {
//          //  performRegistration();
//            Intent g = new Intent(MainActivity.this, Options.class);
//            startActivity(g);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_PERMISSION){
            startNewActivity(resultCode,data);
        }
    }

    private void startNewActivity(int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            Intent intent = new Intent(MainActivity.this,Options.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this,"Login Failed !!! ",Toast.LENGTH_SHORT).show();
        }
    }
}
