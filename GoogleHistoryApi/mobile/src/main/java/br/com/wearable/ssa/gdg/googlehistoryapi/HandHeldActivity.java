package br.com.wearable.ssa.gdg.googlehistoryapi;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;


public class HandHeldActivity extends Activity implements View.OnClickListener {

    private boolean authInProgress;

    private int buttonAction;

    private GoogleApiClient mClient;

    private EditText editText;

    private float heartHate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_held);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(Constants.AUTH_PENDING);
        }

        // Obtém  a instância do componete de inserção do leiaute
         editText = (EditText) findViewById(R.id.editText);

        // Obtém  a instância do botão isert do leiaute
         Button buttonInsert = (Button) findViewById(R.id.button_insert);
        // Prepara a ação do botão quando clicado
         buttonInsert.setOnClickListener(this);

        // Obtém  a instância do botão get do leiaute
         Button buttonGet = (Button) findViewById(R.id.button_get);
        // Prepara a ação do botão quando clicado
         buttonGet.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            Log.i(Constants.TAG,"disconect");
            mClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Conectando o client do Google Fit
        if(mClient != null){

            mClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hand_held, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        // Verifica qual foi o botão clicado para executar a ação
        switch (v.getId()){
            case R.id.button_insert:
                String heartHateString = editText.getText().toString();
                heartHate =  new Float(heartHateString).floatValue();
                buildFitnessAndConnectClient(R.id.button_insert);
                break;
            case R.id.button_get:
                buildFitnessAndConnectClient(R.id.button_get);
                break;
        }
    }

    public void buildFitnessAndConnectClient(int idButton) {
        buttonAction = idButton;
        // Contruindo o fitness client
        buildFitnessClient();
        // Conequitando o Fitness API
        Log.i(Constants.TAG, "Connecting...");

        if(mClient != null && !mClient.isConnected()){
            mClient.connect();
        }

    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)

                // API utilizada para disponibilizar as fontes de dados de um sensor físico
                // em dispositivos locais e de companhia (wearables)
                .addApi(Fitness.HISTORY_API)

                 // Escopo utilizado par ler/escrever dados biométricos de bátimentos cardiácos
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                        //.addApi(new Scope (Scopes.))

                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {

                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
                                Object[] array = new Object[3];
                                array[0] = getApplication();
                                array[1] = mClient;

                                if (R.id.button_insert == buttonAction) {
                                    array[2] = heartHate;
                                    new InsertDataTask().execute(array);
                                } else {
                                    new FindDataTask().execute(array);
                                }

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(Constants.TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(Constants.TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(Constants.TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            HandHeldActivity.this, 0);
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(Constants.TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(HandHeldActivity.this,
                                                Constants.REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(Constants.TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }


}

