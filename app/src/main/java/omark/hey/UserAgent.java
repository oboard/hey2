package omark.hey;
import android.webkit.*;

public class UserAgent {

    public static final String 
    safari_Windows = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
    safari_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
    safari_iPhone = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",
    safari_iPodTouch = "Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",
    safari_iPad = "Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",

    QQBrowser_android = "MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
    QQBrowser_meizu_metal = "Mozilla/5.0 (Linux; U; Android 5.1; zh-cn; m1 metal Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36",
    QQBrowser_oppo_find7 = "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; X9007 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36",
    QQBrowser_iphone6s = "Mozilla/5.0 (iPhone 6s; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 MQQBrowser/7.6.0 Mobile/14E304 Safari/8536.25 MttCustomUA/2 QBWebViewType/1 WKType/1",
    QQBrowser_vivo_xplay6 = "Mozilla/5.0 (Linux; U; Android 6.0.1; zh-cn; vivo Xplay6 Build/MXB48T) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36",
    QQBrowser_redmi3x = "Mozilla/5.0 (Linux; U; Android 6.0.1; zh-cn; Redmi 3X Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.2 Mobile Safari/537.36",
    QQBrowser_tencent_traveler = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; TencentTraveler 4.0; .NET CLR 2.0.50727)",
    QQBrowser_windows = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.15 Safari/535.11 QQBrowser/6.13.13719.201",

    ie_9 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;",
    ie_8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
    ie_7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
    ie_6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",

    firefox_Windows = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
    firefox_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",

    opera_Windows = "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
    opera_MAC = "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
    opera_Android = "Opera/9.80 (Android 2.3.4; Linux; Opera Mobi/build-1107180945; U; en-GB) Presto/2.8.149 Version/11.10",

    chrome_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
    chrome_Windows = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36",

    maxthon = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)",
    maxthon_ie6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; Maxthon/3.0)",
    maxthon_ie8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; Maxthon/3.0)",

    theworld_2 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
    theworld_3 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; The World)",

    uc = "NOKIA5700/ UCWEB7.0.2.37/28/999",
    uc_Openwave = "Openwave/ UCWEB7.0.2.37/28/999",
    uc_Opera = "Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999",
    uc_huawei_mate9 = "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; HUAWEI MT7-TL00 Build/HuaweiMT7-TL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.3.8.909 Mobile Safari/537.36",
    uc_sony_xperia_x = "Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.5.1.944 Mobile Safari/537.36";

    public static final String[] s = new String[] {
        "Default",
        "Safari",
        "FireFox",
        "Opera",
        "Chrome",
        "TheWorld",
        "QQBrowser",
        "Maxthon",
        "UC",
        "IE"
    };

    public static final String[][] ss = new String[][]{
        {
            "Default"
        }, {
            "Windows",
            "Mac",
            "iPhone",
            "iPodTouch",
            "iPad"
        }, {
            "Windows",
            "Mac"
        }, {
            "Windows",
            "Mac",
            "Android",
        }, {
            "Windows",
            "Mac"
        }, {
            "2",
            "3",
        }, {
            "TencentTraveler",
            "Windows",
            "Android",
            "Meizu Metal 1",
            "OPPO Find 7",
            "iPhone 6s",
            "Vivo Xplay6",
            "Redmi 3x"
        }, {
            "maxthon",
            "IE 6",
            "IE 8"
        }, {
            "Openwave",
            "Opera",
            "NOKIA 5700",
            "Huawei Mate9",
            "Sony Xperia X"
        }, {
            "6",
            "7",
            "8",
            "9",
        }
    };

    public static final String[][] ssr() {
        return new String[][] {
            {
                WebSettings.getDefaultUserAgent(Main.me)
            }, {
                safari_Windows,
                safari_MAC,
                safari_iPhone,
                safari_iPodTouch,
                safari_iPad
            }, {
                firefox_Windows,
                firefox_MAC
            }, {
                opera_Windows,
                opera_MAC,
                opera_Android,
            }, {
                chrome_Windows,
                chrome_MAC
            }, {
                theworld_2,
                theworld_3,
            }, {
                QQBrowser_tencent_traveler,
                QQBrowser_windows,
                QQBrowser_android,
                QQBrowser_meizu_metal,
                QQBrowser_oppo_find7,
                QQBrowser_iphone6s,
                QQBrowser_vivo_xplay6,
                QQBrowser_redmi3x
            }, {
                maxthon,
                maxthon_ie6,
                maxthon_ie8
            }, {
                uc_Openwave,
                uc_Opera,
                uc,
                uc_huawei_mate9,
                uc_sony_xperia_x
            }, {
                ie_6,
                ie_7,
                ie_8,
                ie_9,
            }
        };
    }
}
