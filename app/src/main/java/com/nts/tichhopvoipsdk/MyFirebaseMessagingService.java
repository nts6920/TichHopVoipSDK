package com.nts.tichhopvoipsdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.g99.linphone.activities.voip.CallActivity;
import com.g99.voip.CallbackValue;
import com.g99.voip.SDKManager;
import com.g99.voip.UserInfo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;
import org.linphone.core.Account;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TYPE_ORDER_NUMBER = "ORDER_NUMBER";
    public static final String VOIPCALL = "VOIPCALL";
    private NotificationManager mNotificationManager;

    String mobile = "0973385525";
    int merchantId = 1;
    String customer = "Nguyen Duc Vinh";
    String identification = "033080007111";
    String uID = "999999999";
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private Runnable mPushReceivedRunnable = () -> {
        if (SDKManager.Companion.getInstance() == null) {
            UserInfo userInfo = new UserInfo();
            userInfo.setMobile(mobile);
            userInfo.setMerchant_id(merchantId);
            userInfo.setCustomer(customer);
            userInfo.setIdentification_no(identification);
            userInfo.setUid(uID);

            SDKManager.Companion.init(getApplication()).setUserInfo(userInfo).setCallbackListener((callbackValue) -> {
                if (callbackValue == CallbackValue.REQUIRED_LOGIN) {
                    Toast.makeText(this, "replaceLoginActivity", Toast.LENGTH_SHORT).show();
                }
            }).start();
        }
    };


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());
            String typePush = remoteMessage.getData().get("type");
            Log.d("TAG", "typePush..." + typePush);
            sHandler.post(mPushReceivedRunnable);

            if (typePush.equals(TYPE_ORDER_NUMBER)) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                sendNotification(json);
                Log.d("TAG", "number_success: ");
                Intent intent = new Intent("number_success");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            } else if (!typePush.equals(VOIPCALL)) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                sendNotification(json);
            } else {
                sHandler.post(mPushReceivedRunnable);
                JSONObject json = new JSONObject(remoteMessage.getData());
                sendNotification(json);
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    public void sendNotification(JSONObject content) {
        if (content == null)
            return;
        String message;
        Intent intent;
        String typePush;

        try {
            typePush = content.getString("type");

            if (typePush == null)
                return;

            if (typePush.equals(TYPE_ORDER_NUMBER)) {
                message = content.getString("message");
                intent = new Intent(getApplicationContext(), MainActivity.class);

            } else if (typePush.equals(VOIPCALL)) {
                message = "Cuộc gọi đến: " + content.getString("display-name");
                intent = new Intent(getApplicationContext(), CallActivity.class);
                Account defaultAcc = SDKManager.coreContext.getCore().getDefaultAccount();
                SDKManager.Companion.getInstance().isRegisterEnabled(Objects.requireNonNull(defaultAcc));

            } else {
                message = content.getString("message");
                intent = new Intent(this, MainActivity.class);
            }


            int requestID = (int) System.currentTimeMillis();
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            String CHANNEL_ID = "PAYPO_01";// The id of the channel.
            CharSequence name = "PAYPO_NOTIFY";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            }


            //Notify user
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setChannelId(CHANNEL_ID);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setVibrate(new long[]{100, 300, 100, 300, 100, 300});

            Notification notification = mBuilder.build();
            notification.ledARGB = Color.BLUE;
            notification.ledOnMS = 700;
            notification.ledOffMS = 300;
            notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
            mNotificationManager.notify(1, notification);

        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }

}
