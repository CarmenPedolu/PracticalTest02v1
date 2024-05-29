package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.general.Utilities;

public class CommunicationThread extends Thread {
    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word)!");
            String word = bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word)!");
                return;
            }

            HashMap<String, String> data = serverThread.getData();
            String wordAutocompleteInformation;
            if (data.containsKey(word)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                wordAutocompleteInformation = data.get(word);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                URL urlAddress = new URL(Constants.WEB_SERVICE_ADDRESS + word);
                URLConnection urlConnection = urlAddress.openConnection();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String pageSourceCode;
                StringBuilder stringBuilder = new StringBuilder();
                String currentLine;
                while ((currentLine = bufferedReader1.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }
                bufferedReader1.close();
                pageSourceCode = stringBuilder.toString();
//                JSONObject content = new JSONObject(pageSourceCode);

//                print content
                Log.i(Constants.TAG, pageSourceCode);
//                Log.i(Constants.TAG, content.toString());

//                JSONArray results = content.getJSONArray(Constants.RESULTS);
//                JSONObject current = results.getJSONObject(0);
//                String wordAutocomplete = current.getString(Constants.WORD_AUTOCOMPLETE);
//                wordAutocompleteInformation = wordAutocomplete;

//                2024-05-29 09:06:55.037  9328-9358  [PracticalTest02]       ro....systems.eim.practicaltest02v1  I  ["cafea",["cafea boabe","cafea lavazza","cafea davidoff","cafea decofeinizata","cafea tchibo","cafea lavazza boabe","cafea julius meinl","cafea la ibric","cafea jacobs","cafea perla","cafea tassimo","cafea florescu","cafea de specialitate","cafea fortuna","cafea capsule"],["","","","","","","","","","","","","","",""],[],{"google:clientdata":{"bpc":false,"tlw":false},"google:suggestrelevance":[601,600,562,561,560,559,558,557,556,555,554,553,552,551,550],"google:suggestsubtypes":[[512,433],[512,433],[512],[512],[512],[512],[512],[512],[512],[512],[512],[512],[512],[512],[512]],"google:suggesttype":["QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY","QUERY"],"google:verbatimrelevance":1300}]
                wordAutocompleteInformation = pageSourceCode;
                // Regular expression to match the desired list
                String regex = "\\[(\"[^\"]+\"(,\\s*\"[^\"]+\")*)\\]";

                // Compile the pattern
                Pattern pattern = Pattern.compile(regex);

                // Match the pattern in the input string
                Matcher matcher = pattern.matcher(wordAutocompleteInformation);

                // Extract and print the matched group
                if (matcher.find()) {
                    String coffeeNames = matcher.group(1);
                    System.out.println("[" + coffeeNames + "]");
                    serverThread.setData(word, coffeeNames );
                    wordAutocompleteInformation = coffeeNames ;
                } else {
                    System.out.println("No match found");
                }
            }
            if (wordAutocompleteInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] wordAutocompleteInformation is null!");
                return;
            }
            String result = wordAutocompleteInformation;
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
