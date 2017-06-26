package tech.lankabentara.FC6P01_student.TypeFaceUtil;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class TypeFaceUtil {

    /**
     * Set text font for app
     */
    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
          //  Log.e("Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }
}