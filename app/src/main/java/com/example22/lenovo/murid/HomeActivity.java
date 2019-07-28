package com.example22.lenovo.murid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example22.lenovo.murid.chat_package.ListUserChat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sesi ;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    FragmentTransaction move = getSupportFragmentManager().beginTransaction();
                    move.replace(R.id.container, new HomeFragment()).commit();
                   return true;
                case R.id.navigation_profile:

                    FragmentTransaction move2 = getSupportFragmentManager().beginTransaction();
                    move2.replace(R.id.container, new ProfileFragment()).commit();
                   return true;

                case R.id.navigation_chat:

                    FragmentTransaction move3 = getSupportFragmentManager().beginTransaction();
                    move3.replace(R.id.container, new ListUserChat()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentTransaction move = getSupportFragmentManager().beginTransaction();
        move.replace(R.id.container, new HomeFragment()).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        sesi = new SessionManager(this);

        actionToken();
    }

    private void actionToken() {


        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Token_User");
        reference.child(sesi.getNama()).child("token").setValue(token);


    }

}