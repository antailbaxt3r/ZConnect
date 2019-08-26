
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();



// exports.countlikes = functions.database.ref('/communities/{communityID}/Users1').onWrite(( change,event)  => {
//   return  admin.database().ref('/communitiesInfo/' + event.params.communityID+'/size').set(event.data.numChildren());
// });

// exports.countTotalComments = functions.database.ref('/communities/{communityID}/home/{postID}/Chat').onWrite(( change,event)  => {
//   return  admin.database().ref('/communities/'+ event.params.communityID +'/home/{postID}/msgComments').set( change.after.numChildren());
// });


// Keeps track of the length of the 'likes' child list in a separate property.
exports.countlikechange = functions.database.ref('/communities/{communityID}/home/{postID}/Chat/{ChatID}').onWrite(
    (change) => {
      const collectionRef = change.after.ref.parent;
      const countRef = collectionRef.parent.child('msgComments');

      let increment;
      if (change.after.exists() && !change.before.exists()) {
        increment = 1;
      } else if (!change.after.exists() && change.before.exists()) {
        increment = -1;
      } else {
        return null;
      }

      // Return the promise from countRef.transaction() so our function
      // waits for this async event to complete before it exits.
      return countRef.transaction((current) => {
        return (current || 0) + increment;
      }).then(() => {
        return console.log('Counter updated.');
      });
    });

// If the number of likes gets deleted, recount the number of likes
exports.recountlikes = functions.database.ref('/communities/{communityID}/home/{postID}/msgComments').onDelete((snap) => {
  const counterRef = snap.ref;
  const collectionRef = counterRef.parent.child('Chat');

  // Return the promise from counterRef.set() so our function
  // waits for this async event to complete before it exits.
  return collectionRef.once('value')
      .then((messagesData) => counterRef.set(messagesData.numChildren()));
});


exports.grantSignupReward = functions.database.ref('/communities/{communityID}/Users1/{uid}/referredBy').onCreate((snapshot, context) => {
  
  var uid = context.params.uid;
  var communityID = context.params.communityID;

  return admin.database().ref(`communities/${communityID}/Users1/${uid}/referredBy`)
  .once('value').then(data => {
    var referred_by_uid = data.val();
    if (referred_by_uid) {
      var points = admin.database().ref(`/communities/${communityID}/Users1/${referred_by_uid}/userPoints/`);
      points.transaction(current_value => {
        return String((parseInt(current_value) || 0) + 50);
      });

      var points2 = admin.database().ref(`/communities/${communityID}/Users1/${uid}/userPoints/`);
      points2.transaction(current_value => {
        return String((parseInt(current_value) || 0) + 50);
      });
    }
     return console.log('Referal Added');
  });
});

exports.countInfoneMembers = functions.database.ref('/communities/{communityID}/infone/numbers/{contactID}/category')
.onCreate((snap, context) => {
    console.log("snapshot val", snap.val());
    
    const communityID = context.params.communityID;
    const contactID = context.params.contactID;

    const category = snap.val();
    console.log("category", category);

    const collectionRef = snap.ref.parent.parent;
    const countRef = collectionRef.parent.child(`categoriesInfo/${category}/totalContacts`);

    // Return the promise from countRef.transaction() so our function
      // waits for this async event to complete before it exits.
      return collectionRef.parent.child(`categories/${category}`).once('value')
        .then((numbersData) => countRef.set(numbersData.numChildren()));
	});

exports.removeJoinedCommunity = functions.database.ref('/communities/{communityID}/home/{postID}/feature').onCreate((snap, context) => {
  const feature = snap.val();
  const nodeRef = snap.ref;
  console.log("snapshot", snap.val());
  console.log("feature", feature);
  if(feature === "Users")
  {
    return nodeRef.parent.remove();
  }
  else
    return null;
});

