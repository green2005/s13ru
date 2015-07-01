package com.parser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorHelper {
    public static void showError(Context context, Exception e){
        showError(context, e.getMessage());
    }

    public static  void showError(Context context, String errorMessage) {
        if (context != null) {
            String msgBadInet = context.getString(R.string.unable_resolve_host);
            if (errorMessage.contains(msgBadInet)){
                errorMessage = context.getString(R.string.check_inet);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setNegativeButton(context.getResources().getString(R.string.ok), new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setTitle(context.getResources().getString(R.string.error));
            builder.setCancelable(false);
            builder.setMessage(errorMessage);
            builder.create().show();
        }
    }
}
