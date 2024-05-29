package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.general.Utilities;

public class ClientThread extends Thread {
    private String address;
    private int port;

    private String word;

    //    outputul
    private TextView informationTextView;

    private Socket socket;

    public ClientThread(String address, int port, String word, TextView informationTextView) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.informationTextView = informationTextView;
    }


    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            Log.i(Constants.TAG, "[CLIENT THREAD] Created socket!");
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }


            printWriter.println(word);
            printWriter.flush();
            String wordAutocomplete;
            while ((wordAutocomplete = bufferedReader.readLine()) != null) {
                final String finalizedwordAutocomplete = wordAutocomplete;
                informationTextView.post(() -> informationTextView.setText(finalizedwordAutocomplete));
            }


        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
