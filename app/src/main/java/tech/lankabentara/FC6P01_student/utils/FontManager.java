package tech.lankabentara.FC6P01_student.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class FontManager {

    Context c;

    public FontManager(Context context){
        this.c = context;
    }

    public void setAppMedium(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "AppSans_medium.otf");
        tv.setTypeface(font);
    }

    public void setAppRegular(TextView tv){

        Typeface font = Typeface.createFromAsset(c.getAssets(), "AppSans_regular.otf");
        tv.setTypeface(font);
    }
}
