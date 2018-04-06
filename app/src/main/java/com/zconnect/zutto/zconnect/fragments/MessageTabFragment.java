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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ChatTabRVItem;
import com.zconnect.zutto.zconnect.ItemFormats.MessageTabRVItem;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ZConnectDetails;
import com.zconnect.zutto.zconnect.adapters.ChatTabRVAdapter;
import com.zconnect.zutto.zconnect.adapters.MessageTabRVAdapter;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatabaseReference databaseReferenceMessages;
    ValueEventListener listener;
    private SharedPreferences communitySP;
    public String communityReference;
    RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<MessageTabRVItem> messageTabRVItems;
    MessageTabRVAdapter messageTabRVAdapter;
    MessageTabRVItem messageTabRVItem;

    private OnFragmentInteractionListener mListener;

    public MessageTabFragment() {
        // Required empty public constructor
    }

    public static MessageTabFragment newInstance(String param1, String param2) {
        MessageTabFragment fragment = new MessageTabFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_message_tab, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_messages);

        communitySP = getContext().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        databaseReferenceMessages = FirebaseDatabase.getInstance().getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        databaseReferenceMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name,key,namecheck;
                int incount=0,last=0,now=1,change=0,success=0;
                messageTabRVItems=new ArrayList<>();
                for (DataSnapshot childsnapShot :
                        dataSnapshot.child("communities").child(communityReference).child("features").child("messages").child("users").child(user.getUid()).getChildren()) {
                    key = childsnapShot.getKey();
                    name = dataSnapshot.child("communities").child(communityReference).child("Users").child(key).child("Username").getValue().toString();
                    //Log.e("counter",key+name);
                    namecheck = name;
                    //Log.e("in messagetabfragment","yo");
                    for (DataSnapshot childsnapShot2 :
                            dataSnapshot.child("communities").child(communityReference).child("features").child("messages").child("users").child(user.getUid()).child(key).getChildren())
                    {
                        String k=childsnapShot2.getValue().toString();
                        if(dataSnapshot.child("communities").child(communityReference).child("features").child("messages").child("chats").child(k).child("sender").getValue().toString().equals(user.getUid()))
                        {
                            //Log.e("messageif",k);
                            //cif.setName(user.getDisplayName());
                            //cif.setUuid(myuid);
                            if(incount==0)
                            {
                                //setToolbarTitle(recpname);
                                ++incount;
                                success=1;
                            }
                            else {
                                last = now;
                                now = 0;
                                if (last != now) {
                                    ++change;
                                }
                            }
                        }
                        else
                        {
                            //Log.e("messageelse",k);
                            //cif.setUuid(recpuid);
                            //cif.setName("Anonymous");
                            namecheck="Anonymous";
                            if(incount==0)
                            {
                                ++incount;
                            }
                            if(success==1)
                            {
                                namecheck=dataSnapshot.child("communities").child(communityReference).child("Users").child(key).child("Username").getValue().toString();
                                //setToolbarTitle(recpname);
                                //cif.setName(recpname);
                            }
                            else {
                                last = now;
                                now = 1;
                                if (last != now) {
                                    ++change;
                                }
                                if (change >= 2) {
                                    namecheck=dataSnapshot.child("communities").child(communityReference).child("Users").child(key).child("Username").getValue().toString();
                                    //setToolbarTitle(recpname);
                                    //cif.setName(recpname);
                                }
                            }
                        }
                    }
                    if(!namecheck.equals(name)) {
                        messageTabRVItem = new MessageTabRVItem(key + name);
                        messageTabRVItems.add(messageTabRVItem);
                    }

                }
                messageTabRVAdapter = new MessageTabRVAdapter(getContext(), messageTabRVItems);
                recyclerView.setAdapter(messageTabRVAdapter);
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
