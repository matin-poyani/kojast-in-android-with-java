package Web;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ir.ncis.kojast.App;

public class WebService {

    public static String read(HashMap<String, String> params) {
        String result = null;
        Uri.Builder builder = new Uri.Builder();
        String urlString = App.URL_SERVICE;
        if (params.containsKey("action")) {
            urlString += "action=" + params.get("action");
            params.remove("action");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        String parameters = builder.build().getEncodedQuery();
        try {
            Log.i("WS", "REQUEST (@" + App.timeStamp() + "): " + urlString);
            Log.i("WS", "PARAMS (@" + App.timeStamp() + "): " + params.toString());
            result = new SimpleAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString, parameters).get();
            Log.i("WS", "RESPONSE (@" + App.timeStamp() + "): " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class SimpleAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.connect();
                if (strings[1] != null) {
                    OutputStream os = connection.getOutputStream();
                    byte[] parameters = strings[1].getBytes(Charset.forName("UTF-8"));
                    os.write(parameters);
                    os.flush();
                    os.close();
                }
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = convertInputStreamToString(connection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
