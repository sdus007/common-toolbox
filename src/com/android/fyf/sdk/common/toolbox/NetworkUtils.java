package com.android.fyf.sdk.common.toolbox;

import java.lang.reflect.Field;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

public class NetworkUtils {

    public static final int NETWORK_TYPE_UNKNOWN = -1;
    public static final int NETWORK_TYPE_UNAVAILABLE = 0;
    public static final int NETWORK_TYPE_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;
    public static final int NETWORK_TYPE_2G = 3;
    public static final int NETWORK_TYPE_3G = 4;
    public static final int NETWORK_TYPE_4G = 5;

    private static final String STRING_CMWAP = "cmwap";
    private static final String STRING_CTWAP = "ctwap";
    private static final String STRING_UNIWAP = "uniwap";
    private static final String STRING_3GWAP = "3gwap";

    public static final String CP_NONE = "CP_NONE";
    public static final String CP_WIFI = "NET_WIFI";
    public static final String CP_CHINA_MOBILE = "CHINA_MOBILE";
    public static final String CP_CHINA_UNICOM = "CHINA_UNICOM";
    public static final String CP_CHINA_TELCOM = "CHINA_TELCOM";

    public static final String STRING_G3 = "3G";
    public static final String STRING_G2 = "2G";
    public static final String STRING_G4 = "4G";
    public static final String STRING_UNKNOWN = "Unknown";
    public static final String String_UNAVAILABLE = "unavailable";

    public static final String STRING_WIFI = "WiFi";

    // other mobile network

    public static final int OPERATOR_NONE = 0;
    public static final int OPERATOR_CHINA_MOBILE = 1;
    public static final int OPERATOR_CHINA_UNICOM = 2;
    public static final int OPERATOR_CHINA_TELECOM = 3;

    private static int networkType = NETWORK_TYPE_UNKNOWN;

    public static final void initNetworkType(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 区分是WIFI网络还是移动手机网络
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if ((info != null) && info.isAvailable()) {
            if (info.getTypeName().toLowerCase(Locale.ENGLISH).equals("wifi")) {
                networkType = NETWORK_TYPE_WIFI;
            } else if (info.getTypeName().toLowerCase(Locale.ENGLISH).equals("mobile")) {
                networkType = getMobileType(context);
            }
        } else {
            networkType = NETWORK_TYPE_UNAVAILABLE;
        }
    }

    public static final int getNetworkType(Context context) {
        if (networkType == NETWORK_TYPE_UNKNOWN) {
            initNetworkType(context);
        }
        return networkType;
    }

