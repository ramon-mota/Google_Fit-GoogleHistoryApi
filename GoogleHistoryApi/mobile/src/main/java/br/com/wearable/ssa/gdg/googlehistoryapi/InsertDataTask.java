package br.com.wearable.ssa.gdg.googlehistoryapi;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;



/**
 * Created by ramon on 22/08/15.
 */
public class InsertDataTask extends AsyncTask<Object,Void,Boolean>{

    private Application mApplication;

    @Override
    protected Boolean doInBackground(Object... params) {

        mApplication  = (Application)params[0];

        GoogleApiClient googleApiClient = (GoogleApiClient)params[1];

        float heartHate =  (float)params[2];

        DataSet dataSet = createDataSetInstance(mApplication, heartHate);

        Log.i(Constants.TAG, "Inserting the dataset in the History API");

        com.google.android.gms.common.api.Status insertStatus =  Fitness.HistoryApi.insertData(googleApiClient, dataSet).await(1, TimeUnit.MINUTES);

        if(!insertStatus.isSuccess()){
            Log.i(Constants.TAG, "There was a problem inserting the dataset.");
            return false;
        }else{

            // At this point, the data has been inserted and can be read.
            Log.i(Constants.TAG, "Data insert was successful!");
            return true;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        NotificationUtils.createNotificationSimple(mApplication);
    }

    /***
     * Método responsável pela inserção de dados no google fit
     */
    public DataSet createDataSetInstance(Application application, float heartHate){

        Calendar calendar = Calendar.getInstance();

        Date date = new Date();

        // Obtém a data Atual
        calendar.setTime(date);

        // Obtém a data atual em milisegundos
        long endTime = calendar.getTimeInMillis();

        // Insere uma data com a hora anterior ao atual
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        // Obtém a data anterior em milisegundos
        long startTime  = calendar.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);

        Log.i(Constants.TAG, "Range Start: " + dateFormat.format(startTime));

        Log.i(Constants.TAG, "Range End: " + dateFormat.format(endTime));


        //Criando Data Sorce
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(application)
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setName(Constants.TAG + " heart rate")
                .setType(DataSource.TYPE_RAW)
                .build();


        DataSet dataSet = null;

        try{

            //Criando DataSet
            dataSet = DataSet.create(dataSource);

            //Criando DataPoint
            DataPoint dataPoint = dataSet.createDataPoint()
                    // Intervalo de tempo em milisegundos
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
            // Preenchendo o DataPoint com o tipo (BPM - Batimentos Cardiácos por Minuto) e o valor informado.
            dataPoint.getValue(Field.FIELD_BPM).setFloat(heartHate);
            // Inserindo o ponto criado no DataSet
            dataSet.add(dataPoint);


        }catch (Exception e){
            Log.e(Constants.TAG, "Problem in create DataSet: " + e);
        }
        return dataSet;
    }



}
