package com.zconnect.zutto.zconnect;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

import org.joda.time.LocalDate;

public class CounterManager extends BaseActivity {

    public static DatabaseReference ref;


// Infone

    public static void infoneOpen() {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneAddCategory() {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("CategoryAdded").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("CategoryAdded").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneOpenCategory(final String categoryID) {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(categoryID).child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(categoryID).child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneAddContact(final String categoryID) {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(categoryID).child("ContactAdded").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(categoryID).child("ContactAdded").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneOpenContact(final String categoryID, final String contactID) {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(categoryID).child(contactID).child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(categoryID).child(contactID).child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneCallContact() {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Call").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Call").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneVerifyContact() {
        ref.keepSynced(true);
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("verify").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("verify").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//Forums

    public static void forumsOpen() {
        ref.keepSynced(true);
        ref.child("Forums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void forumsOpenTab(final String tab) {
        ref.keepSynced(true);
        ref.child("Forums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(tab).child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(tab).child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void forumsOpenCategory(final String tab,final String catUID) {
        ref.keepSynced(true);
        ref.child("Forums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(tab).child(catUID).child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(tab).child(catUID).child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void forumsLeaveCategory(final String tab,final String catUID) {
        ref.keepSynced(true);
        ref.child("Forums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(tab).child(catUID).child("Leave").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(tab).child(catUID).child("Leave").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void forumsJoinCategory(final String tab,final String catUID) {
        ref.keepSynced(true);
        ref.child("Forums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(tab).child(catUID).child("Join").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(tab).child(catUID).child("Join").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void forumsAddCategory(final String tab) {
        ref.keepSynced(true);
        ref.child("Forums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child(tab).child("CategoryAdded").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child(tab).child("CategoryAdded").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void anonymousMessageOpen() {
        ref.keepSynced(true);
        ref.child("AnonymousMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Open").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Open").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void anonymousMessageSend() {
        ref.keepSynced(true);
        ref.child("AnonymousMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Send").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Send").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void anonymousMessageAccept() {
        ref.keepSynced(true);
        ref.child("AnonymousMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Accept").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Accept").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void anonymousMessageDelete() {
        ref.keepSynced(true);
        ref.child("AnonymousMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.child("Delete").getValue(Long.class);
                if(count == null) {
                    count = (long) 0;
                }
                count = count + 1;
                dataSnapshot.child("Delete").getRef().setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void publicStatusAdd(final boolean anonymous) {
        ref.keepSynced(true);
        ref.child("PublicStatus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(anonymous)
                {
                    Long count = dataSnapshot.child("Anonymous").getValue(Long.class);
                    if(count == null) {
                        count = (long) 0;
                    }
                    count = count + 1;
                    dataSnapshot.child("Anonymous").getRef().setValue(count);
                }
                else {
                    Long count = dataSnapshot.child("Known").getValue(Long.class);
                    if(count == null) {
                        count = (long) 0;
                    }
                    count = count + 1;
                    dataSnapshot.child("Known").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void publicStatusAddClick() {
        ref.keepSynced(true);
        ref.child("PublicStatus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if(count == null) {
                        count = (long) 0;
                    }
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventOpenClick() {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventOpenTab(final String tab) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(tab).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(tab).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventOpenCounter(final String eventId) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child("openCount").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child("openCount").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventAddClick() {

        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("AddEvent").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("AddEvent").getRef().setValue(count);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void eventReminderCounter(final String eventId) {

        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child("ReminderCount").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child("ReminderCount").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventOpenPic(final String eventId) {

        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child("openPicture").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child("openPicture").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventBoost(final String eventId, final String type) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child(type).child("Boost").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child(type).child("Boost").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventEdit(final String eventId, final String type) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child(type).child("Edit").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child(type).child("Edit").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventShare(final String eventId) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child("Share").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child("Share").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventgetDirection(final String eventId) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child("getDirection").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child("getDirection").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addEventVerified(final String id, final String name) {
        ref.keepSynced(true);

        ref.child("Events").child("Verified").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("EventAdded").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("EventAdded").getRef().setValue(count);
                    dataSnapshot.getRef().child(id).child("name").setValue(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addEventUnVerified(final String id, final String name) {
        ref.keepSynced(true);

        ref.child("Events").child("Unverified").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("EventAdded").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("EventAdded").getRef().setValue(count);
                    dataSnapshot.getRef().child(id).child("name").setValue(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopCategoryOpen(final String category) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(category).getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(category).getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopSearch() {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Search").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Search").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopOffers(final String id) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(id).child("OffersClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(id).child("OffersClick").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopDetails(final String id) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(id).child("Details").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(id).child("Details").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopOffers() {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("AllOfferClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("AllOfferClick").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopDirections(final String id) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(id).child("DirectionsClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(id).child("DirectionsClick").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopCall(final String id) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(id).child("CallClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(id).child("CallClick").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopGallery(final String id) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(id).child("GalleryClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(id).child("GalleyClick").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopProducts(final String id) {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(id).child("ProductClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(id).child("ProductClick").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




//    public static void infoneOpenCategory(final String tab, final String category) {
//        ref.keepSynced(true);
//
//        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                {
//                    Long count = dataSnapshot.child(tab).child(category).child("Open").getValue(Long.class);
//                    if (count == null)
//                        count = (long) 0;
//                    count = count + 1;
//                    dataSnapshot.child(tab).child(category).child("Open").getRef().setValue(count);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    public static void infoneOpenContact(final String no) {
//        ref.keepSynced(true);
//
//        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                {
//                    Long count = dataSnapshot.child(no).child("Open").getValue(Long.class);
//                    if (count == null)
//                        count = (long) 0;
//                    count = count + 1;
//                    dataSnapshot.child(no).child("Open").getRef().setValue(count);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


//    public static void InfoneSearchClick() {
//        ref.keepSynced(true);
//
//        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                {
//                    Long count = dataSnapshot.child("SearchClick").getValue(Long.class);
//                    if (count == null)
//                        count = (long) 0;
//                    count = count + 1;
//                    dataSnapshot.child("SearchClick").getRef().setValue(count);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public static void InfoneContactAdded() {
//        ref.keepSynced(true);
//
//        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                {
//                    Long count = dataSnapshot.child("ContactAdd").getValue(Long.class);
//                    if (count == null)
//                        count = (long) 0;
//                    count = count + 1;
//                    dataSnapshot.child("ContactAdd").getRef().setValue(count);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public static void InfoneCallAfterProfile(final String to) {
//        ref.keepSynced(true);
//
//        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                {
//                    Long count = dataSnapshot.child("CallProfile").child(to).getValue(Long.class);
//                    if (count == null)
//                        count = (long) 0;
//                    count = count + 1;
//                    dataSnapshot.child("CallProfile").child(to).getRef().setValue(count);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public static void InfoneCallDirect(final String to) {
//        ref.keepSynced(true);
//
//        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                {
//                    Long count = dataSnapshot.child("CallDirect").child(to).getValue(Long.class);
//                    if (count == null)
//                        count = (long) 0;
//                    count = count + 1;
//                    dataSnapshot.child("CallDirect").child(to).getRef().setValue(count);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public static void email(final String to) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Email").child(to).getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Email").child(to).getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void report(final String to) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("report").child(to).getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("report").child(to).getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void StoreRoomMyProductOpen() {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("MyProductOpen").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("MyProductOpen").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoreroomOpenTab(final String tab) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(tab).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(tab).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoreRoomMyProductDelete() {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("MyProductDelete").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("MyProductDelete").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoreRoomAddClick() {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("AddClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("AddClick").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomCategory(final String category) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(category).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(category).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomAddProduct(final String category) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(category).child("addition").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(category).child("addition").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomShortList(final String category, final String key) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(category).child(key).child("ShortList").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(category).child(key).child("ShortList").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomShortListDelete(final String category, final String key) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(category).child(key).child("ShortListDelete").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(category).child(key).child("ShortListDelete").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomCall(final String category) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(category).child("Calls").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(category).child("Calls").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomOpenProduct(final String productKey) {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(productKey).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(productKey).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoreRoomOpen() {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void createPool(final String dest) {
        ref.keepSynced(true);

        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(dest).child("Pools").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(dest).child("Pools").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void searchPool(final String dest) {
        ref.keepSynced(true);

        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(dest).child("PoolSearch").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(dest).child("PoolSearch").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void ShopOpen() {
        ref.keepSynced(true);

        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void InfoneOpen() {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void EventOpen() {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void openCabPool() {
        ref.keepSynced(true);

        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void openCabPoolList(final String CabID) {
        ref.keepSynced(true);

        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(CabID).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(CabID).child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void openCabPoolJoin(final String CabID) {
        ref.keepSynced(true);

        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(CabID).child("Join").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(CabID).child("Join").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void openCabPoolLeave(final String CabID) {
        ref.keepSynced(true);

        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(CabID).child("Leave").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(CabID).child("Leave").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void openMyRides() {
        ref.keepSynced(true);

        ref.child("CabPool").child("MyRides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void MapOpen() {
        ref.keepSynced(true);

        ref.child("Map").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void AdvertisementOpen() {
        ref.keepSynced(true);

        ref.child("Map").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("Open").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
