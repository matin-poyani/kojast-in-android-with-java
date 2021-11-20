package ir.ncis.kojast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

@SuppressLint("Registered")
public class ActivityEnhanced extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.INFLATER == null) {
            App.INFLATER = getLayoutInflater();
        }
    }

    public void runActivity(Class targetActivityClass) {
        runActivity(targetActivityClass, false);
    }

    public void runActivity(Class targetActivityClass, boolean finish) {
        Intent intent = new Intent(this, targetActivityClass);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }
}
