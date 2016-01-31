package com.temp.jhostelapp;

/**
 * Created by DSM_ on 1/29/16.
 */
public class Constants {

    public final static String CHARSET_UTF8 = "UTF-8";

    public static final String URL_SERVER = "http://192.168.2.107/hostelJ";//http://hosteljthapar.comli.com/";
    public static final String URL_SERVER_LOGIN = URL_SERVER + "/login.php";
    public static final String URL_SERVER_NOTI = URL_SERVER + "/notifications.php";
    public static final int OK_CONNECT_TIMEOUT = 15; // in seconds
    public static final int OK_READ_TIMEOUT = 15; // in seconds

    //Cache files
    public static final String FILE_NOTIFICATIONS = "notifications";
    public static final String FILE_COMPLAINTS = "complaints";

}
