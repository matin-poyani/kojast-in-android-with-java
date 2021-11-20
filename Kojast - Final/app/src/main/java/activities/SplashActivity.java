package activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import ir.ncis.kojast.ActivityEnhanced;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class SplashActivity extends ActivityEnhanced {
    private Button btnPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPlayServices();

        btnPermissions = findViewById(R.id.btnPermissions);
        btnPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permit();
            }
        });

        permit();
    }

    private void permit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                App.PERMISSION_ACL = false;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                App.PERMISSION_AFL = false;
            }
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                App.PERMISSION_ACL = true;
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                App.PERMISSION_AFL = true;
            }
            dismiss();
        }
    }

    private void dismiss() {
        if (App.PERMISSION_ACL && App.PERMISSION_AFL) {
            btnPermissions.setVisibility(View.INVISIBLE);
            App.HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runActivity(MainActivity.class, true);
                }
            }, 3000);
        }
    }
}
