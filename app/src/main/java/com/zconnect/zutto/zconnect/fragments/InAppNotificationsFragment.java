package com.zconnect.zutto.zconnect.fragments;

import android.content.SharedPreferences;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.InAppNotificationsAdapter;
import com.zconnect.zutto.zconnect.interfaces.OnLoadMoreListener;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

public class InAppNotificationsFragment extends Fragment {

    private RecyclerView notifRecyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SharedPreferences communitySP;
    public String communityRef;
    private DatabaseReference notificationsReference;
    private DatabaseReference globalReference;
    private DatabaseReference userNotifReference;
    private ValueEventListener listener;
    private TextView noNotif;
    Vector<InAppNotificationsItemFormat> totalnotificationsList;
    private InAppNotificationsAdapter inAppNotificationsAdapter;
    private ValueEventListener inAppNotifsListener;
    private Query inAppNotifsQuery;
    private String lastNotifID;
    private Long lastPostTimeMillis;

    public InAppNotificationsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifRecyclerView = (RecyclerView) view.findViewById(R.id.rv_notifications_fragment);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container_in_app_notifications);
        noNotif = view.findViewById(R.id.noNotif);
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityRef = communitySP.getString("communityReference", null);
        totalnotificationsList = new Vector<>();
        globalReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityRef);
        userNotifReference = globalReference.child("Users1").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("notifications");
        inAppNotifsQuery = userNotifReference.orderByChild("PostTimeMillis").limitToLast(20);
        defineListerners();
        notifRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        //Test Notification. Uncomment for testing
        //GlobalFunctions.pushNotifications("Test", "This is a test", false, 1, new HashMap<String, String>());
        Log.d("outside", "onDataChange: ");
        inAppNotificationsAdapter = new InAppNotificationsAdapter(getContext(), communityRef, totalnotificationsList, notifRecyclerView);
        inAppNotificationsAdapter.getLoadMoreUtility().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                totalnotificationsList.add(null);
                inAppNotificationsAdapter.notifyItemInserted(totalnotificationsList.size() - 1);

                //load more data
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //removing loader item
                        totalnotificationsList.remove(totalnotificationsList.size()-1);
                        inAppNotificationsAdapter.notifyItemRemoved(totalnotificationsList.size());
                        //load data
                        inAppNotifsQuery = userNotifReference.orderByChild("PostTimeMillis").endAt(lastPostTimeMillis, lastNotifID).limitToLast(20);
                        inAppNotifsQuery.addValueEventListener(inAppNotifsListener);
                    }
                }, 1000);
            }
        });
        notifRecyclerView.setAdapter(inAppNotificationsAdapter);
        return view;
    }

    private void defineListerners() {
        inAppNotifsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer index = 0;
                if(lastNotifID==null)
                {
                    Log.d("IIIIII", "HERE");
                    totalnotificationsList.clear();
                }
                Vector<InAppNotificationsItemFormat> notificationsListTemp = new Vector<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    try {
                        index++;
                        if(index==1)
                        {
                            lastNotifID = childSnapshot.child("key").getValue().toString();
                            lastPostTimeMillis = childSnapshot.child("PostTimeMillis").getValue(Long.class);
                            continue;
                        }
                        InAppNotificationsItemFormat usernotification = childSnapshot.getValue(InAppNotificationsItemFormat.class);
                        if (usernotification != null && !usernotification.getNotifiedBy().getUserUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            notificationsListTemp.add(usernotification);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Collections.reverse(notificationsListTemp);
                totalnotificationsList.addAll(notificationsListTemp);
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmerAnimation();

                notifRecyclerView.setVisibility(View.VISIBLE);
                if (!totalnotificationsList.isEmpty()) {
                    noNotif.setVisibility(View.GONE);
                    notifRecyclerView.setVisibility(View.VISIBLE);
                    inAppNotificationsAdapter.notifyDataSetChanged();
                    boolean isUnread = false;
                    for(InAppNotificationsItemFormat notificationsItemFormat : totalnotificationsList){
                        try{
                            Log.d("NOTIFICATION",notificationsItemFormat.isSeen().get(UserUtilities.currentUser.getUserUID()).toString());
                            if(!notificationsItemFormat.isSeen().get(UserUtilities.currentUser.getUserUID())){
                                TabLayout tabs = getActivity().findViewById(R.id.navigation);
                                tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
                                isUnread = true;
                            }
                        }
                        catch (Exception e){
                            TabLayout tabs = getActivity().findViewById(R.id.navigation);
                            tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
                            Log.d("NOTIFICATIONERROR",e.toString());
                        }
                    }

                    if(!isUnread){
                        TabLayout tabs = getActivity().findViewById(R.id.navigation);
                        tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);

                    }

                } else {
                    noNotif.setVisibility(View.VISIBLE);
                    notifRecyclerView.setVisibility(View.GONE);
                    TabLayout tabs = getActivity().findViewById(R.id.navigation);
                    tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);
                }
                if(lastNotifID!=null)
                {
                    inAppNotificationsAdapter.getLoadMoreUtility().setLoaded();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserUtilities.currentUser != null) {
            if (!UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || !UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                Log.d("MMMMM", "MMMM");
                Log.d("AAAAAAAAA", "RESUME");
                if(!totalnotificationsList.isEmpty())
                {
                    inAppNotifsQuery = userNotifReference.orderByChild("PostTimeMillis").limitToLast(20);
                    lastNotifID=null;
                    lastPostTimeMillis=0L;
                }
                inAppNotifsQuery.addValueEventListener(inAppNotifsListener);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        inAppNotifsQuery.removeEventListener(inAppNotifsListener);
    }
}