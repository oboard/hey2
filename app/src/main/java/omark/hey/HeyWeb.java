package omark.hey;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupMenu;
import android.widget.Toast;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class HeyWeb extends WebView implements OnLongClickListener {
    public static String htmlSource;
    public static ArrayList<String> images = new ArrayList<String>();

    // 获取 img 标签正则
    private static final String IMAGE_URL_TAG = "<img.*src=(.*?)[^>]*?>";
    // 获取 src 路径的正则
    private static final String IMAGE_URL_CONTENT = "http:\"?(.*?)(\"|>|\\s+)";

    //private OnScrollChangedCallback mOnScrollChangedCallback;

    public HeyWeb(final Context context) {
        super(context);
        init();
    } public HeyWeb(final Context context, AttributeSet attr) {
        super(context, attr);
        init();
    } void init() {
        //一堆WebView设置
        WebSettings webSettings = getSettings();

        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setBuiltInZoomControls(true);

        //AppCache
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getContext().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
        webSettings.setAppCacheMaxSize(5 * 1024 * 1024);

        //Database
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(getContext().getApplicationContext().getDir("db", Context.MODE_PRIVATE).getPath());

        //启用地理定位  
        webSettings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath());

        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        webSettings.setCacheMode(webSettings.LOAD_DEFAULT);
        webSettings.setDefaultTextEncodingName("UTF-8");

        //图片加载优化
        if (Build.VERSION.SDK_INT >= 19)
            webSettings.setLoadsImagesAutomatically(true);
        else
            webSettings.setLoadsImagesAutomatically(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            webSettings.setMixedContentMode(webSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        if (Build.VERSION.SDK_INT < 19) {
            try {
                Class class_ = webSettings.getClass();
                Class[] arrclass = new Class[]{Integer.TYPE};
                Method method = class_.getMethod("setPageCacheCapacity", arrclass);
                Object[] arrobject = new Object[]{5};
                method.invoke((Object)webSettings, arrobject);
            } catch (Exception var8_11) {
                var8_11.printStackTrace();
            }
        }

        //HeyApi
        addJavascriptInterface(new HeyApi(), "Context");
        addJavascriptInterface(new HeyApi(), "H5EXT");

        //设置WebClient
        setWebViewClient(new WebViewClient() {
                //file update

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    HitTestResult hitTestResult = view.getHitTestResult();

                    if (isUri(url)) {
                        if (hitTestResult == null) {
                            view.loadUrl(url);
                            return true;
                        }
                    } else {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            getContext().startActivity(intent);
                        } catch (Exception e) {}
                        Toast.makeText(getContext(), url, Toast.LENGTH_SHORT);
                    } return super.shouldOverrideUrlLoading(view, url);   
                }

                //public void onPageStarted(WebView v, String url, Bitmap favicon) {
                //    super.onPageStarted(v, url, favicon);
                //}

                public void onPageFinished(WebView v, String url) {
                    super.onPageFinished(v, url);

                    //历史记录
                    S.addIndex("hnm" , "hn", v.getTitle());
                    S.addIndex("hm" , "h", url);
                    S.addIndex("htm" , "ht", "" + System.currentTimeMillis());
                    
                    if (!getSettings().getLoadsImagesAutomatically()) getSettings().setLoadsImagesAutomatically(true);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    //handler.cancel(); // Android默认的处理方式
                    handler.proceed(); // 接受所有网站的证书
                    //handleMessage(Message msg); // 进行其他处理
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    //loadDataWithBaseURL(null, errorCode + "\n" + description + "\n" + failingUrl, "text/html", "utf-8", null);
                }
            });

        //设置长按
        setOnLongClickListener(this);

        //设置下载
        setDownloadListener(new DownloadListener() {   
                @Override  
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    Main.me.startActivity(i);
                }
            });
    }

    public void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        //MainActivity.com.startActivityForResult(Intent.createChooser(i, "Image Chooser"), 233);
    }
    /*
     @Override protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
     super.onScrollChanged(l, t, oldl, oldt);
     //普通webview
     if (mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(this, l, t);
     } public OnScrollChangedCallback getOnScrollChangedCallback() {
     return mOnScrollChangedCallback;
     } public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
     mOnScrollChangedCallback = onScrollChangedCallback;
     } public interface OnScrollChangedCallback {
     void onScroll(WebView v, int l, int t);
     }
     */


    /***
     * 获取页面所有图片对应的地址对象，
     * 例如 <img src="http://sc1.hao123img.com/data/f44d0aab7bc35b8767de3c48706d429e" />
     * @param HTML WebView 加载的 HTML 文本
     * @return
     */
    private ArrayList<String> getAllImageUrlFromHtml(String html) {
        Matcher matcher = Pattern.compile(IMAGE_URL_TAG).matcher(html);
        ArrayList<String> listImgUrl = new ArrayList<String>();
        while (matcher.find()) {
            listImgUrl.add(matcher.group());
        }
        //从图片对应的地址对象中解析出 src 标签对应的内容
        getAllImageUrlFormSrcObject(listImgUrl);
        return listImgUrl;
    } private ArrayList<String> getAllImageUrlFormSrcObject(ArrayList<String> listImageUrl) {
        ArrayList<String> listImgSrc = new ArrayList<String>();
        for (String image : listImageUrl) {
            Matcher matcher = Pattern.compile(IMAGE_URL_CONTENT).matcher(image);
            while (matcher.find()) {
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listImgSrc;
    }

    public static boolean isUri(String url) {
        return (url.startsWith("http")) || url.startsWith("file") || (url.startsWith("content") || (url.startsWith("javascript")));
    }

    @Override public boolean onLongClick(final View v) {  
        final HitTestResult result = ((WebView)v).getHitTestResult();  
        if (null == result) return false;  

        int type = result.getType();
        if (type == WebView.HitTestResult.UNKNOWN_TYPE) return false;  

        if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) return true;
        PopupMenu popup = new PopupMenu(getContext(), Main.popn);//第二个参数是绑定的那个view
        switch (type) {
                //case WebView.HitTestResult.PHONE_TYPE:  
                // 处理拨号  
                //    break;  
                // case WebView.HitTestResult.EMAIL_TYPE:  
                // 处理Email  
                //    break;  
                //case WebView.HitTestResult.GEO_TYPE:  
                //    break;  
                //case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                //    break;  
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                //填充菜单
                popup.getMenuInflater().inflate(R.menu.link, popup.getMenu());
                break;
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                //填充菜单
                popup.getMenuInflater().inflate(R.menu.link, popup.getMenu());
                break;
            case WebView.HitTestResult.IMAGE_TYPE:  
                //填充菜单
                popup.getMenuInflater().inflate(R.menu.photo, popup.getMenu());
                break;  
            default:
                return true; 
        }
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem i) {
                    if (i.getTitle().equals(getContext().getString(R.string.lang53)))  //复制链接
                        Main.clipboard.set(result.getExtra());
                    else if (i.getTitle().equals(getContext().getString(R.string.lang52))) {
                        //新页面打开
                        Main.me.onDockClick(v);
                        Main.web = Main.me.addPage(result.getExtra());
                    } else if (i.getTitle().equals(getContext().getString(R.string.lang54))) {
                        //后台打开
                        Main.me.addPageB(result.getExtra());
                    } else if (i.getTitle().equals(getContext().getString(R.string.lang40))) 
                        loadUrl(result.getExtra());

                    return true;
                }
            });
        Main.me.multiimages.set(Main.webindex, Main.me.getWebDrawing());
        popup.show();
        return true;  
    }  
}
