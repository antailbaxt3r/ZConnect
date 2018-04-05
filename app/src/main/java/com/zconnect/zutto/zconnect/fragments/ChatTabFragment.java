package com.zconnect.zutto.zconnect.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ChatTabRVItem;
import com.zconnect.zutto.zconnect.PhonebookDetails;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ZConnectDetails;
import com.zconnect.zutto.zconnect.adapters.ChatTabRVAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    DatabaseReference databaseReferenceMessages;
    ValueEventListener listener;
    private SharedPreferences communitySP;
    public String communityReference;
    RecyclerView recyclerView;
    ChatTabRVAdapter chatTabRVAdapter;
    ArrayList<ChatTabRVItem> chatTabRVItems;
    ChatTabRVItem chatTabRVItem;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public ChatTabFragment() {
        // Required empty public constructor
    }

    public static ChatTabFragment newInstance(String param1, String param2) {
        ChatTabFragment fragment = new ChatTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat_tab, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_chat_messages);

        communitySP = getContext().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceMessages = FirebaseDatabase.getInstance().getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        databaseReferenceMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatTabRVItems = new ArrayList<>();
                String name,key;
                for (DataSnapshot childsnapShot :
                        dataSnapshot.child("communities").child(communityReference).child("features").child("messages").child("users").child(user.getUid()).getChildren()) {
                    key = childsnapShot.getKey();
                    name = dataSnapshot.child("communities").child(communityReference).child("Users").child(key).child("Username").getValue().toString();
                    //Log.e("counter",key+name);
                    chatTabRVItem = new ChatTabRVItem(key+name);
                    chatTabRVItems.add(chatTabRVItem);

                }

                chatTabRVAdapter = new ChatTabRVAdapter(getContext(), chatTabRVItems);
                recyclerView.setAdapter(chatTabRVAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        databaseReferenceMessages.removeEventListener(listener);
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
