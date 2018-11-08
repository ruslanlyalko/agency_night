const functions = require('firebase-functions');
const moment = require('moment-timezone');
const admin = require('firebase-admin');
//const nodemailer = require('nodemailer');

admin.initializeApp(functions.config().firebase);

var fireStoreDB;

function getDb(){	
	if (!fireStoreDB){
		const settings = {timestampsInSnapshots: true};
		fireStoreDB = admin.firestore();
		fireStoreDB.settings(settings);
	}		
	return fireStoreDB;
}

function sendPushToUsers(payload, uIds){
	return getDb().collection('USERS')		
		.get().then(snapshot => {
			var tokens = [];		
			var currentHour = moment().tz('Europe/Kiev').format("HH");
			snapshot.forEach(doc => {
				var userObj = doc.data();
				console.log(userObj.notificationHour, currentHour);
				if(userObj.token  && uIds.includes(userObj.key)){	
					if(userObj.notificationHour === currentHour){
						console.log("Token Added for: " + userObj.name +", userId: ", userObj.key);									
						tokens.push(userObj.token);
					} else {
						console.log("Token Skipped for: " + userObj.name +", userId: ", userObj.key);									
					}
				}
			})
			return sendMessagesViaFCM(tokens, payload);
		});
}

function sendMessagesViaFCM(tokens, payload){
	if(tokens.length > 0)
		return  admin.messaging().sendToDevice(tokens, payload)
			.then(response => {
				console.log("Push Sent: ", response);
				return 0;
			})
			.catch(error => {
				console.log("Push Error: ", error);
			});			
	else 
		return console.log("Push No Tokens");
}

exports.balanceRecalculate = functions.database.ref("DATA/{userId}/ORDERS/{orderId}")
.onWrite((change, context) => {
	return console.log("yes");
});


exports.reminder = functions.https.onRequest((request, response) => {   
	const key = request.query.key;
	//firebase functions:config:set cron.key="somecoolkey"
	// Exit if the keys don't match.
	if (key !== functions.config().cron.key) {
		console.log('The key provided in the request does not match the key set in the environment. Check that', key,
			'matches the cron.key attribute in `firebase env:get`');
			response.status(403).send('Security key does not match. Make sure your "key" URL query parameter matches the ' +
			'cron.key environment variable.');
		return null;
	}	
	
	
	const timeStart = moment().add(1, "days").startOf('day');
	const timeEnd = moment().add(1, "days").endOf('day');

	console.log("Start", timeStart.format()," End: ", timeEnd.format());	
	
	return getDb().collection('ORDERS')
		.where("date" , ">=" , timeStart.toDate())
		.where("date" , "<=" , timeEnd.toDate())
		.get()
			.then(snapshot => {
				var usersIds = [];
				snapshot.forEach(doc => {
					console.log(doc.id, '=>', doc.data(), doc.data().userId);
					usersIds.push(doc.data().userId);
				});
				var payload = {
					data:{
						title: "You have Order tomorrow!",
						body: "Please have a look",
						type: "reminder"
					}
				};
				sendPushToUsers(payload, usersIds);
				return response.send("Length: "+ snapshot.size);
			})
			.catch(err => {
				console.log('Error getting documents', err);
				return response.send(err);
			});
});