exports.countCommunityMembers = functions.database.ref('/communities/{communityID}/Users1/{uid}/userUID').onWrite((change, context) => {
  
  const communityID = context.params.communityID;
  const collectionRef = change.after.ref.parent.parent;
  const countRef = collectionRef.parent.parent.parent.child(`communitiesInfo/${communityID}/size`);

  return collectionRef.once('value')
    .then((usersData) => countRef.set(usersData.numChildren()));
});

exports.syncTry = functions.database.ref('/communities/{communityID}/Users1/{uid}/')
.onUpdate((change, context) => {
	
	//temporarily if username and image does not change return null
	if(change.before.child('username').val() === change.after.child('username').val() 
		&& change.before.child('imageURLThumbnail').val() === change.after.child('imageURLThumbnail').val())
	{
		console.log("No need");
		return null;
	}
	

	const userRef = change.after.ref;
	const communityID = context.params.communityID;
	return userRef.on('value', snapshot => {
		let updateObj = {};
		let homePostsKeys = Object.keys(snapshot.child("homePosts").val());
		homePostsKeys.forEach(key => {
			updateObj[`/communities/${communityID}/home/${key}/PostedBy/ImageThumb`] = snapshot.child('imageURLThumbnail').val();
			updateObj[`/communities/${communityID}/home/${key}/PostedBy/Username`] = snapshot.child('username').val();
		});
		console.log("BBB", updateObj);
		// return null;
		return userRef.root.update(updateObj);
	});
});

exports.countForumMembers = functions.database.ref('/communities/{communityID}/features/forums/tabsCategories/{tabID}/{forumID}/users')
.onUpdate((change, context) => {
	const totalMembersRef = change.after.ref.parent.child("totalMembers");
	return totalMembersRef.update(change.after.numChildren());
});

exports.countTotalMessages = functions.database.ref('/communities/{communityID}/features/forums/categories/{catID}')
.onWrite(async (change, context)  => {
  
  const communityID = context.params.communityID;
  const catID = context.params.catID;
  const tabID = change.after.child("tab").val();
  const countRef = change.after.ref.parent.parent.child(`tabsCategories/${tabID}/${catID}/totalMessages`);
  const count = change.after.child("Chat").numChildren();
  
  return countRef.update(count);

  // return  admin.database().ref('/communities/'+ event.params.communityID +'/features/forums/tabsCategories/'+ event.data.child("tab").val()+'/' + event.params.catID+'/totalMessages').set(event.data.child("Chat").numChildren());
});

exports.syncForumToUserForum_Add = functions.database.ref('/communities/{communityID}/features/forums/tabsCategories/{tabID}/{forumID}/users/{uid}')
.onCreate((snapshot, context) => {
  const uid = context.params.uid;
  const forumID = context.params.forumID;
  const userForumRef = snapshot.ref.parent.parent.parent.parent.parent.child("userForums").child(uid).child("joinedForums");
  const forumRef = snapshot.ref.parent.parent;
  return forumRef.once('value', forumSnapshot => {
    const forumObj = forumSnapshot.val();
    if(forumObj.tabUID==="personalChats")
    {
      delete forumObj["name"];
    }
    // else
    // {
      delete forumObj["users"];
    // }
    return userForumRef.child(forumID).update(forumObj);
  });
});

exports.syncForumToUserForum_Update = functions.database.ref('/communities/{communityID}/features/forums/tabsCategories/{tabID}/{forumID}')
.onUpdate((change, context) => {
  const forumID = context.params.forumID;
  const snapshot = change.after;
  const forumObj = snapshot.val();
  delete forumObj["users"];
  if(forumObj.tabUID==="personalChats")
  {
    delete forumObj["name"];
  }
  const userForumListRef = snapshot.ref.parent.parent.parent.child("userForums");
  const joinedUsersListSnapshot = snapshot.child('users');
  return joinedUsersListSnapshot.forEach(userSnapshot => {
    return userForumListRef.child(userSnapshot.key).child("joinedForums").child(forumID).update(forumObj);
  });
});

