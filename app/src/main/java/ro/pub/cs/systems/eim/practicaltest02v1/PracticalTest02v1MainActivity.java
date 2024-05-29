package ro.pub.cs.systems.eim.practicaltest02v1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02v1.network.ServerThread;

public class PracticalTest02v1MainActivity extends AppCompatActivity {

    public EditText serverPortEditText = null;
    public EditText clientAddressEditText = null;
    public EditText clientPortEditText = null;
    public EditText wordEditText = null;
    public Button getAutocompleteButton = null;
    public Button connectButton = null;
    public TextView informationTextView = null;

    public ServerThread serverThread = null;
    public ClientThread clientThread = null;

    private ConnectButtonOnClick connectButtonOnClick = new ConnectButtonOnClick();
    private class ConnectButtonOnClick implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
//            pornesc server thread pe portul serverPort
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private GetAutocompleteButtonOnClickListener getAutocompleteButtonOnClickListener = new GetAutocompleteButtonOnClickListener();
    private class GetAutocompleteButtonOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String word = wordEditText.getText().toString();
            if (word.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            informationTextView.setText(Constants.EMPTY_STRING);
            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), word, informationTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v1_main);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");

        serverPortEditText = (EditText)findViewById(R.id.server_port);
        clientAddressEditText = (EditText)findViewById(R.id.client_address);
        clientPortEditText = (EditText)findViewById(R.id.client_port);
        wordEditText = (EditText)findViewById(R.id.client_word);
        informationTextView = (TextView)findViewById(R.id.information_text_view);

        getAutocompleteButton = (Button)findViewById(R.id.get_autocomplete_button);
        getAutocompleteButton.setOnClickListener(getAutocompleteButtonOnClickListener);

        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonOnClick);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}