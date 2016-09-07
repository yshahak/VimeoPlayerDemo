package il.co.yshahak.simplevimeoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;

public class VimeoPlayerActivity extends AppCompatActivity {
    public static final String EXTRA_LINK = "extraLink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String html = getIntent().getStringExtra(EXTRA_LINK);
        if(html != null) {
            HTML5WebView mWebView = new HTML5WebView(this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setAllowFileAccess(true);
            mWebView.getSettings().setAppCacheEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setPluginState(WebSettings.PluginState.OFF);
            mWebView.getSettings().setAllowFileAccess(true);
            mWebView.loadData(html, "text/html", "utf-8");
            setContentView(mWebView.getLayout());
        }
    }
}
