package Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import ir.ncis.kojast.R;

public class DialogProgress extends ProgressDialog {
    private Context context;
    private String message;

    public DialogProgress(Context context) {
        super(context);
        this.context = context;
    }

    public DialogProgress(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        TextView txtMessage = findViewById(R.id.txtMessage);
        txtMessage.setText(message != null ? message : context.getText(R.string.message_wait));
        setCancelable(false);
    }

    public DialogProgress setMessage(String message) {
        this.message = message;
        return this;
    }
}
