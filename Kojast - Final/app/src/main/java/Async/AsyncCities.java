package Async;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Models.StructCity;
import Web.WebService;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class AsyncCities extends AsyncTask<Integer, Void, ArrayList<StructCity>> {
    private Operations operations;

    @Override
    protected void onPreExecute() {
        if (operations != null) {
            operations.before();
        }
    }

    @Override
    protected ArrayList<StructCity> doInBackground(Integer... integers) {
        ArrayList<StructCity> result = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "cities");
        params.put("state_id", String.valueOf(integers[0]));
        try {
            String response = WebService.read(params);
            if (response != null) {
                JSONObject json = new JSONObject(response);
                final String error = json.getString("error");
                if (error.isEmpty()) {
                    JSONArray data = json.getJSONArray("data");
                    result = new ArrayList<>();
                    int len = data.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject item = data.getJSONObject(i);
                        StructCity city = new StructCity();
                        city.id = item.getInt("id");
                        city.name = item.getString("name");
                        result.add(city);
                    }
                } else {
                    if (operations != null) {
                        App.HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                operations.failure(error);
                            }
                        });
                    }
                }
            } else {
                if (operations != null) {
                    App.HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            operations.failure(App.CONTEXT.getString(R.string.error_info));
                        }
                    });
                }
            }
        } catch (final JSONException e) {
            if (operations != null) {
                App.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        operations.failure(e.getMessage());
                    }
                });
            }
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<StructCity> result) {
        if (operations != null) {
            operations.after(result);
        }
    }

    public AsyncCities setOperations(Operations operations) {
        this.operations = operations;
        return this;
    }

    public interface Operations {
        void before();

        void failure(String error);

        void after(ArrayList<StructCity> result);
    }
}