exports.syncForumToUserForum_Delete = functions.database.ref('/communities/{communityID}/features/forums/tabsCategories/{tabID}/{forumID}/users/{uid}')
.onDelete((snapshot, context) => {
  const uid = context.params.uid;
  const forumID = context.params.forumID;
  const joinedForumRef = snapshot.ref.parent.parent.parent.parent.parent.child("userForums").child(uid).child("joinedForums");
  return joinedForumRef.child(forumID).remove();
});

exports.countNumberOfForums = functions.database.ref('/communities/{communityID}/features/forums/userForums/{uid}/joinedForums')
.onWrite((change, context) => {
  const totalJoinedForumsRef = change.after.ref.parent.child('totalJoinedForums');
  return totalJoinedForumsRef.set(change.after.numChildren());
});


exports.updateLastMessageAfterDeletion = functions.database.ref('communities/{communityID}/features/forums/tabsCategories/{tabID}/{forumID}/lastMessage')
.onDelete((snapshot, context) => {
  const forumID = context.params.forumID;
  const chatsRef = snapshot.ref.parent.parent.parent.parent.child('categories').child(forumID).child('Chat');
  return chatsRef.orderByChild('timeDate').limitToLast(1).once('value', messageSnapshot => {
    const messageKey = Object.keys(messageSnapshot.val())[0];
    const secondLastMessage = messageSnapshot.val()[messageKey];
    secondLastMessage["key"] = messageKey;
    return snapshot.ref.set(secondLastMessage);
  });
});

exports.addTitleImagePersChatUserForums = functions.database.ref('/communities/{communityID}/features/forums/tabsCategories/personalChats/{forumID}')
.onCreate((snapshot, context) => {
  // return;
  let { forumID } = context.params;
  snapshot.ref.child('totalMessages').set(0);
  console.log("1 ", forumID, snapshot.val());
  if(Object.keys(snapshot.child("users").val()).length!==2)
    return console.log("Both users did not get added in the personal chat at once");
  const uid1 = Object.keys(snapshot.child("users").val())[0];
  const uid2 = Object.keys(snapshot.child("users").val())[1];
  console.log("User 1 ", uid1, "User 2 ", uid2);
  const userForumRef1 = snapshot.ref.parent.parent.parent.child("userForums").child(uid1).child("joinedForums");
  const userForumRef2 = snapshot.ref.parent.parent.parent.child("userForums").child(uid2).child("joinedForums");
  const userForumsRef = snapshot.ref.parent.parent.parent.child("userForums");
  const forumObj = snapshot.val();
  const _forumObj1 = {...forumObj, name: forumObj.users[uid2].name,
                                  image: forumObj.users[uid2].imageThumb,
                                  imageThumb: forumObj.users[uid2].imageThumb,
                                  };
  const _forumObj2 = {...forumObj, name: forumObj.users[uid1].name,
                                  image: forumObj.users[uid1].imageThumb,
                                  imageThumb: forumObj.users[uid1].imageThumb};
  delete _forumObj1["users"];
  delete _forumObj2["users"];
  userForumRef1.child(forumID).update(_forumObj1);
  userForumRef2.child(forumID).update(_forumObj2);
  console.log("Name 1 ", _forumObj1.name, "Name 2 ", _forumObj2.name);
  console.log("Image 1 ", _forumObj1.imageThumb, "Image 2 ", _forumObj2.imageThumb);
  var updatedData = {};
  updatedData[uid1 + "/joinedForums/" + forumID] = {..._forumObj1};
  updatedData[uid2 + "/joinedForums/" + forumID] = {..._forumObj2};
  return userForumsRef.update(updatedData);
});

exports.countNumberOfInAppNotifs = functions.database.ref('/communities/{communityID}/Users1/{uid}/notifications')
.onWrite((change, context) => {
  const totalInAppNotifsRef = change.after.ref.parent.child('notificationStatus/totalNotifications');
  return totalInAppNotifsRef.set(change.after.numChildren());
});

