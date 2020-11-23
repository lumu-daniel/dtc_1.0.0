/*
  Copyright 2016 Google Inc. All Rights Reserved.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.mlt.dtc.pushnotification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.DriverImageListener;
import com.mlt.dtc.interfaces.FareDialogListener;
import com.mlt.dtc.interfaces.TripStartedForPaymentListener;
import com.mlt.dtc.utility.Constant;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mlt.dtc.common.Common.WriteTextInTextFileForShift;
import static com.mlt.dtc.common.Common.WriteTextInTextFileForTrip;
import static com.mlt.dtc.common.Common.getFilePath;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Map<String, String> arrayMap;
    String TripID, TripStatus, GivenName, FamilyName, ShiftID, ShiftStatus, DriverID, EventCode;
    MainActivity mainActivity = new MainActivity();
    static private FareDialogListener fareDialogListenerCallback;
    static private DriverImageListener driverImageListener;
    static private TripStartedForPaymentListener tripStartedForPaymentListener;
    String ShiftEventDateTime;
    String ClassName, tripCode;
    long startTime, endTime;
    LinkedHashMap<String, String> hashMap;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        if (processresponse(remoteMessage)) return;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private boolean processresponse(RemoteMessage remoteMessage) {
        remoteMessage.getData().keySet();
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "CollapseKey: " + remoteMessage.getCollapseKey());
        Log.e(TAG, "Data: " + remoteMessage.getData().get("gcm.notification.title"));
        Log.e(TAG, "MessageId: " + remoteMessage.getMessageId());
        Log.e(TAG, "MessageType: " + remoteMessage.getMessageType());
        Log.e(TAG, "Notification(): " + remoteMessage.getNotification());
        Log.e(TAG, "To: " + remoteMessage.getTo());
        Log.e(TAG, "OriginalPriority(): " + remoteMessage.getOriginalPriority());
        Log.e(TAG, "SentTime: " + remoteMessage.getSentTime());
        Log.e(TAG, "Priority: " + remoteMessage.getPriority());
        Log.e(TAG, "Ttl: " + remoteMessage.getTtl());

        //get Class Name
        ClassName = getClass().getCanonicalName();

        arrayMap = remoteMessage.getData();
        //LinkedHashMap<String, String> hashMap =
        hashMap = (arrayMap instanceof HashMap)
                       ? (LinkedHashMap) arrayMap
                       : new LinkedHashMap<>(arrayMap);

        Log.d(TAG, "hashMap: "+hashMap);

        try {
            if (hashMap.get(Constant.ErrorMessage) != null) {
                try {
                    Common.writeObject(getApplicationContext(), Constant.PushDetailsHashMap, hashMap);
                } catch (IOException e) {

                }
                return true;
            }
            if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripStartEventCode)
                    || hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripEndEventCode)) {
                //Getting values
                GivenName = hashMap.get(Constant.TSGivenNameKey);
                FamilyName = hashMap.get(Constant.TSFamilyNameKey);

                //To save it in the text file for the log
                TripID = hashMap.get(Constant.TSTripIdKey);

                Log.d(TAG, "TripID: "+TripID);

                DriverID = hashMap.get(Constant.TSDriverIdKey);
                EventCode = hashMap.get(Constant.TSEventCodeKey);
                TripStatus = hashMap.get(Constant.TSEventDescriptionKey);
                if (TripStatus.equals(Constant.TSEventDescriptionValue))
                    TripStatus = Constant.TSEventDescriptionValue;
                else if (TripStatus.equals(Constant.TEEventDescriptionValue)) {
                    TripStatus = Constant.TEEventDescriptionValue;
                }
                //Write data in text file
                WriteTextInTextFileForTrip(getFilePath(), TripID, TripStatus, GivenName, FamilyName);

                //Call back function to open the dialog for trip end when the notification comes
                try {
                    Common.writeObject(getApplicationContext(), Constant.PushDetailsHashMap, hashMap);
                } catch (IOException e) {

                }

                //Check trip code to open fragments to show trip start and end details
                tripCode = EventCode;
                //Call Back function call when trip notification comes
                try {
                    if (fareDialogListenerCallback != null) {
                        Long logTime = new Date().getTime();
                        // Saved the time when the notification has been received.
                        PreferenceConnector.writeLong(getApplicationContext(), Constant.TripTimeKey, logTime);

                        int notificationStatus = PreferenceConnector.readInteger(getApplicationContext(), Constant.TripNotificationStatus, 0);

                        // Set the notification status to 1 to acknowledge receipt of notification
                        PreferenceConnector.writeInteger(getApplicationContext(), Constant.TripNotificationStatus, 1);
                        // Set the notification status to 1 to acknowledge receipt of notification
                        if( notificationStatus == 0 ) {
                            compareTime();
                        }
                    }
                }catch (Exception ex){
                    Log.d(TAG, ex.getMessage()+". ");
                }

                //Trip Starts and set the value to true
                if (tripStartedForPaymentListener != null) {
                    tripStartedForPaymentListener.TripStartedForPaymentCallBackMethod(true);
                }
            }
            else {
                //Getting values
                GivenName = hashMap.get(Constant.TSGivenNameKey);
                FamilyName = hashMap.get(Constant.TSFamilyNameKey);
                ShiftEventDateTime = hashMap.get(Constant.SSEventDateTimeKey);

                //To save it in the text file for the log
                ShiftID = hashMap.get(Constant.SSShiftSequenceKey);
                ShiftStatus = hashMap.get(Constant.SSEventDescriptionKey);
                if (ShiftStatus.equals(Constant.SSEventDescriptionValue))
                    ShiftStatus = Constant.SSEventDescriptionValue;
                else if (ShiftStatus.equals(Constant.SEEventDescriptionValue))
                    ShiftStatus = Constant.SEEventDescriptionValue;
                PreferenceConnector.writeString(getApplicationContext(), Constant.SSEventDateTimeKey, ShiftEventDateTime);
                //Write data in text file
                WriteTextInTextFileForShift(getFilePath(), ShiftID, ShiftStatus, GivenName, FamilyName);
                //Call Back function call when trip notification comes
                if (driverImageListener != null) {
                    driverImageListener.DriverImageCallBackMethod(hashMap.get(Constant.SSPictureKey), getApplicationContext());
                }
                PreferenceConnector.writeString(getApplicationContext(), Constant.DriverImage, hashMap.get(Constant.SSPictureKey));
            }

            try {
                Common.writeObject(getApplicationContext(), Constant.PushDetailsHashMap, hashMap);
            } catch (IOException e) {

            }
        } catch (Exception e) {

        }
        return false;
    }
    // [END receive_message]

/*
* Compare time between since the last notification was received and now.
* if the time is greater than 5 seconds, call the pop up dialog.*/
    private void compareTime(){
        Runnable runnable = () -> {
            //Get the current time for comparison with the saved time
            Long logTime = new Date().getTime();
            while(true) {
                // Get the saved time when the last notification and compare with the current time.
                Long loggedTime = PreferenceConnector.readLong(getApplicationContext(), Constant.TripTimeKey, logTime);
                Long timeDifference = logTime - loggedTime;
                // if it is greater than 5 seconds call the pop up.
                if( timeDifference > 4500 ) {
                    //reset the notification status to 0(default)
                    PreferenceConnector.writeInteger(getApplicationContext(), Constant.TripNotificationStatus, 0);

                    //Pop up the dialog.
                    fareDialogListenerCallback.FareCallBackMethod(hashMap.get(Constant.EventCodeLog), true, tripCode);
                    break;
                }
                logTime = new Date().getTime();
            }
        };
        //Start the thread.
        new Thread(runnable).start();
    }


    //Set Call back method to get the values in CardPaymentSwipe activity
    public static void setcardValuesCallBackMethod(FareDialogListener CallBack) {
        fareDialogListenerCallback = CallBack;
    }

    public static void setDriverImageCallBackMethod(DriverImageListener CallBack) {
        driverImageListener = CallBack;
    }

    public static void setTripStartedForPaymentCallBackMethod(TripStartedForPaymentListener CallBack) {
        tripStartedForPaymentListener = CallBack;
    }

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(null)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
