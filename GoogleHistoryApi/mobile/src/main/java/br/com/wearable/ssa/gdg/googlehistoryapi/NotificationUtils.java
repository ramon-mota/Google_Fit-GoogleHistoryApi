package br.com.wearable.ssa.gdg.googlehistoryapi;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by ramon on 22/08/15.
 */
public class NotificationUtils {

    /**
     * Cria uma simples notificação notificação
     *
     *  @param context
     */
    public static void createNotificationSimple(Context context ){

        Log.i(Constants.TAG, "Send Notificaion Heart Rate bpm");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.common_signin_btn_icon_light)
                .setContentTitle("Google Fit ")
                .setContentText("Batimento cardiáco inserido com sucesso.");


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(Constants.NOTIFICATION_SIMPLE_ID, mBuilder.build());
    }

    /**
     * Cria uma notificação com um número maior de registros
     *
     * @param context
     * @param mHeartHateList
     */
    public static void createNotificationBig(Context context,List<String> mHeartHateList) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (String record : mHeartHateList) {
            inboxStyle.addLine(record);
        }

        NotificationCompat.Builder mBuider = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                .setContentTitle("Frequência cardíaca das ultimas 24h")
                .setContentText("")
                .setStyle(inboxStyle);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Constants.NOTIFICATION_BIG_ID,mBuider.build());

    }



}