    private static final int getMobileType(Context context) {
        int type = NETWORK_MOBILE;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                type = NETWORK_TYPE_2G; // ~ 100 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                type = NETWORK_TYPE_2G; // ~ 14-64 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                type = NETWORK_TYPE_2G; // ~ 50-100 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                type = NETWORK_TYPE_3G; // ~ 50-100 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                type = NETWORK_TYPE_3G; // ~ 400-1000 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                type = NETWORK_TYPE_3G; // ~ 600-1400 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                type = NETWORK_TYPE_3G; // ~ 2-14 Mbps
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                type = NETWORK_TYPE_3G; // ~ 700-1700 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                type = NETWORK_TYPE_3G; // ~ 1-23 Mbps
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                type = NETWORK_TYPE_3G; // ~ 400-7000 kbps
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                type = NETWORK_MOBILE;
                break;
            default:
                type = NETWORK_MOBILE;
                break;
        }
        int sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        try {
            Class<?> tempClass = Class.forName("android.telephony.TelephonyManager");
            if (sdkVersion >= 11) {

                Field fieldLTE = tempClass.getField("NETWORK_TYPE_LTE");
                if (fieldLTE != null && fieldLTE.get(null) != null
                        && fieldLTE.get(null).toString().equals(String.valueOf(telephonyManager.getNetworkType()))) {
                    type = NETWORK_TYPE_4G;
                }
                Field fieldEHRPD = tempClass.getField("NETWORK_TYPE_EHRPD");
                if (fieldEHRPD != null && fieldEHRPD.get(null) != null
                        && fieldEHRPD.get(null).toString().equals(String.valueOf(telephonyManager.getNetworkType()))) {
                    type = NETWORK_TYPE_3G;
                }

            }
            if (sdkVersion >= 9) {
                Field fieldEVDOB = tempClass.getField("NETWORK_TYPE_EVDO_B");
                if (fieldEVDOB != null && fieldEVDOB.get(null) != null
                        && fieldEVDOB.get(null).toString().equals(String.valueOf(telephonyManager.getNetworkType()))) {
                    type = NETWORK_TYPE_3G;
                }
            }
            if (sdkVersion >= 13) {
                Field fieldHSPAP = tempClass.getField("NETWORK_TYPE_HSPAP");
                if (fieldHSPAP != null && fieldHSPAP.get(null) != null
                        && fieldHSPAP.get(null).toString().equals(String.valueOf(telephonyManager.getNetworkType()))) {
                    type = NETWORK_TYPE_3G;
                }
            }
        } catch (Exception ex) {
            LogUtils.e(ex);
        }
        return type;
    }

    public static String getNetworkStringByType(int networkType) {
        String networkString = STRING_UNKNOWN;
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                networkString = String_UNAVAILABLE;
                break;
            case NETWORK_TYPE_WIFI:
                networkString = STRING_WIFI;
                break;
            case NETWORK_TYPE_2G:
                networkString = STRING_G2;
                break;
            case NETWORK_TYPE_3G:
                networkString = STRING_G3;
                break;
            case NETWORK_TYPE_4G:
                networkString = STRING_G4;
                break;
            default:
                networkString = STRING_UNKNOWN;
                break;
        }
        return networkString;
    }

    public static final boolean is2G(Context context) {
        boolean ret = false;
        if (isMobileConnected(context)) {
            ret = getMobileType(context) == NETWORK_TYPE_2G;
        }
        return ret;
    }

    public static final boolean is3G(Context context) {
        boolean ret = false;
        if (isMobileConnected(context)) {
            ret = getMobileType(context) == NETWORK_TYPE_3G;
        }
        return ret;
    }

    public static final boolean is4G(Context context) {
        boolean ret = false;
        if (isMobileConnected(context)) {
            ret = getMobileType(context) == NETWORK_TYPE_4G;
        }
        return ret;
    }

    public static final boolean isMobile(int networkType) {
        return (networkType == NETWORK_TYPE_2G || networkType == NETWORK_TYPE_3G || networkType == NETWORK_TYPE_4G || networkType == NETWORK_TYPE_UNKNOWN);
    }

    public static final boolean isWifi(int networkType) {
        return networkType == NETWORK_TYPE_WIFI;
    }

    public static final boolean isMobile(Context context) {
        return isMobile(getNetworkType(context));
    }

    public static final boolean isWifi(Context context) {
        return isWifi(getNetworkType(context));
    }

    public static final boolean isWifiConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean ret = (networkInfo != null) && networkInfo.isConnected();
        return ret;
    }

    public static final boolean isMobileConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean ret = (networkInfo != null) && networkInfo.isConnected();
        return ret;
    }

    /**
     * 判断type是否表示为NETWORK_TYPE_UNAVAILABLE
     * 
     * @param networkType
     * @return
     */
    public static final boolean isUnavailable(int networkType) {
        return networkType == NETWORK_TYPE_UNAVAILABLE;
    }

    public static final String getHostByWap(Context context) {
        String result = "";
        if (!isMobileConnected(context)) {
            return result;
        }
        ConnectivityManager mag = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != mag) {
            NetworkInfo mobInfo = mag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != mobInfo) {
                String extrainfo = mobInfo.getExtraInfo();
                if (!TextUtils.isEmpty(extrainfo)) {
                    extrainfo = extrainfo.toLowerCase(Locale.ENGLISH);
                    if (extrainfo.equals(STRING_CMWAP) || extrainfo.equals(STRING_3GWAP)
                            || extrainfo.equals(STRING_UNIWAP)) {
                        result = "10.0.0.172";
                    } else if (extrainfo.toLowerCase(Locale.ENGLISH).contains(STRING_CTWAP)) {
                        result = "10.0.0.200";
                    }
                }
            }
        }
        return result;
    }

    public static final boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /***
     * 默认上传环境为wifi
     * 
     * @param context
     * @return
     */
    public static String getNetTypeForUpload(Context context) {
        String netType = STRING_WIFI;
        if (context != null) {
            int netState = getMobileType(context);
            if (netState == NETWORK_TYPE_WIFI || netState == NETWORK_TYPE_UNKNOWN) {
                netType = STRING_WIFI;
            } else {// 2g or 3g
                String postfix = "";
                if (netState == NETWORK_TYPE_2G) {
                    postfix = "2G";
                } else if (netState == NETWORK_TYPE_3G) {
                    postfix = "3G";
                }
                TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telManager != null) {
                    String imsi = telManager.getSubscriberId();
                    if (imsi != null) {
                        StringBuilder builder = new StringBuilder();
                        if (getMobileOperator(context) != OPERATOR_NONE) {
                            netType = builder.append(imsi.substring(0, 3)).append("_").append(imsi.substring(3, 5))
                                    .append("_").append(postfix).toString();
                        }
                    }
                }
            }
        }
        return netType;
    }

    /**
     * 取得当前网络的提供者
     * 
     * @param context
     * @return
     */
    public static int getMobileOperator(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager != null) {
            String imsi = telManager.getSubscriberId();
            if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                    // 中国移动
                    return OPERATOR_CHINA_MOBILE;
                } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
                    // 中国联通
                    return OPERATOR_CHINA_UNICOM;
                } else if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
                    // 中国电信
                    return OPERATOR_CHINA_TELECOM;
                }
            }
        }
        return OPERATOR_NONE;
    }

    /**
     * 获取mac地址
     * 
     * @author xiangyutian
     * @param context
     * @return create at 2014-5-22 下午6:44:01
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        if (wifi != null) {
            info = wifi.getConnectionInfo();
        }
        String macString = "";
        if (info != null) {
            macString = info.getMacAddress();
        }
        return macString;
    }

    public static String getPhoneLAC(Context context) {
        int lac = 0;
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephonyManager != null) {
                String provider = getNetProvider(context);
                if (CP_CHINA_MOBILE.equals(provider) || CP_CHINA_UNICOM.equals(provider)) {
                    GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
                    if (location != null) {
                        lac = location.getLac();
                    }
                } else if (CP_CHINA_TELCOM.equals(provider)) {
                    CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                    if (location1 != null) {
                        lac = location1.getNetworkId();
                    }
                } else {

                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return String.valueOf(lac);
    }

    public static String getPhoneCellId(Context context) {
        int cellId = 0;
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephonyManager != null) {
                String provider = getNetProvider(context);
                if (CP_CHINA_MOBILE.equals(provider) || CP_CHINA_UNICOM.equals(provider)) {
                    GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
                    if (location != null) {
                        cellId = location.getCid();
                    }
                } else if (CP_CHINA_TELCOM.equals(provider)) {
                    CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                    if (location1 != null) {
                        cellId = location1.getBaseStationId();
                    }
                } else {

                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return String.valueOf(cellId);
    }

    public static String getPhoneWebInfo(Context context) {
        String webInfo = "";
        try {
            int type = getNetworkType(context);
            if (NETWORK_TYPE_UNAVAILABLE == type || NETWORK_TYPE_UNKNOWN == type) {
                return webInfo;
            }
            if (NETWORK_TYPE_WIFI == type) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        webInfo = wifiInfo.getSSID();
                    }
                }
            } else {
                String provider = getNetProvider(context);
                if (CP_CHINA_MOBILE.equals(provider)) {
                    webInfo = "中国移动";
                } else if (CP_CHINA_TELCOM.equals(provider)) {
                    webInfo = "中国电信";
                } else if (CP_CHINA_UNICOM.equals(provider)) {
                    webInfo = "中国联通";
                } else {

                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return webInfo;
    }

    /**
     * 取得当前网络的提供者
     * 
     * @param context
     * @return
     */
    public static String getNetProvider(Context context) {
        if (context == null) {
            return CP_NONE;
        }
        if (isWifi(context)) {
            return CP_WIFI;
        }
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager != null) {
            String imsi = telManager.getSubscriberId();
            if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                    // 中国移动
                    return CP_CHINA_MOBILE;
                } else if (imsi.startsWith("46001")) {
                    // 中国联通
                    return CP_CHINA_UNICOM;
                } else if (imsi.startsWith("46003")) {
                    // 中国电信
                    return CP_CHINA_TELCOM;
                }
            }
        }
        return CP_NONE;
    }

}
