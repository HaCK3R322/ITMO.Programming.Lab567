package com.androsov.server.localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messenger {
    public Messenger(Locale locale) {
        setPropertiesPath("com.androsov.server.localization/text");
        setLocale(locale);
    }

    public static ResourceBundle rb;

    private String propertiesPath;

    public void setLocale(Locale locale) {
        rb = ResourceBundle.getBundle(propertiesPath, locale);
    }

    public void setPropertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }
}
