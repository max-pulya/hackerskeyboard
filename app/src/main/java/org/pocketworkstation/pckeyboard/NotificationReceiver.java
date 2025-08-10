package org.pocketworkstation.pckeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class NotificationReceiver extends BroadcastReceiver {
    static final String TAG = "PCKeyboard/Notification";
    static public final String ACTION_SHOW = "org.pocketworkstation.pckeyboard.SHOW";
    static public final String ACTION_SETTINGS = "org.pocketworkstation.pckeyboard.SETTINGS";
    static public final String ACTION_GAMEPAD = "org.pocketworkstation.pckeyboard.GAMEPAD";

    private LatinIME mIME;

    NotificationReceiver(LatinIME ime) {
        super();
        mIME = ime;
        Log.i(TAG, "NotificationReceiver created, ime=" + mIME);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "NotificationReceiver.onReceive called, action=" + action);
        if (action.equals(ACTION_SHOW)) {
            startKeyboard(context,false);
        }
        else if (action.equals(ACTION_GAMEPAD)){
            startKeyboard(context,true);
        }
        else if (action.equals(ACTION_SETTINGS)) {
            Intent i=new Intent(mIME, LatinIMESettings.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
    private void startKeyboard(Context context,boolean isGamepad){
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if(mIME.mKeyboardClosingLock){
                mIME.mKeyboardClosingLock=false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mIME.hideFloatingKeyboard();
                }
                mIME.onFinishInput();
            }
            else {
                mIME.startFloatingKeyboard(isGamepad);

            }
        }
    }
}
