package com.parser.authentication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    public AuthDialog(Context context, AuthenticateListener listener) {
        super(context);
        setTitle(context.getString(R.string.auth_dialog_title));
        mListener = listener;
        setContentView(R.layout.dialog_authenticate);
        mPwd = (EditText) findViewById(R.id.pwdEdit);
        mUserName = (EditText) findViewById(R.id.userNameEdit);
        Button btnOk = (Button)findViewById(R.id.okbtn);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToCommit();
            }
        });

        mPwd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryToCommit();
                    return true;
                }
                return false;
            }
        });
    }

    private String getPwd(){
        return pwd;
    }

    private void tryToCommit(){
        Toast.makeText(getContext(), "test", Toast.LENGTH_LONG).show();

    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putString(PWD_KEY, );

        return super.onSaveInstanceState();
    }
}
