package mdquizgames.geography.quiz;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.Calendar;

public class InternetDialog {

    private Dialog dialog;
    private Button reloadButton;
    private ProgressBar simpleProgressBar;
    private TextView textViewMessage;
    Context context;
    private boolean internetConnection;
    private final int reloadingTime = 3000;

    public InternetDialog(Context context) {
        this.context = context;
        init();
        this.dialog = getDialog();
        this.simpleProgressBar = getSimpleProgressBar();
        this.reloadButton = getReloadButton();
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public Button getReloadButton() {
        return reloadButton;
    }

    public void setReloadButton(Button reloadButton) {
        this.reloadButton = reloadButton;
    }

    public ProgressBar getSimpleProgressBar() {
        return simpleProgressBar;
    }

    public void setSimpleProgressBar(ProgressBar simpleProgressBar) {
        this.simpleProgressBar = simpleProgressBar;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void init(){
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            dialog = new Dialog(context, android.R.style.Theme_Holo_Wallpaper_NoTitleBar);
            dialog.setContentView(R.layout.internet_dialog);
            dialog.setCancelable(false);
            simpleProgressBar = dialog.findViewById(R.id.simpleProgressBar);
            reloadButton = dialog.findViewById(R.id.startButton);
            textViewMessage = dialog.findViewById(R.id.textViewMessage);

            reloadButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    reloadButtonClick();
                }
            });

            reloadButtonClick();
        }catch(Exception e){
            // Crashlytics.logException(e);
        }
    }

    public boolean isNetworkConnected() {
        try{
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }catch(Exception e){
            // Crashlytics.logException(e);
            return false;
        }
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("www.google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            // Crashlytics.logException(e);
            return false;
        }
    }

    public boolean internetConnection(){
        try{
            return (isNetworkConnected() || isInternetAvailable());
        } catch (Exception e) {
            // Crashlytics.logException(e);
            return false;
        }
    }

    public void openDialog(){
        try {
            if(dialog!=null) {
                dialog.show();
            }
        }catch (Exception e){
            //  Crashlytics.logException(e);
        }
    }

    public void closeDialog(){
        try {
            dialog.dismiss();
        }catch (Exception e){
            // Crashlytics.logException(e);
        }
    }

    private void reloadButtonClick(){

        try {
            simpleProgressBar.setVisibility(View.VISIBLE);
            reloadButton.setAlpha(0);
            textViewMessage.setText("Checking Internet Connection");
            internetConnection = internetConnection();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (!internetConnection) {
                        reloadButton.setAlpha(1);
                        textViewMessage.setText("No Internet Connection");
                        simpleProgressBar.setVisibility(View.INVISIBLE);
                    } else {
                        simpleProgressBar.setVisibility(View.INVISIBLE);
                        closeDialog();
                    }
                }
            }, reloadingTime);
        }catch (Exception e){

        }
    }
}

