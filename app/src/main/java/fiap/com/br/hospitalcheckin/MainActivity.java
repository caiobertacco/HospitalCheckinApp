package fiap.com.br.hospitalcheckin;

import android.Manifest;
import android.app.AlarmManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import fiap.com.br.hospitalcheckin.entity.Paciente;
import fiap.com.br.hospitalcheckin.entity.Topic;


public class MainActivity extends AppCompatActivity {

    public static final String MQTT_SERVICE_URI = "tcp://iot.eclipse.org:1883";

    private MqttAndroidClient client;
    private Button button;
    private Paciente paciente = new Paciente();
    private String topic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.checkin);

        paciente.setNome("Caio");
        paciente.setSobrenome("Menezes");
        paciente.setConvenio("Bradesco");
        paciente.setNumCartaoConvenio("123456789");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PacienteCheckin pch = new PacienteCheckin(paciente);
                pch.execute();
                button.setEnabled(false);
                while (pch.getPosition() == null){
                    System.out.print(".");
                }
                Toast.makeText(MainActivity.this, "Checkin iniciado! \n Posição na fila: " + pch.getPosition(), Toast.LENGTH_SHORT).show();
            }
        });
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        connectMQTTClient(new Topic(paciente));
    }

    private void connectMQTTClient(final Topic topico) {
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(),
                        MQTT_SERVICE_URI,
                        clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribeIn(topico.getText());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String resposta = new String(message.getPayload());
                    if (resposta != null){
                        if(resposta.equals("1")){
                            button.setEnabled(false);
                            Toast.makeText(MainActivity.this, "Checkin realizado!", Toast.LENGTH_SHORT).show();
                        } else{
                            button.setEnabled(true);
                        }
                    } else {
                        button.setEnabled(true);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i("TAG", "Delivery complete");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeIn(String topico) {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topico, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    button.setEnabled(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribeIn(String topico) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topico);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    button.setEnabled(false);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribeIn(new Topic(paciente).getText());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
