package com.example.fauza.golang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauza.golang.R;
import com.example.fauza.golang.fragment.FragmentGiveRating;
import com.example.fauza.golang.fragment.FragmentHomeMember;
import com.example.fauza.golang.fragment.FragmentHomeMemberCreateRequest;
import com.example.fauza.golang.model.TourGuideRequest;
import com.example.fauza.golang.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class HomeMemberActivity extends AppCompatActivity implements ValueEventListener {

    private final String TAG = "HomeMemberActivity";

    private TextView textViewCurrentUser;
    private Toolbar toolbarHome;

    private FirebaseUtils firebaseUtils = new FirebaseUtils();
    private ValueEventListener veListener;
    private ChildEventListener ceListener;
    private Query query1;

    FragmentHomeMember fragmentHomeMember;
    FragmentHomeMemberCreateRequest fragmentHomeMemberCreateRequest;
    FragmentGiveRating fragmentGiveRating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_member);

        textViewCurrentUser = findViewById(R.id.textView_current_user);
        toolbarHome = findViewById(R.id.toolbar_home);

        // Set textView text with the current signed in user
        setUser();

        toolbarHome.setLogo(R.drawable.ic_account);
        setSupportActionBar(toolbarHome);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        fragmentHomeMember = new FragmentHomeMember();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_home_member, fragmentHomeMember)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        query1 = firebaseUtils.getRef()
                .child(getString(R.string.tourGuideRequests))
                .orderByChild(getString(R.string.idMember_status))
                .equalTo(firebaseUtils.getUser().getUid() + "_" + getString(R.string.TOUR_STATUS_INPROGRESS));
        veListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = null;
                    TourGuideRequest tourGuideRequest = null;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        key = ds.getKey();
                        tourGuideRequest = ds.getValue(TourGuideRequest.class);
                    }
                    fragmentHomeMemberCreateRequest = new FragmentHomeMemberCreateRequest();
                    Bundle data = new Bundle();
                    data.putString(FragmentHomeMemberCreateRequest.argsKeyTourGuideRequest, key);
                    if (tourGuideRequest != null) {
                        data.putString(FragmentHomeMemberCreateRequest.argsIdTourGuide, tourGuideRequest.getIdTourGuide());
                    }
                    fragmentHomeMemberCreateRequest.setArguments(data);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_home_member, fragmentHomeMemberCreateRequest)
                            .commit();
                    if (tourGuideRequest != null
                            && tourGuideRequest.getRequestStatus()
                            == HomeMemberActivity.this.getResources().getInteger(R.integer.TOUR_STATUS_COMPLETED)) {
                        fragmentGiveRating = new FragmentGiveRating();
                        data.putString(FragmentGiveRating.argsKeyTourGuideRequests, key);
                        fragmentGiveRating.setArguments(data);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_home_member, fragmentGiveRating)
                                .commit();
                    }
                } else {
                    fragmentHomeMember = new FragmentHomeMember();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_home_member, fragmentHomeMember)
                            .commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query1.addValueEventListener(veListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        query1.removeEventListener(veListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sign_out:
                FirebaseAuth.getInstance().signOut();
                explicitIntent(this, LoginActivity.class);
                HomeMemberActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void explicitIntent(Activity activity, Class _class) {
        Intent explicitIntent = new Intent(activity, _class);
        startActivity(explicitIntent);
    }

    private void setUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.textViewCurrentUser.setText(user.getEmail());
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
