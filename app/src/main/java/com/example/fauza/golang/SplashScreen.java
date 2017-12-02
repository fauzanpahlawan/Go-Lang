package com.example.fauza.golang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.fauza.golang.activity.HomeMemberActivity;
import com.example.fauza.golang.activity.HomeTourGuideActivity;
import com.example.fauza.golang.activity.LoginActivity;
import com.example.fauza.golang.model.Member;
import com.example.fauza.golang.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity implements ValueEventListener {

    private FirebaseUtils firebaseUtils = new FirebaseUtils();
    private Class[] classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_splash_screen);

        classes = new Class[3];
        classes[0] = LoginActivity.class;
        classes[1] = HomeTourGuideActivity.class;
        classes[2] = HomeMemberActivity.class;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUtils.getUser() != null) {
            Query query = firebaseUtils.getRef().child("members").orderByKey().equalTo(firebaseUtils.getUser().getUid());
            query.addValueEventListener(this);
        } else {
            explicitIntent(this, classes[0]);
        }
    }

    private void explicitIntent(Activity activity, Class mClass) {
        Intent explicitIntent = new Intent(activity, mClass);
        this.startActivity(explicitIntent);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Member member = postSnapshot.getValue(Member.class);
                if (member != null) {
                    explicitIntent(SplashScreen.this, classes[Integer.valueOf(member.getType())]);
                    Log.i("Login", member.getType());
                }
            }
        } else {
            Log.i("Login", "Data Empty");
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
