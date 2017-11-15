package omark.hey;

public class UserAgent {
    public static final String safari_Windows = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50";
    public static final String safari_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50";
    public static final String safari_iOS4_iPhone = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5";
    public static final String safari_iOS4_iPodTouch = "Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5";
    public static final String safari_iOS4_iPad = "Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5";
    public static final String QQBrowser_android = "MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
 
    public static final String ie_9 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;";
    public static final String ie_8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)";
    public static final String ie_7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
    public static final String ie_6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";

    public static final String firefox_Windows = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1";
    public static final String firefox_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1";

    public static final String opera_Windows = "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11";
    public static final String opera_MAC = "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11";
    public static final String opera_Android = "Opera/9.80 (Android 2.3.4; Linux; Opera Mobi/build-1107180945; U; en-GB) Presto/2.8.149 Version/11.10";
    
    public static final String chrome_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11";

    public static final String maxthon = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)";
    public static final String theworld_2 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";
    public static final String theworld_3 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; The World)";
    
    public static final String uc = "NOKIA5700/ UCWEB7.0.2.37/28/999";
    public static final String uc_Openwave = "Openwave/ UCWEB7.0.2.37/28/999";
    public static final String uc_Opera = "Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999";
    
    
    public static String[] getU() {
        String[] s = new String[] {
            "Safari",
            "FireFox",
            "Opera",
            "Chrome",
            "TheWorld",
            "QQBrowser",
            "Maxthon",
            "UC",
            "IE",
        };
        return s;
    }
}
