/*
 * Copyright (C) 2014 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.ehviewer.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.hippo.ehviewer.data.ListUrls;
import com.hippo.ehviewer.ehclient.EhClient;

public class Config {
    @SuppressWarnings("unused")
    private static final String TAG = Config.class.getSimpleName();

    public static final String EXTERANL_DIR_NAME = "EhViewer";
    public static File sExternalDir;

    private static final String KEY_UPDATE_DATE = "update_date";

    private static final String KEY_SCREEN_ORIENTATION = "screen_orientation";
    private static final String DEFAULT_SCREEN_ORIENTATION = "0";

    private static final String KEY_CACHE_SIZE = "cache_size";
    private static final String DEFAULT_CACHE_SIZE = "25";


    private static final String KEY_AUTO_CHECK_FOR_UPDATE = "auto_check_for_update";
    private static final boolean DEFAULT_AUTO_CHECK_FOR_UPDATE = true;
    private static final String KEY_UPDATE_SERVER = "update_server";
    private static final String DEFAULT_UPDATE_SERVER = "2";

    private static final String KEY_RANDOM_THEME_COLOR = "random_theme_color";
    private static final boolean DEFAULT_RANDOM_THEME_COLOR = true;
    private static final String KEY_THEME_COLOR = "theme_color";
    private static final int DEFAULT_THEME_COLOR = 0xff0099cc;

    private static final String KEY_DEFAULT_FAVORITE = "default_favorite";
    private static final String DEFAULT_DEFAULT_FAVORITE = "-2";

    private static final String KEY_SET_ANALYTICS = "set_analyics";
    private static final boolean DEFAULT_SET_ANALYTICS = false;
    private static final String KEY_ALLOW_ANALYTICS = "allow_analyics";
    private static final boolean DEFAULT_ALLOW_ANALYTICS = false;
    private static final String KEY_POPULAR_WARNING = "popular_warning";
    private static final boolean DEFAULT_POPULAR_WARNING = true;

    private static final String KEY_SHOW_POPULAR_UPDATE_TIME = "show_popular_update_time";
    private static final boolean DEFAULT_SHOW_POPULAR_UPDATE_TIME = false;

    private static boolean mInit = false;

    private static Context mContext;
    private static SharedPreferences mConfigPre;

    /**
     * Init Config
     *
     * @param context Application context
     */
    public static void init(Context context) {
        if (mInit)
            return;
        mInit = true;

        mContext = context;
        mConfigPre = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sExternalDir = new File(Environment.getExternalStorageDirectory(), EXTERANL_DIR_NAME);
            Utils.ensureDir(sExternalDir, true);
        } else {
            sExternalDir = null;
        }
    }

    /**
     * Is init
     * @return True if init
     */
    public static boolean isInit() {
        return mInit;
    }

    public static int getInt(String key, int defValue) {
        return mConfigPre.getInt(key, defValue);
    }

    public static void setInt(String key, int value) {
        mConfigPre.edit().putInt(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return mConfigPre.getBoolean(key, defValue);
    }

    public static void setBoolean(String key, boolean value) {
        mConfigPre.edit().putBoolean(key, value).apply();
    }


    /****** For Normal Config ******/
    private static final String KEY_ALLOWED = "allowed";
    private static final String KEY_FIRST = "first_time";

    /**
     * Get is it allowed to launch
     *
     * @return True if allowed
     */
    public static boolean isAllowed() {
        return mConfigPre.getBoolean(KEY_ALLOWED, false);
    }

    /**
     * Allowed the appliation to launch
     */
    public static void allowed() {
        mConfigPre.edit().putBoolean(KEY_ALLOWED, true).apply();
    }

    /**
     * Check is it first time to launch the application
     *
     * @return
     */
    public static boolean isFirstTime() {
        return mConfigPre.getBoolean(KEY_FIRST, true);
    }

    /**
     * It is first time to launch the application
     */
    public static void firstTime() {
        mConfigPre.edit().putBoolean(KEY_FIRST, false).apply();
    }

    /**
     * Get cover cache size in MB
     *
     * @return
     */
    public static int getCoverDiskCacheSize() {
        try {
            return Integer.parseInt(mConfigPre.getString(KEY_CACHE_SIZE, DEFAULT_CACHE_SIZE));
        } catch (Exception e) {
            mConfigPre.edit().putString(KEY_CACHE_SIZE, DEFAULT_CACHE_SIZE).apply();
            return 25;
        }
    }

    public static int getScreenOriMode() {
        int screenOriMode = 0;
        try {
            screenOriMode = Integer.parseInt(mConfigPre.getString(
                    KEY_SCREEN_ORIENTATION, DEFAULT_SCREEN_ORIENTATION));
        } catch (Exception e) {
            mConfigPre.edit().putString(KEY_SCREEN_ORIENTATION, DEFAULT_SCREEN_ORIENTATION)
                    .apply();
        }
        return screenOriPre2Value(screenOriMode);
    }

    public static int screenOriPre2Value(int screenOriModePre) {
        switch (screenOriModePre) {
        case 0:
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        case 1:
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        case 2:
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        default:
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
    }

    public static boolean isAutoCheckForUpdate() {
        return mConfigPre.getBoolean(KEY_AUTO_CHECK_FOR_UPDATE,
                DEFAULT_AUTO_CHECK_FOR_UPDATE);
    }

    public static void setAutoCheckForUpdate(boolean autoCheckForUpdate) {
        mConfigPre.edit().putBoolean(KEY_AUTO_CHECK_FOR_UPDATE,
                autoCheckForUpdate).apply();
    }

    public static int getUpdateDate() {
        return mConfigPre.getInt(KEY_UPDATE_DATE, 0);
    }

    public static void setUpdateDate() {
        setUpdateDate(Utils.getDate());
    }

    public static void setUpdateDate(int date) {
        mConfigPre.edit().putInt(KEY_UPDATE_DATE, date).apply();
    }

    public static String getUpdateServer() {

        int value = 1;
        try {
            value = Integer.parseInt(mConfigPre.getString(KEY_UPDATE_SERVER, null));
        } catch (Exception e) {
            mConfigPre.edit().putString(KEY_UPDATE_SERVER, DEFAULT_UPDATE_SERVER)
                    .apply();
        }
        switch (value) {
        case 1:
            return "qiniu";
        case 0:
        default:
            return "google";
        }
    }

    public static boolean getRandomThemeColor() {
        return mConfigPre.getBoolean(KEY_RANDOM_THEME_COLOR, DEFAULT_RANDOM_THEME_COLOR);
    }

    public static int getThemeColor() {
        return mConfigPre.getInt(KEY_THEME_COLOR, DEFAULT_THEME_COLOR);
    }

    public static void setThemeColor(int themeColor) {
        mConfigPre.edit().putInt(KEY_THEME_COLOR, themeColor).apply();
    }

    public static int getDefaultFavorite() {
        return Integer.parseInt(mConfigPre.getString(KEY_DEFAULT_FAVORITE, DEFAULT_DEFAULT_FAVORITE));
    }

    public static void setDefaultFavorite(int defaultFavorite) {
        mConfigPre.edit().putString(KEY_DEFAULT_FAVORITE, String.valueOf(defaultFavorite)).apply();
    }

    public static boolean getSetAnalyics() {
        return mConfigPre.getBoolean(KEY_SET_ANALYTICS, DEFAULT_SET_ANALYTICS);
    }

    public static void setSetAnalyics(boolean setAnalyics) {
        mConfigPre.edit().putBoolean(KEY_SET_ANALYTICS, setAnalyics).apply();
    }

    public static boolean getAllowAnalyics() {
        return mConfigPre.getBoolean(KEY_ALLOW_ANALYTICS, DEFAULT_ALLOW_ANALYTICS);
    }

    public static void setAllowAnalyics(boolean setAnalyics) {
        mConfigPre.edit().putBoolean(KEY_ALLOW_ANALYTICS, setAnalyics).apply();
        if (!setAnalyics)
            setPopularWarning(true);
    }

    public static boolean getPopularWarning() {
        return mConfigPre.getBoolean(KEY_POPULAR_WARNING, DEFAULT_POPULAR_WARNING);
    }

    public static void setPopularWarning(boolean popularWarning) {
        mConfigPre.edit().putBoolean(KEY_POPULAR_WARNING, popularWarning).apply();
    }

    public static boolean getShowPopularUpdateTime() {
        return mConfigPre.getBoolean(KEY_SHOW_POPULAR_UPDATE_TIME, DEFAULT_SHOW_POPULAR_UPDATE_TIME);
    }

    /****** Translate ******/
    private static final String KEY_TRADITIONAL_CHINESE_WARNING = "traditional_chinese_warning";
    private static final boolean DEFAULT_TRADITIONAL_CHINESE_WARNING = true;

    public static boolean getTraditionalChineseWarning() {
        return mConfigPre.getBoolean(
                KEY_TRADITIONAL_CHINESE_WARNING, DEFAULT_TRADITIONAL_CHINESE_WARNING);
    }

    public static void setTraditionalChineseWarning(boolean traditional_chinese_warning) {
        mConfigPre.edit().putBoolean(KEY_TRADITIONAL_CHINESE_WARNING, traditional_chinese_warning).apply();
    }

    /****** Display ******/

    private static final String KEY_LIST_MODE = "list_mode";
    private static final String DEFAULT_LIST_MODE = "0";

    private static final String KEY_LIST_THUMB_COLUMNS_PORTRAIT = "list_thumb_columns_portrait";
    private static final int DEFAULT_LIST_THUMB_COLUMNS_PORTRAIT = 3;

    private static final String KEY_LIST_THUMB_COLUMNS_LANDSCAPE = "list_thumb_columns_landscape";
    private static final int DEFAULT_LIST_THUMB_COLUMNS_LANDSCAPE = 5;

    private static final String KEY_PREVIEW_COLUMNS_PORTRAIT = "preview_columns_portrait";
    private static final int DEFAULT_PREVIEW_COLUMNS_PORTRAIT = 3;

    private static final String KEY_PREVIEW_COLUMNS_LANDSCAPE = "preview_columns_landscape";
    private static final int DEFAULT_PREVIEW_COLUMNS_LANDSCAPE = 5;

    public static int getListMode() {
        return Integer.parseInt(mConfigPre.getString(KEY_LIST_MODE, DEFAULT_LIST_MODE));
    }

    public static void setListMode(int listMode) {
        mConfigPre.edit().putString(KEY_LIST_MODE, String.valueOf(listMode)).apply();
    }

    public static int getListThumbColumnsPortrait() {
        return mConfigPre.getInt(
                KEY_LIST_THUMB_COLUMNS_PORTRAIT, DEFAULT_LIST_THUMB_COLUMNS_PORTRAIT);
    }

    public static int getListThumbColumnsLandscape() {
        return mConfigPre.getInt(
                KEY_LIST_THUMB_COLUMNS_LANDSCAPE, DEFAULT_LIST_THUMB_COLUMNS_LANDSCAPE);
    }

    public static int getPreviewColumnsPortrait() {
        return mConfigPre.getInt(
                KEY_PREVIEW_COLUMNS_PORTRAIT, DEFAULT_PREVIEW_COLUMNS_PORTRAIT);
    }

    public static int getPreviewColumnsLandscape() {
        return mConfigPre.getInt(
                KEY_PREVIEW_COLUMNS_LANDSCAPE, DEFAULT_PREVIEW_COLUMNS_LANDSCAPE);
    }

    private static final String KEY_VERSION_CODE = "version_code";
    private static final int DEFAULT_VERSION_CODE = Integer.MAX_VALUE;

    public static int getVersionCode() {
        return mConfigPre.getInt(KEY_VERSION_CODE,
                DEFAULT_VERSION_CODE);
    }

    public static void setVersionCode(int versionCode) {
        mConfigPre.edit().putInt(KEY_VERSION_CODE, versionCode).apply();
    }

    /****** GalleryActivity ******/

    private static final String KEY_GALLERY_FIRST = "gallery_first";
    private static final boolean DEFAULT_GALLERY_FIRST = true;

    public static boolean getGalleryFirst() {
        return mConfigPre.getBoolean(KEY_GALLERY_FIRST, DEFAULT_GALLERY_FIRST);
    }

    public static void setGalleryFirst(boolean galleryFirst) {
        mConfigPre.edit().putBoolean(KEY_GALLERY_FIRST, galleryFirst).apply();
    }

    /****** Mode an API Mode ******/

    private static final String KEY_MODE = "mode";
    private static final int DEFAULT_MODE = EhClient.MODE_G;

    private static final String KEY_API_MODE = "api_mode";
    private static final int DEFAULT_API_MODE = EhClient.MODE_G;

    private static final String KEY_LOFI_RESOLUTION = "lofi_resolution";
    private static final int DEFAULT_LOFI_RESOLUTION = 1;

    public static int getMode() {
        return mConfigPre.getInt(KEY_MODE, DEFAULT_MODE);
    }

    public static void setMode(int mode) {
        mConfigPre.edit().putInt(KEY_MODE, mode).apply();
    }

    public static int getApiMode() {
        return mConfigPre.getInt(KEY_API_MODE, DEFAULT_API_MODE);
    }

    public static void setApiMode(int apiMode) {
        mConfigPre.edit().putInt(KEY_API_MODE, apiMode).apply();
    }

    public static int getLofiResolution() {
        return mConfigPre.getInt(KEY_LOFI_RESOLUTION, DEFAULT_LOFI_RESOLUTION);
    }

    public static void setLofiResolution(int lofiResolution) {
        mConfigPre.edit().putInt(KEY_LOFI_RESOLUTION, lofiResolution).apply();
    }

    /****** Eh Config ******/

    private static final String KEY_DEFAULT_CAT = "default_cat";
    private static final int DEFAULT_DEFAULT_CAT = ListUrls.ALL_CATEGORT;

    // m for normal, l for large
    public static final String KEY_PREVIEW_MODE = "preview_mode";
    public static final String PREVIEW_MODE_NORMAL = "m";
    public static final String PREVIEW_MODE_LARGE = "l";
    private static final String DEFAULT_PREVIEW_MODE = PREVIEW_MODE_LARGE;

    /**
     * reclass   0x1
     * language  0x2
     * parody    0x4
     * character 0x8
     * group     0x10
     * artist    0x20
     * male      0x40
     * female    0x80
     */
    private static final String KEY_EXCLUDE_TAG_GROUP = "exculde_tag_group";
    private static final int DEFAULT_EXCLUDE_TAG_GROUP = 0;

    /**
     *            Original   Translated    Rewrite    All
     * Japanese                 1024        2048
     * English       1          1025        2049
     * Chinese       10         1034        2058
     * Dutch         20         1044        2068
     * French        30         1054        2078
     * German        40         1064        2088
     * Hungarian     50         1074        2098
     * Italian       60         1084        2108
     * Korean        70         1094        2118
     * Polish        80         1104        2128
     * Portuguese    90         1114        2138
     * Russian       100        1124        2148
     * Spanish       110        1134        2158
     * Thai          120        1144        2168
     * Vietnamese    130        1154        2178
     * Other         255        1279        2303
     */
    private static final String KEY_EXCLUDE_LANGUAGE = "exculde_language";
    private static final String DEFAULT_EXCLUDE_LANGUAGE = "";

    private static final String KEY_PREVIEW_PER_ROW = "preview_per_row";
    private static final String DEFAULT_PREVIEW_PER_ROW = "3";

    public static int getDefaultCat() {
        return mConfigPre.getInt(KEY_DEFAULT_CAT, DEFAULT_DEFAULT_CAT);
    }

    public static void setDefaultCat(int defaultCat) {
        mConfigPre.edit().putInt(KEY_DEFAULT_CAT, defaultCat).apply();
    }

    public static String getPreviewMode() {
        return mConfigPre.getString(KEY_PREVIEW_MODE, DEFAULT_PREVIEW_MODE);
    }

    public static int getExculdeTagGroup() {
        return mConfigPre.getInt(KEY_EXCLUDE_TAG_GROUP, DEFAULT_EXCLUDE_TAG_GROUP);
    }

    public static void setExculdeTagGroup(int exculdeTagGroup) {
        mConfigPre.edit().putInt(KEY_EXCLUDE_TAG_GROUP, exculdeTagGroup).apply();
    }

    public static String getExculdeLanguage() {
        return mConfigPre.getString(KEY_EXCLUDE_LANGUAGE, DEFAULT_EXCLUDE_LANGUAGE);
    }

    public static void setExculdeLanguage(String exculdeLanguage) {
        mConfigPre.edit().putString(KEY_EXCLUDE_LANGUAGE, exculdeLanguage).apply();
    }

    public static int getPreviewPerRow() {
        return Integer.parseInt(mConfigPre.getString(KEY_PREVIEW_PER_ROW, DEFAULT_PREVIEW_PER_ROW));
    }

    /****** Read ******/

    private static final String KEY_PAGE_SCALING = "page_scaling";
    private static final String DEFAULT_PAGE_SCALING_STR = "3";
    private static final int DEFAULT_PAGE_SCALING = 3;

    private static final String KEY_START_POSITION = "start_position";
    private static final String DEFAULT_START_POSITION_STR = "1";
    private static final int DEFAULT_START_POSITION = 1;

    private static final String KEY_GALLERY_SHOW_TIME = "gallery_show_clock";
    private static final boolean DEFAULT_GALLERY_SHOW_TIME = true;

    private static final String KEY_GALLERY_SHOW_BATTERY = "gallery_show_battery";
    private static final boolean DEFAULT_GALLERY_SHOW_BATTERY = true;

    private static final String KEY_CUSTOM_CODEC = "custom_codec";

    private static final String KEY_DECODE_FORMAT = "decode_format";
    private static final String DEFAULT_DECODE_FORMAT_STR = "0";
    private static final int DEFAULT_DECODE_FORMAT = 0;

    public static int getPageScalingMode() {
        return Utils.parseIntSafely(mConfigPre.getString(KEY_PAGE_SCALING,
                DEFAULT_PAGE_SCALING_STR), DEFAULT_PAGE_SCALING);
    }

    public static int getStartPosition() {
        return Utils.parseIntSafely(mConfigPre.getString(KEY_START_POSITION,
                DEFAULT_START_POSITION_STR), DEFAULT_START_POSITION);
    }

    public static boolean getGShowTime() {
        return mConfigPre.getBoolean(KEY_GALLERY_SHOW_TIME, DEFAULT_GALLERY_SHOW_TIME);
    }

    public static boolean getGShowBattery() {
        return mConfigPre.getBoolean(KEY_GALLERY_SHOW_BATTERY, DEFAULT_GALLERY_SHOW_BATTERY);
    }

    public static boolean getCustomCodec() {
        return mConfigPre.getBoolean(KEY_CUSTOM_CODEC, Utils.SUPPORT_IMAGE);
    }

    public static int getDecodeFormat() {
        return Utils.parseIntSafely(mConfigPre.getString(KEY_DECODE_FORMAT,
                DEFAULT_DECODE_FORMAT_STR), DEFAULT_DECODE_FORMAT);
    }

    /****** Download ******/

    private static final String KEY_DOWNLOAD_PATH = "download_path";
    private static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/EhViewer/download/";

    private static final String KEY_MEDIA_SCAN = "media_scan";
    private static final boolean DEFAULT_MEDIA_SCAN = false;

    private static final String KEY_DOWNLOAD_THREAD = "download_thread";
    private static final String DEFAULT_DOWNLOAD_THREAD_STR = "3";
    private static final int DEFAULT_DOWNLOAD_THREAD = 3;

    public static boolean getMediaScan() {
        return mConfigPre.getBoolean(KEY_MEDIA_SCAN, DEFAULT_MEDIA_SCAN);
    }

    public static String getDownloadPath() {
        return mConfigPre.getString(KEY_DOWNLOAD_PATH, DEFAULT_DOWNLOAD_PATH);
    }

    public static void setDownloadPath(String path) {
        mConfigPre.edit().putString(KEY_DOWNLOAD_PATH, path).apply();
    }

    public static int getDownloadThread() {
        return Utils.parseIntSafely(mConfigPre.getString(KEY_DOWNLOAD_THREAD,
                DEFAULT_DOWNLOAD_THREAD_STR), DEFAULT_DOWNLOAD_THREAD);
    }


    /****** Advanced ******/

    private static final String KEY_HTTP_RETRY = "http_retry";
    private static final String DEFAULT_HTTP_RETRY = "3";

    private static final String KEY_HTTP_CONNECT_TIMEOUT = "http_connect_timeout";
    private static final String DEFAULT_HTTP_CONNECT_TIMEOUT = "5000";

    private static final String KEY_HTTP_READ_TIMEOUT = "http_read_timeout";
    private static final String DEFAULT_HTTP_READ_TIMEOUT = "5000";

    private static final String KEY_EH_MIN_INTERVAL = "eh_min_interval";
    private static final String DEFAULT_EH_MIN_INTERVAL = "0";

    public static int getHttpRetry() {
        try {
            return Integer.parseInt(mConfigPre.getString(KEY_HTTP_RETRY, DEFAULT_HTTP_RETRY));
        } catch(Throwable e){
            return 3;
        }
    }

    public static int getHttpConnectTimeout() {
        try {
            return Integer.parseInt(mConfigPre.getString(KEY_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_CONNECT_TIMEOUT));
        } catch(Throwable e){
            return 5000;
        }
    }

    public static int getHttpReadTimeout() {
        try {
            return Integer.parseInt(mConfigPre.getString(KEY_HTTP_READ_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT));
        } catch(Throwable e){
            return 5000;
        }
    }

    public static int getEhMinInterval() {
        try {
            return Integer.parseInt(mConfigPre.getString(KEY_EH_MIN_INTERVAL, DEFAULT_EH_MIN_INTERVAL));
        } catch(Throwable e){
            return 0;
        }
    }


    // Proxy urls
    private static final String PROXY_URLS_KEY = "proxy_urls";
    public static final String[] DEFAULT_PROXY_URLS = {
        "http://proxyy0000.appsp0t.com/proxy",
        "http://proxyy0001.appsp0t.com/proxy",
        "http://proxyy0002.appsp0t.com/proxy",
        "http://proxyy0003.appsp0t.com/proxy",
        "http://proxyy0004.appsp0t.com/proxy",
        "http://proxyy0005.appsp0t.com/proxy",
        "http://proxyy0006.appsp0t.com/proxy",
        "http://proxyy0007.appsp0t.com/proxy",
        "http://proxyy0008.appsp0t.com/proxy",
        "http://proxyy0009.appsp0t.com/proxy",
        "http://proxyy000a.appsp0t.com/proxy",
        "http://proxyy000b.appsp0t.com/proxy",
        "http://proxyy000c.appsp0t.com/proxy",
        "http://proxyy000d.appsp0t.com/proxy",
        "http://proxyy000e.appsp0t.com/proxy",
        "http://proxyy000f.appsp0t.com/proxy"
    };

    public static String[] getProxyUrls() {
        Set<String> re = mConfigPre.getStringSet(PROXY_URLS_KEY, null);
        if (re != null && re.size() > 0)
            return re.toArray(new String[re.size()]);
        else
            return DEFAULT_PROXY_URLS;
    }

    public static void setProxyUrls(String[] urls) {
        mConfigPre.edit().putStringSet(PROXY_URLS_KEY, new HashSet<String>(Arrays.asList(urls))).apply();
    }
}