//for new apps
exports.syncUserPointsAndUserPointsNum1 = functions.database.ref('/communities/{communityID}/Users1/{uid}/userPoints')
.onUpdate((change, context) => {
		const user_points_aft = parseInt(change.after.val());
		const user_points_bef = parseInt(change.before.val());
		const userPointsNumRef = change.after.ref.parent.child("userPointsNum");
		return userPointsNumRef.transaction(current_value => {
			// if(current_value)
			// {
				if(current_value <= user_points_bef)
					return user_points_aft;
        else
        {
          console.log("Error in sync");
          return current_value;
        }
			// }
		});
});

//for old apps
exports.syncUserPointsAndUserPointsNum2 = functions.database.ref('/communities/{communityID}/Users1/{uid}/userPointsNum')
.onUpdate((change, context) => {
		const user_points_num_aft = change.after.val();
		const user_points_num_bef = change.before.val();
		const userPointsRef = change.after.ref.parent.child("userPoints");
		return userPointsRef.transaction(current_value => {
			// if(current_value)
			// {
				if(parseInt(current_value) <= user_points_num_bef)
					return String(user_points_num_aft);
        else
        {
          console.lofusg("Error in sync " + parseInt(current_value) + " " + current_value + " " + user_points_num_bef);
          return parseInt(current_value);
        }
			// }
		});
});

exports.addNewShopPoolToHome = functions.database.ref('communities/{communityID}/features/shops/pools/current/{poolID}')
.onCreate((snapshot, context) => {
  let shopPostObj = {};
  const { communityID } = context.params;
  
  return snapshot.ref.parent.once('value', poolSnapshot => {
    let poolObj = poolSnapshot.val();
    let homeRef = poolSnapshot.ref.root.child(`communities/${communityID}/home`).push();
    shopPostObj = {
      'Key': homeRef.key,
      'PostTimeMillis': (new Date()).getTime(),
      'desc': poolObj.poolInfo.description,
      'desc2': "",
      'feature': 'shops',
      'id': homeRef.key,
      'imageurl': poolObj.poolInfo.imageURL,
      'name': poolObj.poolInfo.name,
      'recentType': 'ShopPost',
      'timestampOrderReceivingDeadline':poolObj.timestampOrderReceivingDeadline,
    };
    return homeRef.set(shopPostObj);
  });
});

