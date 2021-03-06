package com.android.xl;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.meizu.cloud.pushsdk.util.MzSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by xilinch on 2017/10/9.
 */

public class UtilOSProperties {
    public static final String SYS_EMUI = "sys_emui";
    public static final String SYS_MIUI = "sys_miui";
    public static final String SYS_FLYME = "sys_flyme";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    public static String getSystem(Context context) {
        String system = "";
        FileInputStream fileInputStream = null;
        try {
            Properties prop = new Properties();
            fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(fileInputStream);
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                system = SYS_MIUI; //小米
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    || prop.getProperty(KEY_EMUI_VERSION, null) != null
                    || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                system = SYS_EMUI; //华为
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme") || (context != null && MzSystemUtils.isBrandMeizu(context))) {
                system = SYS_FLYME; //魅族
            }
        } catch (Exception e) {
            e.printStackTrace();
            system = null;
        } finally {
            if (fileInputStream != null){
                try{
                    fileInputStream.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }
            Log.e("my","getSystem:" + system);
        }
        if (TextUtils.isEmpty(system)) {
            String brand = android.os.Build.BRAND;
            if ("HUAWEI".equalsIgnoreCase(brand) || "honor".equalsIgnoreCase(android.os.Build.BRAND)) {
                system = SYS_EMUI; //华为
            } else if("Xiaomi".equalsIgnoreCase(brand) || "Xiaomi".equals(Build.MANUFACTURER)){
                //xiaomi   model
                system = SYS_MIUI;
            } else if("meizu".equalsIgnoreCase(android.os.Build.BRAND)){
                system = SYS_FLYME;
            }
        }
        return system;
    }

    public static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

}
