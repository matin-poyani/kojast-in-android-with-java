package Async;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Models.StructType;
import Web.WebService;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class AsyncTypes extends AsyncTask<Void, Void, ArrayList<StructType>> {
    private Operations operations;

    @Override
    protected void onPreExecute() {
        if (operations != null) {
            operations.before();
        }
    }

    @Override
    protected ArrayList<StructType> doInBackground(Void... voids) {
        ArrayList<StructType> result = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "types");
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
                        StructType type = new StructType();
                        type.id = item.getInt("id");
                        type.title = item.getString("title");
                        result.add(type);
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
    protected void onPostExecute(ArrayList<StructType> result) {
        if (operations != null) {
            operations.after(result);
        }
    }

    public AsyncTypes setOperations(Operations operations) {
        this.operations = operations;
        return this;
    }

    public interface Operations {
        void before();

        void failure(String error);

        void after(ArrayList<StructType> result);
    }
}
