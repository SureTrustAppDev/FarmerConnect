package com.suretrust.farmerconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FarmerInfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView f_nametv, agetv, locationtv, farmer_nametv, farmer_infotv,yt_tv;
    private Button donate_btn;
    private ImageView farmer_photo, call;
    DrawerLayout drawerLayout;
    DatabaseReference dbref;
    private FirebaseAuth mAuth;
    FirebaseFirestore st;
    TextView pro;
    String uid,number,ytlink;
    private String upi,f_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_farmer_info);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        f_nametv = findViewById(R.id.f_name);
        agetv = findViewById(R.id.age);
        locationtv = findViewById(R.id.location);
        farmer_nametv = findViewById(R.id.farmer_name);
        farmer_infotv = findViewById(R.id.farmer_info);
        farmer_photo= findViewById(R.id.farmer_photo);
        donate_btn = findViewById(R.id.donate_btn);
        call = findViewById(R.id.call);
        yt_tv = findViewById(R.id.yt_tv);



        Toolbar toolbar = findViewById(R.id.product_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener( this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        View hd= navigationView.getHeaderView(0);
        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();


        pro=hd.findViewById(R.id.protxt);
        st= FirebaseFirestore.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        DocumentReference dr=st.collection("users").document(uid);
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                pro.setText(documentSnapshot.getString("name"));
            }
        });

        String qrID = getIntent().getStringExtra("qrID");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("qrcode").document(qrID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract field values from the document
                    f_name = document.getString("farmer_name");
                    String age = document.getString("age");
                    String location = document.getString("location");
                    String farmer_info = document.getString("farmer_info");
                    number = document.getString("call");
                    upi=document.getString("farmer_upi");
                    ytlink=document.getString("ytlink");

                    // Assign values to TextViews
                    f_nametv.setText(f_name);
                    agetv.setText(age);
                    locationtv.setText(location);
                    farmer_nametv.setText(f_name);
                    farmer_infotv.setText(farmer_info);
                    yt_tv.setText(ytlink);
                } else {
                    // Document doesn't exist
                }
            } else {
                // Error accessing Firestore
            }
        });

        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmerInfoActivity.this, PaymentActivity.class);
                intent.putExtra("upiID", upi);
                intent.putExtra("name", f_name);
                startActivity(intent);
            }
        });

        yt_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link=ytlink;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);

            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = number; // Make sure 'number' variable contains the phone number you want to call

                // Create the intent with ACTION_DIAL and the phone number
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + num));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Start the activity with the intent
                startActivity(intent);
            }
        });

    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){

            case R.id.settings:
                Intent intent = new Intent(FarmerInfoActivity.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.donationhis:
                Intent intent1 = new Intent(FarmerInfoActivity.this, Donation.class);
                startActivity(intent1);
                break;
            case R.id.help:
                Intent intent2 = new Intent(FarmerInfoActivity.this, Help.class);
                startActivity(intent2);
                break;
            case R.id.info:
                Intent intent4 = new Intent(FarmerInfoActivity.this, Information.class);
                startActivity(intent4);
                break;
            case R.id.logout:
                mAuth.signOut();
                // Sign out the user from Google Sign-In (if used)
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(this, gso).signOut();

                // Redirect the user to the login screen or perform any other required actions
                // For example, you can start a new activity or show a toast message
                if (this != null) {
                    Intent intent5 = new Intent(this, LoginActivity.class);
                    intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent5);
                    this.finish(); // Optional: Finish the current activity to prevent going back
                }
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}