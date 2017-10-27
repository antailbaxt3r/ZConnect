package com.zconnect.zutto.zconnect;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CounterManager {

    private final static DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Counter");

    public static void eventOpenCounter(final String eventId) {
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalOpen").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(eventId).child("openCount").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(eventId).child("openCount").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventReminderCounter(final String eventId) {
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalReminder").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalReminder").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(eventId).child("ReminderCount").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalPicOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalPicOpen").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(eventId).child("openPicture").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(eventId).child("openPicture").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventBoost(final String eventId) {
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalBoost").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalBoost").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(eventId).child("Boost").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(eventId).child("Boost").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void eventShare(final String eventId) {
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalShare").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalShare").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(eventId).child("Share").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalGetDirection").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalGetDirection").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(eventId).child("getDirection").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Events").child("Verified").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("EventAdded").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Events").child("Unverified").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("EventAdded").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalCategoryClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCategoryClick").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(category).getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Search").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalOffersClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalOffersClick").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(id).child("OffersClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalShopDetails").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalShopDetails").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(id).child("Details").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalOffersClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalOffersClick").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void shopDirections(final String id) {
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalDirectionsClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalDirectionsClick").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(id).child("DirectionsClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalCallClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCallClick").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(id).child("CallClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalGalleyClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalGalleyClick").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(id).child("GalleryClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalProductsClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalProductsClick").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(id).child("ProductClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(id).child("ProductClick").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public static void storeRoomProductClick(final String id) {
//        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                {
//                    Long count = dataSnapshot.child("TotalProductsClick").getValue(Long.class);
//                    if (count == null)
//                        count = Long.parseLong("0");
//                    count = count + 1;
//                    dataSnapshot.child("TotalProductsClick").getRef().setValue(count);
//                }
//                {
//                    Long count = dataSnapshot.child(id).child("ProductClick").getValue(Long.class);
//                    if (count == null)
//                        count = Long.parseLong("0");
//                    count = count + 1;
//                    dataSnapshot.child(id).child("ProductClick").getRef().setValue(count);
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


    public static void infoneOpenTab(final String tab) {
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalTabOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalTabOpen").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(tab).child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(tab).child("Open").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void infoneOpenCategory(final String tab, final String category) {
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child(tab).child("TotalCategoryOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCategoryOpen").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(tab).child(category).child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalContactOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalContactOpen").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(no).child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("SearchClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("ContactAdd").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("ContactAdd").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void InfoneCallAfterProfile() {
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("CallProfile").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("CallProfile").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void InfoneCallDirect() {
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("CallDirect").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("CallDirect").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void StoreRoomMyProductOpen() {
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("MyProductOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("MyProductOpen").getRef().setValue(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoreRoomMyProductDelete() {
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("MyProductDelete").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("FabClick").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalCategoryOpen").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCategoryOpen").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(category).child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalAdd").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalAdd").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(category).child("addition").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(category).child("addition").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomShortList(final String category) {
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalShortlist").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalShortlist").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(category).child("ShortList").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(category).child("ShortList").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomShortListDelete(final String category) {
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalShortlistDelete").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalShortlistDelete").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(category).child("ShortListDelete").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child(category).child("ShortListDelete").getRef().setValue(count);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void StoroomCall(final String category) {
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalCall").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCall").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(category).child("Calls").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("StoreRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalCabPool").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCabPool").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(dest).child("Pools").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("TotalCabPoolSearch").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
                    count = count + 1;
                    dataSnapshot.child("TotalCabPoolSearch").getRef().setValue(count);
                }
                {
                    Long count = dataSnapshot.child(dest).child("PoolSearch").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Infone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("CabPool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Map").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
        ref.child("Map").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Long count = dataSnapshot.child("Open").getValue(Long.class);
                    if (count == null)
                        count = Long.parseLong("0");
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
