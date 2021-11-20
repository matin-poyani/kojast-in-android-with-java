package Async;

import android.os.AsyncTask;

import java.io.IOException;

public class AsyncNetworkCheck extends AsyncTask<Void, Void, Boolean> {
    private Operations operations;

    @Override
    protected Boolean doInBackground(Void... voids) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            ipProcess.destroy();
            runtime.freeMemory();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (operations != null) {
            if (result) {
                operations.online();
            } else {
                operations.offline();
            }
        }
    }

    public AsyncNetworkCheck setOperations(Operations operations) {
        this.operations = operations;
        return this;
    }

    public interface Operations {
        void online();
        void offline();
    }
}