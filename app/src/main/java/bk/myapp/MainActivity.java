package bk.myapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import bk.myapp.Fragments.Coupons;
import bk.myapp.Fragments.History;
import bk.myapp.Fragments.Home;
import bk.myapp.Fragments.Settings;
import bk.myapp.showContactsRecview.ViewContacts;
import com.google.firebase.database.*;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

public class MainActivity extends AppCompatActivity {
    RelativeLayout layout;
    Home home;
    History history;
    Coupons coupons;
    Settings settings;
    android.app.FragmentTransaction transaction;
    FragmentManager manager;
    Toolbar toolbar;
    SharedPreferences preferences;
    FlowingDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("sp", MODE_PRIVATE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        getSupportActionBar().setTitle("Emergenci");
        layout = (RelativeLayout) findViewById(R.id.content);
        initializeFragments();
        manager = this.getFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content, home).commit();
        listenToFirebaseChanges();
    }


    private void listenToFirebaseChanges() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("name");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person p = dataSnapshot.getValue(Person.class);
                if (p != null) {
                    if (!p.name.equals(preferences.getString("name", ""))) {
                        Notifs notifs = new Notifs(MainActivity.this);
                        notifs.showNotification(p.reason);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeFragments() {
        home = new Home();
        history = new History();
        coupons = new Coupons();
        settings = new Settings();
    }

    private void performTransaction(Fragment fragToReplace) {
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content, fragToReplace).commit();
        drawer.closeMenu();
    }

    @Override
    public void onBackPressed() {
        home.backPressed();
    }

    public void sendAlertMessage(String reason) {
        (new Firebase(this)).sendAlertMessage(preferences.getString("name", ""), reason);
        startActivity(new Intent(this, AlertSuccessActivity.class));
    }

    public void hf(View view) {
        performTransaction(home);
    }

    public void hisf(View view) {
        performTransaction(history);
    }

    public void cf(View view) {
        performTransaction(coupons);
    }

    public void setf(View view) {
        performTransaction(settings);
    }

    public void emf(View view) {
        startActivity(new Intent(this, NotificationReceiver.class));
    }

    public void startTrustedContacts(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, ViewContacts.class));
        } else {
            //ask for permission if user didnot given
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 0);
            }
        }
    }

    public void logout(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, ViewContacts.class));
                } else {
                    Toast.makeText(this, "We need contacts permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
