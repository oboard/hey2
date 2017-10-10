package omark.hey;

import android.webkit.WebView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.os.Build;
import java.lang.reflect.Method;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.AbsoluteLayout;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View.OnLongClickListener;
import android.webkit.DownloadListener;
import java.util.ArrayList;
import android.animation.ValueAnimator;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.content.ClipboardManager;
import android.webkit.WebResourceResponse;
import android.webkit.ValueCallback;

public class HeyWeb extends WebView implements OnLongClickListener {
    public static String htmlSource;
    public static ImageView iv;
    public static String lurl = "";

    // public static Boolean canScrollreload = true;
    //重定义webview
    private OnScrollChangedCallback mOnScrollChangedCallback;

    public HeyWeb(final Context context) {
        super(context);
        init();
    } public HeyWeb(final Context context, AttributeSet attr) {
        super(context, attr);
        init();
    } void init() {
        //一堆WebView设置
        WebSettings websettings = getSettings();

        websettings.setAllowFileAccess(true);
        websettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= 16) {
            websettings.setAllowFileAccessFromFileURLs(true);
            websettings.setAllowUniversalAccessFromFileURLs(true);
        }
        websettings.setDomStorageEnabled(true);
        websettings.setSavePassword(true);
        websettings.setSaveFormData(true);
        websettings.setBuiltInZoomControls(true);

        //AppCache
        websettings.setAppCacheEnabled(true);
        websettings.setAppCachePath(getContext().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
        websettings.setAppCacheMaxSize(5 * 1024 * 1024);

        //Database
        websettings.setDatabaseEnabled(true);
        websettings.setDatabasePath(getContext().getApplicationContext().getDir("db", Context.MODE_PRIVATE).getPath());

        //启用地理定位  
        websettings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        websettings.setGeolocationDatabasePath(getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath());

        websettings.setSupportZoom(true);
        websettings.setDisplayZoomControls(false);
        websettings.setUseWideViewPort(true);
        websettings.setJavaScriptEnabled(true);
        //websettings.setJavaScriptCanOpenWindowsAutomatically(true);
        websettings.setPluginState(WebSettings.PluginState.ON);
        websettings.setLoadWithOverviewMode(true);
        websettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        websettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        websettings.setDefaultTextEncodingName("UTF-8");

        //图片加载优化
        if (Build.VERSION.SDK_INT >= 19) {
            websettings.setLoadsImagesAutomatically(true);
        } else {
            websettings.setLoadsImagesAutomatically(false);
        }

        if (Build.VERSION.SDK_INT < 19) {
            try {
                Class class_ = websettings.getClass();
                Class[] arrclass = new Class[]{Integer.TYPE};
                Method method = class_.getMethod("setPageCacheCapacity", arrclass);
                Object[] arrobject = new Object[]{5};
                method.invoke((Object)websettings, arrobject);
            } catch (Exception var8_11) {
                var8_11.printStackTrace();
            }
        }

        //设置WebClient
        setWebViewClient(new WebViewClient() {
                //file update

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    HitTestResult hitTestResult = view.getHitTestResult();

                    if ((url.startsWith("http")) || (url.startsWith("file") || (url.startsWith("content")))) {
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

                public void onPageFinished(WebView v, String url) {
                    super.onPageFinished(v, url);
                    
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
                    loadDataWithBaseURL(null, errorCode + "\n" + description + "\n" + failingUrl, "text/html", "utf-8", null);
                }
            });

        //设置长按
        setOnLongClickListener(this);

        //设置下载
        setDownloadListener(new DownloadListener() {   
                @Override  
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                }
            });
    }


    public void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        //MainActivity.com.startActivityForResult(Intent.createChooser(i, "Image Chooser"), 233);
    }

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

    @Override public boolean onLongClick(View v) {  
        final HitTestResult result = ((WebView)v).getHitTestResult();  
        if (null == result)  
            return false;  

        int type = result.getType();  
        if (type == WebView.HitTestResult.UNKNOWN_TYPE) return false;  

        if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) return true;
        PopupMenu popup = new PopupMenu(getContext(), Main.popn);//第二个参数是绑定的那个view
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem i) {
                    if (i.getTitle().equals(getContext().getString(R.string.dktp))) 
                        loadUrl(result.getExtra());
                    else 
                        Toast.makeText(getContext(), getContext().getString(R.string.wtf), Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
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
                //case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                //填充菜单
                popup.getMenuInflater().inflate(R.menu.link, popup.getMenu());
                //绑定菜单项的点击事件
                /*popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem i) {
                            if (i.getTitle().equals(getContext().getString(R.string.fzlj)))  //复制链接
                                MainActivity.Clipboard.setText(result.getExtra());
                            else if (i.getTitle().equals(getContext().getString(R.string.xymdk))) {
                                //新页面打开
                                MainActivity.webaddbutton.performClick();
                                MainActivity.webholder.loadUrl(result.getExtra());
                            }
                            return true;
                        }
                    });*/
                //显示(这一行代码不要忘记了)
                popup.show();
                break;
            case WebView.HitTestResult.IMAGE_TYPE:  
                //填充菜单
                popup.getMenuInflater().inflate(R.menu.photo, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem i) {
                            if (i.getTitle().equals(getContext().getString(R.string.dktp))) 
                                loadUrl(result.getExtra());

                            return true;
                        }
                    });
                //显示(这一行代码不要忘记了)
                popup.show();
                break;  
            default:  
                break;  
        }  
        return true;  
    }  
}
