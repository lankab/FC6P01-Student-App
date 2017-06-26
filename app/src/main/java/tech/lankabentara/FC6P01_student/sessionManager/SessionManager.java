package tech.lankabentara.FC6P01_student.sessionManager;

import android.content.Context;
import android.content.SharedPreferences;

import tech.lankabentara.FC6P01_student.constants.Constants;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class SessionManager {

    public static String sessionUserId;

    public void setPreferences(Context context, String sessionStatus, String sessionUserId) {

        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.KEY_SESSION_STATUS, sessionStatus);
        editor.putString(Constants.KEY_SESSION_USER_ID,sessionUserId);
        editor.commit();

    }

    public String getPreferences(Context context, String sessionStatus){

        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(Constants.KEY_SESSION_STATUS,  "");
        sessionUserId = prefs.getString(Constants.KEY_SESSION_USER_ID,  "");
        return position;
    }
}
