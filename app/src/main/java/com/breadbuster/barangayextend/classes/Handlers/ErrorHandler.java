package com.breadbuster.barangayextend.classes.Handlers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

public class ErrorHandler{
    public static ErrorHandler mInstance;
    private static Context mContext;

    public ErrorHandler(Context context){
        mContext = context;
    }

    public static synchronized ErrorHandler getInstance(Context context){
        if(mInstance == null){
            mInstance = new ErrorHandler(context);
        }
        return mInstance;
    }

    public void viewErrorMessage(Context context, String title, String message) {
        MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(context)
                .setTitle(title)
                .setDescription(message)
                .setPositiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }}).show();
    }

    public void viewToastErrorMessage(Context context, String message){
        try{
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }catch(Exception ex){
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
