package com.example.fauza.golang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fauza.golang.R;
import com.example.fauza.golang.model.Member;
import com.example.fauza.golang.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FragmentGiveRating extends Fragment {
    public static final String argsKeyTourGuideRequests = "keyTourGuideRequest";
    public static final String argsIdTourGuide = "idTourGuide";

    private TextView textViewGiveRating;
    private FirebaseUtils firebaseUtils = new FirebaseUtils();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_give_rating,
                container,
                false);

        textViewGiveRating = view.findViewById(R.id.tv_give_rating);
        String idTourGuide = getArguments().getString(argsIdTourGuide);
        if (idTourGuide != null) {
            Query query = firebaseUtils.getRef()
                    .child(getString(R.string.members))
                    .child(idTourGuide);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Member member = dataSnapshot.getValue(Member.class);
                    String placeHolder = null;
                    if (member != null) {
                        placeHolder = "Ratingnya" + member.getMemberName();
                    }
                    textViewGiveRating.setText(placeHolder);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        textViewGiveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyTourGuideRequest = getArguments().getString(argsKeyTourGuideRequests);
                HashMap<String, Object> updateMap = new HashMap<>();
                updateMap.put(getString(R.string.idMember_status), firebaseUtils.getUser().getUid() + "_" + getString(R.string.TOUR_STATUS_COMPLETED));
                updateMap.put(getString(R.string.requestStatus), FragmentGiveRating.this.getResources().getInteger(R.integer.TOUR_STATUS_RATED));
                if (keyTourGuideRequest != null) {
                    firebaseUtils.getRef().child(getString(R.string.tourGuideRequests))
                            .child(keyTourGuideRequest)
                            .updateChildren(updateMap);
                }
            }
        });
        return view;
    }
}
