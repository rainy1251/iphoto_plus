package com.iphoto.plus.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class WebUtil {

    private static Context mContext;
    private static RelativeLayout webParentView;
    private static View mErrorView; //加载错误的视图
    public WebUtil(Context ctx, RelativeLayout webParentView) {
        super();
        this.mContext = ctx;
        this.webParentView = webParentView;
//        initErrorPage();
    }
    /**
     * 用来控制字体大小
     */
    private int fontSize = 1;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setListViewData(Context context, String url, WebView webView, final ProgressBar progress) {
        mContext = context;
        if (url == null) {

            return;
        }
        Log.i("wch",url);
//        //执行JavaScript脚本

        //得到一个WebView的设置对象,
        WebSettings setting = webView.getSettings();

        setting.setJavaScriptEnabled(true);
        //setSupportZoom:使WebView允许网页缩放,记住这个方法前,要有让WebView支持JavaScript的设定,否则会不起作用
        setting.setSupportZoom(true);
        //打开页面时， 自适应屏幕：
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDisplayZoomControls(false);
        setting.setDomStorageEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);

        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        //允许cookie 不然有的网站无法登陆
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);
        mCookieManager.setAcceptThirdPartyCookies(webView, true);
        webView.setSaveEnabled(true);
        webView.loadUrl(url);
//        webView.addJavascriptInterface(new JSInterface(),"window");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    intent.setData(Uri.parse("tel:400-021-0916"));
                    mContext.startActivity(intent);
                    view.reload();
                    return false;
                }


                if (!url.contains("text/plain")){

                    view.loadUrl(url);
                }
                return true;
//                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                showErrorPage();//显示错误页面
            }
        });



//触摸焦点起作用,如果不设置则在点击网页文本输入框的时候不能弹出软键盘及一些点击事件
        webView.requestFocus();
        //该事件是指UI界面发生改变时进行监听
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (textView != null) {
                    textView.setText(view.getTitle());
                }
                //通过代码让ProgressBar显示出来
                if (progress != null) {
                    //对ProgressBar设置加载进度的参数
                    //通过代码让ProgressBar显示出来
                    progress.setVisibility(View.VISIBLE);
                    //对ProgressBar设置加载进度的参数
                    progress.setProgress(newProgress);
                    if (newProgress == 100) {
                        //如果ProgressBar加载到100,就让他隐藏

                        progress.setVisibility(View.GONE);
                    }
                }

                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Log.d("message", message);
//                Toast.makeText(ctx, message+"0000"+url, Toast.LENGTH_SHORT).show();
                if (message != null) {

                }
                result.cancel();
                return true;

            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.contains("404")){

                }
            }
        });


    }
    private static TextView textView;
    public void setTitle(TextView tvTitle) {
        this.textView = tvTitle;
    }



}