exports.getPayment = functions.database.ref('communities/{communityID}/features/shops/orders/current/{uid}/{orderID}/paymentGatewayID')
.onCreate((snapshot, context) => {  
	var request = require('request');

  const communityID = context.params.communityID;
  const uid = context.params.uid;
  const orderID = context.params.orderID;

  const paymentGatewayID = snapshot.val();
  snapshot.ref.parent.child('paymentStatus').set("processing");
  return snapshot.ref.parent.once('value', orderSnapshot => {
    let orderObj = orderSnapshot.val();
    const shopID = orderSnapshot.child("poolInfo/shopID").val();
    const poolPushID = orderSnapshot.child("poolPushID").val();
    const discountedAmount = orderSnapshot.child("discountedAmount").val();

    request({
        method: 'POST',
        url: `https://rzp_live_pMQ3fHFcjSv6kP:NNQNMb0W2HGprUf6IkXV4oXG@api.razorpay.com/v1/payments/${paymentGatewayID}/capture`,
        form: {
          amount: parseInt(discountedAmount*1000/10)
        }
      }, (error, response, body) => {
        console.log('Status:', response.statusCode);
        console.log('Headers:', JSON.stringify(response.headers));
        console.log('Response:', body);
        if(response.statusCode === 200)
        {
           console.log("inside status 200");
            snapshot.ref.root.child(`communities/${communityID}/Users1/${uid}`).once('value', userSnapshot => {
              const userObjForForum = {
                imageThumb: userSnapshot.child("imageURLThumbnail").val(),
                name: userSnapshot.child("username").val(),
                phoneNumber: userSnapshot.child("mobileNumber").val(),
                userUID: uid,
              };
              const shopRef = snapshot.ref.root.child(`shops/shopDetails/${shopID}`);
              snapshot.ref.root.child(`communities/${communityID}/features/forums/tabsCategories/shopPools/${poolPushID}/users/${uid}`)
              .set(userObjForForum);
              const timestampPaymentAfter = Date.now();
              const orderStatus = "out for delivery";
              orderObj = {...orderObj,
                orderedBy: {
                  UID: uid,
                  Username: userSnapshot.child("username").val(),
                  ImageThumb: userSnapshot.child("imageURLThumbnail").val(),
                  phoneNumber: orderObj.phoneNumber,
                },
                timestampPaymentAfter,
                orderStatus,
                paymentStatus: "success",
              };
              orderSnapshot.ref.child('phoneNumber').remove();
              const orderRefInsideShop = shopRef.child(`orders/current/${poolPushID}/${orderID}`);
              orderRefInsideShop.set(orderObj);
              const tempObj = {timestampPaymentAfter, orderStatus, paymentStatus: "success"};
              snapshot.ref.parent.update(tempObj);
              shopRef.child(`createdPools/current/${poolPushID}/totalOrder`)
              .transaction(current_value => {
                const userBillID = String(orderID.substr(-6)) + getThreeDigitString(current_value + 1);
                orderRefInsideShop.child("userBillID").set(userBillID);
                //set userBillID in the orderID node of users as well
                snapshot.ref.parent.child("userBillID").set(userBillID);
                snapshot.ref.parent.parent.parent.parent.parent.child(`pools/current/${poolPushID}/totalOrder`).set(current_value + 1);
                return current_value + 1;
              });
            });
          }
        if(response.statusCode === 400)
          {
            const tempObj = {paymentStatus: "fail", timestampPaymentAfter: Date.now()};
            snapshot.ref.parent.update(tempObj);
          }
      });
  });
});

exports.createUpcomingPoolInCommunity = functions.database.ref('shops/shopDetails/{shopID}/createdPools/current/{poolPushID}')
.onCreate((snapshot, context) => {
  return snapshot.ref.parent.parent.parent.child("info/communityID").once('value', (communityIDSnapshot)=>{
      const communityID = communityIDSnapshot.val();
      return snapshot.ref.root.child(`communities/${communityID}/features/shops/pools/current/${context.params.poolPushID}`)
            .set(snapshot.val());
  });
});

exports.deleteActivePoolFromCommunity = functions.database.ref('shops/shopDetails/{shopID}/createdPools/current/{poolPushID}/status')
.onUpdate((change, context) => {
  if(change.after.val() === "paymentRequested")
  {
    const poolPushID = context.params.poolPushID;
    return change.after.ref.parent.once('value', poolSnapshot => {

      return poolSnapshot.ref.parent.parent.parent.child("info/communityID").once('value', communityIDSnapshot => {
        const communityID = communityIDSnapshot.val();
        poolSnapshot.ref.root.child(`communities/${communityID}/features/shops/pools/current/${poolPushID}`)
        .remove();
        poolSnapshot.ref.root.child(`communities/${communityID}/features/shops/pools/archive/${poolPushID}`)
        .set(poolSnapshot.val());
        poolSnapshot.ref.parent.parent.child("previousPools").child(poolPushID).set(poolSnapshot.val());
        poolSnapshot.ref.remove();
        poolSnapshot.ref.root.child(`communities/${communityID}/features/forums/categories/${poolPushID}`)
        .remove();
        poolSnapshot.ref.root.child(`communities/${communityID}/features/forums/tabsCategories/shopPools/${poolPushID}`)
        .remove();
      });
    });
  }
  else 
    return console.log("Nothing to do");
});

