package br.com.wearable.ssa.gdg.googlehistoryapi;

import com.google.android.gms.fitness.data.DataType;

/**
 * Created by ramon on 22/08/15.
 */
public class Constants {

    // Tipos de dados
    public static final DataType TYPE_LOCATION_SAMPLE = DataType.TYPE_LOCATION_SAMPLE;
    public static final DataType TYPE_HEART_RATE_BPM  = DataType.TYPE_HEART_RATE_BPM;

    // Identificadores das notificações
    public static final int NOTIFICATION_SIMPLE_ID = 1;
    public static final int NOTIFICATION_BIG_ID = 2;

    //
    public static final int REQUEST_OAUTH = 1;
    public static final String AUTH_PENDING = "auth_state_pending";

    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    // Constante de Log
    public static final String TAG = "BasicSensorsApi";
}
