package omark.hey;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.net.http.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.widget.*;
import java.lang.reflect.*;
import java.io.IOException;

public class HeyWeb extends WebView implements OnLongClickListener {

    public HeyApi mApi;
    public WebChromeClient mClient;

 //   private OnScrollChangedCallback mOnScrollChangedCallback;

    public HeyWeb(final Context context) {
        super(context);
        init();
    } 
	public HeyWeb(final Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }
	void init() {
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
        //webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
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
        mApi = new HeyApi(Main.pages.indexOf(this));
		//我就是强啊！兼容三个浏览器的API
        addJavascriptInterface(mApi, "hey");
        addJavascriptInterface(mApi, "via");
        addJavascriptInterface(mApi, "H5EXT");

        //设置WebClient
        setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.equals("folder://")) {
						Main.me.onDockLongClick(null);
						//Main.manager_tab_button[0].performClick();
						return true;
					} else if (url.equals("history://")) {
						Main.me.onDockLongClick(null);
						//Main.manager_tab_button[1].performClick();
						return true;
					}

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
                        return true;
                    } return super.shouldOverrideUrlLoading(view, url);
                }

                public void onPageStarted(WebView v, String url, Bitmap favicon) {
                    super.onPageStarted(v, url, favicon);
                    try {
                        Main.menus.get(Main.pages.indexOf(v)).setState(7, false);
                    } catch (Exception e) {
                        //找不到朋友
                    }
                }

                public void onPageFinished(WebView v, String url) {
                    super.onPageFinished(v, url);

                    if (!getSettings().getLoadsImagesAutomatically()) getSettings().setLoadsImagesAutomatically(true);

                    if (!Main.vmode) {
                        int hm = S.get("hm", 0) - 1;
                        long time = System.currentTimeMillis();
                        if (url.equals("about:blank") || v.getTitle().equals("about:blank")) {
                            loadUrl("javascript:document.title = 'Hey'");
                            return;
                        }
                        if (url.equals(S.get("h" + hm, "")) && v.getTitle().equals(S.get("hn" + hm, ""))) return;
                        if (url.equals(S.get("h" + hm, "")) && v.getTitle().equals(url)) return;
                        String t = S.getString(R.string.lang58);
                        //历史记录
                        if (!v.getTitle().equals(""))
                            t =  v.getTitle();
                        S.addIndexX("hm", new String[] {"h", "hn", "ht"}, new String[] {url, t, "" + time});
                    }
                }
				
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

        //设置拖拽
		//在分屏的时候拖拽EditText的内容到Hey，可以发起访问链接或搜索
        setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
					switch (dragEvent.getAction()) {
						case DragEvent.ACTION_DROP:
							String c = dragEvent.getClipData().getItemAt(0).getText().toString();
							loadUrl(HeyHelper.toWeb(c));
							setPadding(0,0,0,0);
							break;
					}
                    return true;
                }
            });

    }
	

	public void runJS(String js) {
		loadUrl("javascript:(function(){" + js + "})()");
	}
	

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
        mClient = client;
    }

    public WebChromeClient getWebChromeClient() {
        return mClient;
    }
/*
    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //普通webview
        if (mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(this, l, t);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    public interface OnScrollChangedCallback {
        void onScroll(WebView v, int l, int t);
    }
*/
    public static boolean isUri(String url) {
        return url.startsWith("http:") ||
            url.startsWith("javascript:") ||
			url.startsWith("history:") ||
			url.startsWith("folder:") ||
            url.startsWith("content:") ||
            url.startsWith("https:") ||
            url.startsWith("about:") ||
			url.startsWith("file:");
    }

    @Override
    public boolean onLongClick(final View v) {  
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
                    if (i.getTitle().equals(S.getString(R.string.lang53))) {
                    //复制链接
                        Main.clipboard.set(result.getExtra());
                   } else if (i.getTitle().equals(S.getString(R.string.lang52))) {
                        //新页面打开
                        Main.me.onDockClick(v);
                        Main.web = Main.me.addPage(result.getExtra());
                    } else if (i.getTitle().equals(S.getString(R.string.lang54))) {
                        //后台打开
                        Main.me.addPageB(result.getExtra());
                    } else if (i.getTitle().equals(S.getString(R.string.lang40)))
					//Open
                        loadUrl(result.getExtra());
                    else if (i.getTitle().equals(S.getString(R.string.lang41))) {
                        //Save
                        Intent io = new Intent(Intent.ACTION_VIEW);
                        io.setData(Uri.parse(result.getExtra()));
                        Main.me.startActivity(io);
                    }

                    return true;
                }
            });
        Main.me.multiimages.set(Main.webindex, Main.me.getWebDrawing());
        popup.show();
        return true;  
    }  
}