exports.changeOrderStatus = functions.database.ref('shops/shopDetails/{shopID}/orders/current/{poolPushID}/{orderID}/orderStatus')
.onUpdate((change, context) => {
  return change.after.ref.parent.parent.parent.parent.parent.child('info/communityID').once('value', communityIDSnapshot => {
    return change.after.ref.parent.child('orderedBy').once('value', orderedBySnapshot => {
      const uid = orderedBySnapshot.child('UID').val();
      const communityID = communityIDSnapshot.val();
      const orderID = context.params.orderID;
      const userOrderRef = change.after.ref.root.child(`communities/${communityID}/features/shops/orders/current/${uid}/${orderID}`);
      userOrderRef.child('orderStatus').set(change.after.val());
      const timestamp = Date.now();
      userOrderRef.child('deliveryRcdTime').set(timestamp);
      change.after.ref.parent.child('deliveryRcdTime').set(timestamp);
    });
  });
});

exports.changePoolStatus = functions.database.ref('communities/{communityID}/features/shops/pools/current/{poolPushID}/status')
.onUpdate((change, context) => {
  return change.after.ref.parent.child('poolInfo/shopID').once('value', shopIDSnapshot => {
    const shopID = shopIDSnapshot.val();
    const poolPushID = context.params.poolPushID;
    const shopPoolRef = change.after.ref.root.child(`shops/shopDetails/${shopID}/createdPools/current/${poolPushID}`);
    shopPoolRef.child('status').set(change.after.val());
  });
});

const getThreeDigitString = (num) => {
  if(num < 10)
    return "00" + num;
  else if(num < 100)
    return "0" + num;
  else
    return String(num);
}

exports.copyOrderReceivingStatusInShopDetail = functions.database.ref('/communities/{communityID}/features/shops/pools/current/{poolPushID}/orderReceivingStatus')
.onUpdate((change, context) => {
  return change.after.ref.parent.child('poolInfo/shopID').once('value', shopIDSnapshot => {
    const shopID = shopIDSnapshot.val();
    return change.after.ref.root.child(`shops/shopDetails/${shopID}/createdPools/current/${context.params.poolPushID}/orderReceivingStatus`)
    .set(change.after.val());
  });
});

exports.copyOrderReceivingStatusInShopFeature = functions.database.ref('shops/shopDetails/{shopID}/createdPools/current/{poolPushID}/orderReceivingStatus')
.onUpdate((change, context) => {
  return change.after.ref.parent.parent.parent.parent.child('info/communityID').once('value', communityIDSnapshot => {
    const communityID = communityIDSnapshot.val();
    return change.after.ref.root.child(`communities/${communityID}/features/shops/pools/current/${context.params.poolPushID}/orderReceivingStatus`)
    .set(change.after.val());
  });
});

exports.addToUserCommunities = functions.database.ref('communities/{communityID}/Users1/{uid}/userType')
.onUpdate((change, context) => {
  const uid = context.params.uid;
  const communityID = context.params.communityID;
  const userType = change.after.val();
  return change.after.ref.root.child("userCommunities").child(uid).child("communitiesJoined").once('value', snapshot => {
    if(userType === "verified" || userType === "admin")
    {
      if(snapshot.val() === null || !snapshot.hasChild(communityID))
      {
        return snapshot.ref.child(communityID).set(communityID) && snapshot.ref.parent.child("totalCommunitiesJoined").set(1);
      }
      else
      {
        return console.log("User uid - ", uid, " already has community ID - ", communityID, " in their communititesJoined list");
      } 
    }
    else if(snapshot.hasChild(communityID))
    {
      if(snapshot.numChildren()===1)
      {
        return snapshot.ref.parent.remove();
      }
      else
      {
        return snapshot.ref.child(communityID).remove();
      }
    }
    else
    {
      return console.log("User UID - ", uid, " is not a verfied member of the community ID - ", communityID);
    }
  });
});

