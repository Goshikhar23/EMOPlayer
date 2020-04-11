package com.example.emoplayer.Music;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emoplayer.Adapter.AdapterEmotionSong;
import com.example.emoplayer.Model.Model_Songs;
import com.example.emoplayer.Model.Model_Users;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public HomeFragment() {
    }

    private TextView emotionTV;
    private TextView displayNameTV;
    private ImageButton cameraButton;
    private RecyclerView recommend_recyclerView;
    private RecyclerView emotion_recyclerView;

    private FirebaseUser user;
    private FirebaseFirestore databaseUser;
    private FirebaseFirestore databaseSong;

    private AdapterEmotionSong adapterEmotionSong;
    private ArrayList<Model_Songs> songList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseFirestore.getInstance();
        databaseSong = FirebaseFirestore.getInstance();

        getUserDetail();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmotion();
                getSongBasedOnYourEmotion();
            }
        });

        return view;
    }

    private void initViews(View view) {

        emotionTV = view.findViewById(R.id.home_emotionTv);
        displayNameTV = view.findViewById(R.id.home_displayName);
        cameraButton = view.findViewById(R.id.home_cameraButton);
        recommend_recyclerView = view.findViewById(R.id.home_recyclerView_recommendation);
        emotion_recyclerView = view.findViewById(R.id.home_recyclerView_emotion);

        emotion_recyclerView.setHasFixedSize(true);
        emotion_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void getUserDetail() {

        String uid = user.getUid();

        databaseUser.collection("Users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.d(TAG, "getUserDetail: Successful");

                        Model_Users model_users = documentSnapshot.toObject(Model_Users.class);
                        assert model_users != null;
                        Log.d(TAG, "getUserDetail: user = " + model_users.getUserName());

                        setDisplayName(model_users);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getUserDetail: failed " + e.getMessage());
            }
        });

    }

    private void setDisplayName(Model_Users model_users) {

        char c;
        String display;

        if (model_users.getUserName().equals("")) {
            c = model_users.getEmail().charAt(0);
        }

        c = model_users.getUserName().charAt(0);
        display = Character.toString(c);

        displayNameTV.setText(display);

    }

    @SuppressLint("SetTextI18n")
    private void getEmotion() {

        if (emotionTV.getText().equals("Happy")) {
            emotionTV.setText("Sad");
        } else {
            emotionTV.setText("Happy");
        }
    }

    private void getSongBasedOnYourEmotion() {

        databaseSong.collection("Songs")
                .whereEqualTo("songCategory", emotionTV.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            songList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Model_Songs model_song = document.toObject(Model_Songs.class);
                                songList.add(model_song);
                            }
                            adapterEmotionSong = new AdapterEmotionSong(getActivity(), songList);
                            emotion_recyclerView.setAdapter(adapterEmotionSong);

                        } else {
                            Log.d(TAG, "getSongBasedOnYourEmotion: Error getting documents: " + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getSongBasedOnYourEmotion: failed: " + e.getMessage());
            }
        });

    }

}



