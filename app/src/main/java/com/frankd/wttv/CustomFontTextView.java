package com.frankd.wttv;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by FrankD on 7-6-2015.
 */
public class CustomFontTextView extends TextView {

    private static final String sScheme =
            "http://schemas.android.com/apk/res-auto";
    private static final String sAttribute = "customFont";

    static enum CustomFont {
        BIG_NOODLE_TITLE("fonts/big_noodle_titling.ttf"),
        BIG_NOODLE_TITLE_OBLIQUE("fonts/big_noodle_titling_oblique.ttf"),
        OSWALD_REGULAR("fonts/Oswald-Regular.ttf");

        private final String fileName;

        CustomFont(String fileName) {
            this.fileName = fileName;
        }

        static CustomFont fromString(String fontName) {
            return CustomFont.valueOf(fontName.toUpperCase(Locale.getDefault()));
        }

        public Typeface asTypeface(Context context) {
            return Typeface.createFromAsset(context.getAssets(), fileName);
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        } else {
            final String fontName = attrs.getAttributeValue(sScheme, sAttribute);

            if (fontName == null) {
                throw new IllegalArgumentException("You must provide \"" + sAttribute + "\" for your text view");
            } else {
                final Typeface customTypeface = CustomFont.fromString(fontName).asTypeface(context);
                setTypeface(customTypeface);
            }
        }
    }
}
