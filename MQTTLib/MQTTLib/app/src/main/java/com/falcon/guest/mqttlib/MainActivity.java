package com.falcon.guest.mqttlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, "tcp://192.168.0.2:1883", clientId);

        Button btn = (Button) findViewById(R.id.bnPublish);
        assert btn != null;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndPublish("AtoG/", "&13A200#41241BC9$1*1@1,255");
            }
        });
        Button btn2 = (Button) findViewById(R.id.btn2);
        assert btn2 != null;
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndPublish("AtoG/", "&13A200#41241BC9$1*1@1,100");
            }
        });
        Button btn3 = (Button) findViewById(R.id.btn3);
        assert btn3 != null;
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndPublish("AtoG/", "&13A200#41241BC9$1*1@1,0");
            }
        });
    }

    /*
     * Code snippet to
     * connect to the MQTT server
     * Code snippet by Kalpesh
     */
    private void connectAndPublish(final String topic, final String command) {
        //if (!client.isConnected()) {
            String clientId = MqttClient.generateClientId();
            final MqttAndroidClient client = new MqttAndroidClient(this, "tcp://192.168.0.2:1883", clientId);

            // Connection Options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

            //client = new MqttAndroidClient(this, "tcp://192.168.0.2:1883", clientId);

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(MainActivity.this, "Connection Successful! :)", Toast.LENGTH_SHORT).show();
                        publishCommand(client,topic,command);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "Unable to connect. Reason: " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (MqttException me) {
                me.printStackTrace();
            }
        /*} else {
            publishCommand(client,topic,command);
        }*/
    }

    /*
     * Publish command to
     * the server
     * Code snippet by Kalpesh
     */
    void publishCommand(MqttAndroidClient client, String topic, String command) {
        //try {
            byte[] encodedPayload = command.getBytes();//"UTF-8"
            MqttMessage message = new MqttMessage(/*encodedPayload*/command.getBytes());
            try {
                //client.publish(topic, message);
                client.publish(topic, command.getBytes(),0,false);
                Toast.makeText(MainActivity.this, "Topic: " + topic + "\nCommand: " + command, Toast.LENGTH_LONG).show();
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Unable to publish. Reason: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        /*} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        client.unregisterResources();
        client.close();
    }
}
