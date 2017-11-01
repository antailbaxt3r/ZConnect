package com.zconnect.zutto.zconnect;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDate;

public class CounterManager {
    private static LocalDate dateTime = new LocalDate();
    private final static DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Counter").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dateTime.toString());

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

    public static void eventOpenCounter(final String eventId, final String type) {
        ref.keepSynced(true);

        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(eventId).child(type).child("openCount").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(eventId).child(type).child("openCount").getRef().setValue(count);
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


    public static void infoneOpenTab(final String tab) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void RecentsOpen() {
        ref.keepSynced(true);

        ref.child("Recents").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void infoneOpenCategory(final String tab, final String category) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(tab).child(category).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(tab).child(category).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneOpenContact(final String no) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                {
                    Long count = dataSnapshot.child(no).child("Open").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child(no).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void InfoneSearchClick() {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("SearchClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("SearchClick").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void InfoneContactAdded() {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("ContactAdd").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("ContactAdd").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void InfoneCallAfterProfile(final String to) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("CallProfile").child(to).getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("CallProfile").child(to).getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void InfoneCallDirect(final String to) {
        ref.keepSynced(true);

        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("CallDirect").child(to).getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("CallDirect").child(to).getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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

    public static void StoreRoomFABclick() {
        ref.keepSynced(true);

        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("FabClick").getValue(Long.class);
                    if (count == null)
                        count = (long) 0;
                    count = count + 1;
                    dataSnapshot.child("FabClick").getRef().setValue(count);
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
