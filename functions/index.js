
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
        return (parseInt(current_value) || 0) + 50;
      });

      var points2 = admin.database().ref(`/communities/${communityID}/Users1/${uid}/userPoints/`);
      points2.transaction(function (current_value) {
        return (parseInt(current_value) || 0) + 50;
      });
    }

     return console.log('Referal Added');
  });


});

exports.countInfoneMembers = functions.database.ref('/communities/{communityID}/infone/numbers/{contactID}/category').onCreate(
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