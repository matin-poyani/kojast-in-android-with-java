package ir.ncis.kojast;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class App extends Application {
    public static final Handler HANDLER = new Handler();
    public static final String URL_SERVER = "http://192.168.1.100/kojast/";
    public static final String URL_SERVICE = URL_SERVER + "?";
    public static boolean PERMISSION_ACL = true;
    public static boolean PERMISSION_AFL = true;
    @SuppressLint("StaticFieldLeak")
    public static Context CONTEXT;
    public static LayoutInflater INFLATER;

    public static String timeStamp() {
        return String.valueOf(System.currentTimeMillis()).substring(0, 10);
    }

    public static void toast(String message) {
        toast(message, false);
    }

    public static void toast(String message, boolean longDuration) {
        @SuppressLint("InflateParams") View view = INFLATER.inflate(R.layout.toast, null);
        TextView txtMessage = view.findViewById(R.id.txtMessage);
        txtMessage.setText(message);
        Toast toast = new Toast(CONTEXT);
        toast.setDuration(longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();
    }
}
