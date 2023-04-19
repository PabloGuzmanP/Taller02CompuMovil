package utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.taller_02.R;
import com.google.android.material.snackbar.Snackbar;

public class AlertUtils {
    private static final String TAG = AlertUtils.class.getName();

    public static void  shortToast(Context context, String message){
        Log.i(TAG, String.format("shortToast: %s", message));
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void  longToast(Context context, String message){
        Log.i(TAG, String.format("longToast: %s", message));
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void shortSimpleSnackbar(View parentView, String message) {
        Log.i(TAG, String.format("shortSimpleSnackbar: %s", message));
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static void longSimpleSnackbar(View parentView, String message) {
        Log.i(TAG, String.format("longSimpleSnackbar: %s", message));
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void indefiniteSnackbar(View parentView, String message) {
        Log.i(TAG, String.format("indefiniteSnackbar: %s", message));
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.snackbar_dismiss_label, view -> {
            snackbar.dismiss();
        });
    snackbar.show();
    }
}