exports.countCommunitiesJoined = functions.database.ref('userCommunities/{uid}/communitiesJoined')
.onWrite((change, context) => {
  const countRef = change.after.ref.parent.child("totalCommunitiesJoined");
  if(change.after.val()===null)
    return countRef.remove();
  else
    return countRef.set(change.after.numChildren());
});

exports.deleteForumInAppNotif = functions.database.ref('communities/{communityID}/features/forums/categories/{forumID}')
.onDelete((snapshot, context) => {
  let { forumID, communityID } = context.params;
  const forumInAppNotifsRef = snapshot.ref.parent.parent.child("inAppNotifications").child(forumID);
  const personal_inAppNotifsRef = snapshot.ref.root.child(`communities/${communityID}/Users1`);
  return forumInAppNotifsRef.once('value', notificationsSnapshot => {
    return notificationsSnapshot.forEach(notif => {
      if(notif.hasChild('receiverUID'))
        personal_inAppNotifsRef.child(notif.child('receiverUID').val()).child("notifications").child(notif.key).remove();
      notif.ref.remove();
    });
  });
});

exports.deleteStoreroomInAppNotif = functions.database.ref('communities/{communityID}/features/storeroom/products/{productID}')
.onDelete((snapshot, context) => {
  let { productID, communityID } = context.params;
  const productInAppNotifsRef = snapshot.ref.parent.parent.child('inAppNotifications').child(productID);
  const personal_inAppNotifsRef = snapshot.ref.root.child(`communities/${communityID}/Users1`);
  return productInAppNotifsRef.once('value', notificationsSnapshot => {
    return notificationsSnapshot.forEach(notif => {
      if(notif.hasChild('receiverUID'))
        personal_inAppNotifsRef.child(notif.child('receiverUID').val()).child("notifications").child(notif.key).remove();
      notif.ref.remove();
    });
  });
});

exports.deleteEventsInAppNotif = functions.database.ref('communities/{communityID}/features/events/activeEvents/{eventID}')
.onDelete((snapshot, context) => {
  let { eventID, communityID } = context.params;
  const eventInAppNotifsRef = snapshot.ref.parent.parent.child('inAppNotifications').child(eventID);
  const personal_inAppNotifsRef = snapshot.ref.root.child(`communities/${communityID}/Users1`);
  return eventInAppNotifsRef.once('value', notificationsSnapshot => {
    return notificationsSnapshot.forEach(notif => {
      if(notif.hasChild('receiverUID'))
        personal_inAppNotifsRef.child(notif.child('receiverUID').val()).child("notifications").child(notif.key).remove();
      notif.ref.remove();
    });
  });
});

exports.deleteStatusesInAppNotif = functions.database.ref('communities/{communityID}/home/{statusID}')
.onDelete((snanpshot, context) => {
  if(snanpshot.child('feature').val()!=="Message")
    return console.log("Deleted post is not a status.");
  let { statusID, communityID } = context.params;
  const statusesInAppNotifsRef = snanpshot.ref.parent.parent.child(`features/statuses/inAppNotifications/${statusID}`);
  const personal_inAppNotifsRef = snanpshot.ref.root.child(`communities/${communityID}/Users1`);
  return statusesInAppNotifsRef.once('value', notificationsSnapshot => {
    return notificationsSnapshot.forEach(notif => {
      if(notif.hasChild('receiverUID'))
        personal_inAppNotifsRef.child(notif.child('receiverUID').val()).child('notifications').child(notif.key).remove();
      notif.ref.remove();
    });
  });
});

