package org.g_okuyama.counterpro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class Chart extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.chart);

        WebView view = (WebView)findViewById(R.id.char_view);
        /*
        String url = "http://chart.apis.google.com/chart?"
            + "chs=560x260"
            + "&chd=t:10,15,4,60,45|30,23,73,24,150"
            + "&cht=lc"
            + "&chtt=Title"
            + "&chdl=button1|button2"
            + "&chxt=x,y"
            + "&chxl=0:|7:00|8:00|2002|2003|2004"
            + "&chco=ff0000,0000ff";
        view.loadUrl(url);
        */
        
        Bundle extras = getIntent().getExtras();        
        String url = extras.getString("url");
        view.loadUrl(url);
    }
}
