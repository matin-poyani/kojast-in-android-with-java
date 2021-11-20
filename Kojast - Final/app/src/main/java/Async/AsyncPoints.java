package Async;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Models.StructPoint;
import Web.WebService;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class AsyncPoints extends AsyncTask<Integer, Void, ArrayList<StructPoint>> {
    private Operations operations;

    @Override
    protected void onPreExecute() {
        if (operations != null) {
            operations.before();
        }
    }

    @Override
    protected ArrayList<StructPoint> doInBackground(Integer... integers) {
        ArrayList<StructPoint> result = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "points");
        params.put("city_id", String.valueOf(integers[0]));
        params.put("type_id", String.valueOf(integers[1]));
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
                        StructPoint point = new StructPoint();
                        point.id = item.getInt("id");
                        point.name = item.getString("name");
                        point.description = item.getString("description");
                        point.lat = item.getDouble("lat");
                        point.lng = item.getDouble("lng");
                        result.add(point);
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
    protected void onPostExecute(ArrayList<StructPoint> result) {
        if (operations != null) {
            operations.after(result);
        }
    }

    public AsyncPoints setOperations(Operations operations) {
        this.operations = operations;
        return this;
    }

    public interface Operations {
        void before();

        void failure(String error);

        void after(ArrayList<StructPoint> result);
    }
}
