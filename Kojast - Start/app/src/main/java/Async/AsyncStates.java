package Async;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Models.StructState;
import Web.WebService;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class AsyncStates extends AsyncTask<Void, Void, ArrayList<StructState>> {
    private Operations operations;

    @Override
    protected void onPreExecute() {
        if (operations != null) {
            operations.before();
        }
    }

    @Override
    protected ArrayList<StructState> doInBackground(Void... voids) {
        ArrayList<StructState> result = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "states");
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
                        StructState state = new StructState();
                        state.id = item.getInt("id");
                        state.name = item.getString("name");
                        result.add(state);
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
    protected void onPostExecute(ArrayList<StructState> result) {
        if (operations != null) {
            operations.after(result);
        }
    }

    public AsyncStates setOperations(Operations operations) {
        this.operations = operations;
        return this;
    }

    public interface Operations {
        void before();

        void failure(String error);

        void after(ArrayList<StructState> result);
    }
}
