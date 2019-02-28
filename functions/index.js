
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
  .once('value').then(function(data) {
    var referred_by_uid = data.val();
    if (referred_by_uid) {
      var points = admin.database().ref(`/communities/${communityID}/Users1/${referred_by_uid}/userPoints/`);
      points.transaction(function (current_value) {
        return String((parseInt(current_value) || 0) + 50);
      });

      var points2 = admin.database().ref(`/communities/${communityID}/Users1/${uid}/userPoints/`);
      points2.transaction(function (current_value) {
        return String((parseInt(current_value) || 0) + 50);
      });
    }

     return console.log('Referal Added');
  });


});

exports.countInfoneMembers = functions.database.ref('/communities/{communityID}/infone/numbers/{contactID}/category')
.onCreate(
	(snap, context) => {
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

exports.countTotalMessages = functions.database.ref('/communities/{communityID}/features/forums/categories/{catID}')
.onWrite((change, context)  => {
  
  const communityID = context.params.communityID;
  const catID = context.params.catID;
  const tabID = change.after.child("tab").val();
  const countRef = change.after.ref.parent.parent.child(`tabsCategories/${tabID}/${catID}/totalMessages`);
  const count = change.after.child("Chat").numChildren();
  
  return countRef.set(count);

  // return  admin.database().ref('/communities/'+ event.params.communityID +'/features/forums/tabsCategories/'+ event.data.child("tab").val()+'/' + event.params.catID+'/totalMessages').set(event.data.child("Chat").numChildren());
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
	return totalMembersRef.set(change.after.numChildren());
});

//for new apps
exports.syncUserPointsAndUserPointsNum1 = functions.database.ref('/communities/{communityID}/Users1/{uid}/userPoints')
.onUpdate((change, context) => {
	if(context.params.communityID === "testCollege")
	{
		const user_points_aft = parseInt(change.after.val());
		const user_points_bef = parseInt(change.before.val());
		const userPointsNumRef = change.after.ref.parent.child("userPointsNum");
		return userPointsNumRef.transaction(current_value => {
			// if(current_value)
			// {
				if(current_value <= user_points_bef)
					return user_points_aft;
				else
					console.log("Error in sync");
			// }
		});
	}
});

//for old apps
exports.syncUserPointsAndUserPointsNum2 = functions.database.ref('/communities/{communityID}/Users1/{uid}/userPointsNum')
.onUpdate((change, context) => {
	if(context.params.communityID === "testCollege")
	{
		const user_points_num_aft = change.after.val();
		const user_points_num_bef = change.before.val();
		const userPointsRef = change.after.ref.parent.child("userPoints");
		return userPointsRef.transaction(current_value => {
			// if(current_value)
			// {
				if(parseInt(current_value) <= user_points_num_bef)
					return String(user_points_num_aft);
				else
					console.log("Error in sync " + parseInt(current_value) + " " + current_value + " " + user_points_num_bef);
			// }
		});
	}
});

exports.test_getPayment = functions.database.ref('communities/{communityID}/features/shops/orders/current/{uid}/{orderID}/paymentGatewayID')
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
        url: `https://rzp_test_A8XTiRdzpUWJcH:avDG7H44K0ChQCSFqMSwcGTg@api.razorpay.com/v1/payments/${paymentGatewayID}/capture`,
        form: {
          amount: discountedAmount*100
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
        url: `https://rzp_test_A8XTiRdzpUWJcH:avDG7H44K0ChQCSFqMSwcGTg@api.razorpay.com/v1/payments/${paymentGatewayID}/capture`,
        form: {
          amount: discountedAmount*100
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
        poolSnapshot.ref.root.child(`communities/${communityIDSnapshot.val()}/features/shops/pools/current/${poolPushID}`)
          .remove();
        poolSnapshot.ref.root.child(`communities/${communityIDSnapshot.val()}/features/shops/pools/archive/${poolPushID}`)
          .set(poolSnapshot.val());
        poolSnapshot.ref.parent.parent.child("previousPools").child(poolPushID).set(poolSnapshot.val());
        return poolSnapshot.ref.remove();
      });
    });
  }
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