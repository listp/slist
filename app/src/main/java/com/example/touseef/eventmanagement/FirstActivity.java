package com.example.touseef.eventmanagement;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class FirstActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction= manager.beginTransaction();
            Fragment frag;
            if( (frag=manager.findFragmentByTag("home"))!=null){
                transaction.remove(frag);
            }
            if( (frag=manager.findFragmentByTag("event"))!=null) {
                transaction.remove(frag);
            }
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new ListFragment();
                    transaction.add(R.id.fragment_container,fragment,"home");
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragment = new EventFragment();
                    transaction.add(R.id.fragment_container,fragment,"event");
                    transaction.commit();
                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
            }
            return false;
        }
    };
    FirebaseAuth auth;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mAuthstateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        auth = FirebaseAuth.getInstance();
        mAuthstateListener  =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser() != null){
                    Toast.makeText(FirstActivity.this,"signed in", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                            )).build(),RC_SIGN_IN);
                }
            }
        };
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        Fragment fragment = new ListFragment();
        transaction.add(R.id.fragment_container,fragment,"home");
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                if(!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()){
                    Toast.makeText(this,"sign up successful",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    if(response == null) {
                        Toast.makeText(this, "signup failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        auth.removeAuthStateListener(mAuthstateListener);
    }
    @Override
    protected void onResume(){
        super.onResume();
        auth.addAuthStateListener(mAuthstateListener);
    }
}
