package com.parser.blogio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parser.ErrorHelper;
import com.parser.R;

public class AuthDialog extends Dialog {
    private AuthenticateListener mListener;
    private static final String PWD_KEY = "pwd";
    private static final String USER_KEY = "user";
    private EditText mPwd;
    private EditText mUserName;

    public static void editAuth(Context context, AuthenticateListener listener) {
        AuthDialog dialog = new AuthDialog(context, listener);
        dialog.show();
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getString(USER_KEY, "");
    }

    public static String getPwd(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getString(PWD_KEY, "");
    }

    private static void setAuthSettings(String userName, String pwd, Context context) {
        SharedPreferences prefs = getPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_KEY, userName);
        editor.putString(PWD_KEY, pwd);
        editor.apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        String key = context.getApplicationContext().getPackageName();
        return context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
    }


    public AuthDialog(Context context, AuthenticateListener listener) {
        super(context);
        setTitle(context.getString(R.string.auth_dialog_title));
        mListener = listener;
        setContentView(R.layout.dialog_authenticate);
        mPwd = (EditText) findViewById(R.id.pwdEdit);
        mUserName = (EditText) findViewById(R.id.userNameEdit);
        mPwd.setText(getPwd(getContext()));
        mUserName.setText(getUserName(getContext()));
        Button btnOk = (Button) findViewById(R.id.okbtn);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToLogin();
            }
        });

        mPwd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryToLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void tryToLogin() {
        BlogConnector blogConnector = new BlogConnector();
        Context context = getContext();
        if (context == null) {
            return; //wtf?
        }
        final ProgressDialog pg = new ProgressDialog(context);
        pg.setMessage(context.getString(R.string.please_wait));
        pg.show();
        blogConnector.login(mUserName.getText().toString(), mPwd.getText().toString(),
                new BlogConnector.RequestListener() {
                    @Override
                    public void onRequestDone(BlogConnector.QUERY_RESULT result, String errorMessage) {
                        if (pg != null && pg.isShowing()) {
                            pg.dismiss();
                        }
                        Context context = getContext();
                        switch (result) {
                            case OK: {
                                if (context != null) {
                                    Toast.makeText(context, context.getString(R.string.auth_completed_ok), Toast.LENGTH_SHORT).show();
                                    setAuthSettings(mUserName.getText().toString(), mPwd.getText().toString(), context);
                                    dismiss();
                                }
                                break;
                            }
                            case ACCESS_DENIED: {
                                if (context != null) {
                                    Toast.makeText(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case ERROR: {
                                if (context != null) {
                                    ErrorHelper.showError(context, errorMessage);
                                }
                                break;
                            }
                        }
                    }
                });
    }


}
