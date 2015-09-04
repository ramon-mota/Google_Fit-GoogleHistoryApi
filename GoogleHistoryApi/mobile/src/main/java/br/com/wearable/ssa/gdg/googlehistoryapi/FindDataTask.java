package br.com.wearable.ssa.gdg.googlehistoryapi;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ramon on 22/08/15.
 */
public class FindDataTask extends AsyncTask<Object,Void,List>{

    private List<String> mHeartRateList;

    private Application mApplication;

    @Override
    protected List doInBackground(Object... params) {
       
         mApplication = (Application)params[0];

        GoogleApiClient googleApiClient = (GoogleApiClient)params[1];

        DataReadRequest dataReadRequest = createReadRequestInstance();

        // Obtémm os dados lidos de acordo com client e o resultSet por minuto
        DataReadResult dataReadResult = Fitness.HistoryApi.readData(googleApiClient, dataReadRequest).await(1, TimeUnit.MINUTES);

        mHeartRateList = new ArrayList<>(0);

        extracttData(dataReadResult);

        return mHeartRateList;
    }

    @Override
    protected void onPostExecute(List list) {
        super.onPostExecute(list);

        NotificationUtils.createNotificationBig(mApplication, mHeartRateList);
    }

    /**
     * Método cria instâncância de um readRequest
     */
    public DataReadRequest createReadRequestInstance(){

        Calendar calendar = Calendar.getInstance();

        Date date = new Date();

        // Obtém a data Atual
        calendar.setTime(date);

        // Obtém a data atual em milisegundos
        long endTime = calendar.getTimeInMillis();

        // Insere uma data com o dia anterior ao atual
        calendar.add(Calendar.DAY_OF_WEEK, -1);

        // Obtém a data anterior em milisegundos
        long startTime  = calendar.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);

        Log.i(Constants.TAG, "Range Start: " + dateFormat.format(startTime));

        Log.i(Constants.TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = null;
        try{
            // Instancia o DataRequest
            readRequest = new DataReadRequest.Builder()

                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .read(DataType.TYPE_HEART_RATE_BPM)
                    .build();

        }catch (Exception e){
            Log.e(Constants.TAG, "Problem in read: " + e);
        }


        return readRequest;

    }

    /**
     * Método responsável por extratir cada DataSet de suas listas.Provenientes de
     *  Backets (baldes) ou não.
     *
     * @param dataReadResult
     */
    public void extracttData(DataReadResult dataReadResult){

        // Verifica se foi retornado algum registro/baldes com os DataSets ou apenas DataSets
        if(dataReadResult.getBuckets().size() > 0 ){
            Log.i(Constants.TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());

            for(Bucket bucket :dataReadResult.getBuckets()){
                List<DataSet> dataSets = bucket.getDataSets();

                for(DataSet dataSet: dataSets){
                    fillListFromDataSet(dataSet);
                }
            }

        }else if(dataReadResult.getDataSets().size()>0){
            Log.i(Constants.TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                fillListFromDataSet(dataSet);
            }
        }

    }

    /**
     * Preenche lista a partir de um DataSet
     *
     * @param dataSet
     */
    public void fillListFromDataSet(DataSet dataSet){

        Log.i(Constants.TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);


        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(Constants.TAG, "Data point:");

            Log.i(Constants.TAG, "\tType: " + dp.getDataType().getName());

            Log.i(Constants.TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

            Log.i(Constants.TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

                for(Field field : dp.getDataType().getFields()) {
                    Log.i(Constants.TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field) + " " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)));

                    mHeartRateList.add(dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) +
                                      "\n "+ dp.getValue(field) + " " + field.getName() );

                }
        }

    }


}
