package com.parser.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parser.R;

public class CommentEditDialog extends DialogFragment {
    public interface OnDoneEditingListener {
        public void onDoneEditing(String comment);
    }

    private EditText mEdit;
    private OnDoneEditingListener mListener;
    private String mComment;

    public static CommentEditDialog getNewDialog(String comment, OnDoneEditingListener listener) {
        CommentEditDialog dialog = new CommentEditDialog();
        dialog.mListener = listener;
        dialog.mComment = comment;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_comment, null);
        Button btnOk = (Button) view.findViewById(R.id.okbtn);
        getDialog().setTitle(R.string.comment_editing);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDoneEditing(mEdit.getText().toString());
                    getDialog().dismiss();
                }
            }
        });

        mEdit = (EditText) view.findViewById(R.id.commentEdit);
        if (!TextUtils.isEmpty(mComment)) {
            mEdit.setText(mComment);
            mEdit.setSelection(mComment.length());
            mEdit.requestFocus();
        }

        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });
        return view;
    }
}