exports.syncCabChatsWithCabForums = functions.database.ref('communities/{communityID}/features/cabPool/allCabs/{cabID}/Chat/{messageID}/')
.onCreate((snapshot, context) => {
  let { tabID, forumID } = context.params;
  console.log("1 ", tabID, forumID, snapshot.val());
  if(tabID === null || forumID === null)
  {
    console.warn(`Invalid params, expected 'tabID' and 'forumID'`, context.params);
    tabID = snapshot.ref.parent.id;
    forumID = snapshot.ref.id;
    console.log("2", tabID, forumID, snapshot.val());
  }
  if(tabID!=="personalChats")
    return console.log("Not a personal chat.");
  if(Object.keys(snapshot.child("users").val()).length!==2)
    return console.log("Both users did not get added in the personal chat at once");
  const uid1 = Object.keys(snapshot.child("users").val())[0];
  const uid2 = Object.keys(snapshot.child("users").val())[1];
  console.log("User 1 ", uid1, "User 2 ", uid2);
  const userForumRef1 = snapshot.ref.parent.parent.parent.child("userForums").child(uid1).child("joinedForums");
  const userForumRef2 = snapshot.ref.parent.parent.parent.child("userForums").child(uid2).child("joinedForums");
  const forumObj = snapshot.val();
  const _forumObj1 = {...forumObj, name: forumObj.users[uid2].name,
                                  image: forumObj.users[uid2].imageThumb,
                                  imageThumb: forumObj.users[uid2].imageThumb};
  const _forumObj2 = {...forumObj, name: forumObj.users[uid1].name,
                                  image: forumObj.users[uid1].imageThumb,
                                  imageThumb: forumObj.users[uid1].imageThumb};
  console.log("Name 1 ", _forumObj1.name, "Name 2 ", _forumObj2.name);
  console.log("Image 1 ", _forumObj1.imageThumb, "Image 2 ", _forumObj2.imageThumb);  
  delete _forumObj1["users"];
  delete _forumObj2["users"];
  console.log("name 1 ", _forumObj1.name, "name 2 ", _forumObj2.name);
  return userForumRef1.child(forumID).set(_forumObj1)
  && userForumRef2.child(forumID).set(_forumObj2);
});

exports.syncCabForumsWithCabChats = functions.database.ref('communities/{communityID}/features/forums/categories/{cabID}/Chat/{messageID}/')
.onWrite((change, context) => {
  const { communityID, cabID, messageID } = context.params;
  return change.after.ref.parent.parent.child('tab').once('value', tabSnapShot => {
    if(tabSnapShot.val()!=="cabpools")
      return;
      const cabChatRef = change.before.ref.root.child(`communities/${communityID}/features/cabPool/allCabs/${cabID}/Chat`);
      // eslint-disable-next-line consistent-return
      return cabChatRef.once('value', chatSnapshot => {
        if(chatSnapshot.hasChild(messageID))
        {
          return console.log("Already synced this message: ", change.after.child('message').val());
        }
        return cabChatRef.child(messageID).set(change.after.val());
>>>>>>> anshuman-test
    });
  });
});

exports.addUserInternships = functions.database.ref('communities/{communityID}/features/internships/opportunities/{internshipID}/users/{uid}')
.onCreate((snapshot, context) => {
  const { internshipID, uid } = context.params;
  return snapshot.ref.parent.parent.once('value', internshipSnapshot => {
    const internshipObj = internshipSnapshot.val();
    delete internshipObj["users"];
    const addUserInternshipsRef = snapshot.ref.parent.parent.parent.parent.child('usersInternships').child('appliedInternships').child(uid);
    return addUserInternshipsRef.child(internshipID).set(internshipObj);
    });
});

exports.createCommunity = functions.database.ref('createCommunity/{communityID}')
.onCreate(async (snanpshot, context) => {
  if(!snanpshot.child('create').val())
    return;
  const { communityID } = context.params;
  const newCommunityRef = snanpshot.ref.root.child(`communities/${communityID}`);
  await snanpshot.ref.root.child('communities/templateNew').once('value', templateSnapshot => {
    newCommunityRef.set(templateSnapshot.val());
  });
  const newCommunityInfoRef = snanpshot.ref.root.child(`communitiesInfo/${communityID}`);
  await snanpshot.ref.root.child('communitiesInfo/templateNew').once('value', templateSnapshot => {
    newCommunityInfoRef.set(templateSnapshot.val());
>>>>>>> anshuman-test
  });
});