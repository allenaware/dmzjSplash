package com.dmzj.manhua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.bayescom.sdk.BayesBannerListener;
import com.bayescom.sdk.BayesSdkConfig;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.dm_ad_banner_container);

        DmBanner dmBanner = new DmBanner(this, "10000396", "100171", "e1d0d3aaf95d3f1980367e75bc41141d");

        RelativeLayout.LayoutParams rbl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rbl.addRule(RelativeLayout.CENTER_HORIZONTAL);

        rl.addView(dmBanner, rbl);
        dmBanner.setListener(new BayesBannerListener() {
            @Override
            public void onAdReady() {
                System.out.println("get Ad Ready");

            }

            @Override
            public void onAdShow() {

                System.out.println("get Ad Show");
            }

            @Override
            public void onAdClick() {
                System.out.println("get Ad Click");

            }

            @Override
            public void onAdFailed() {

                System.out.println("get Ad Failed");
            }

            @Override
            public void onAdReportOk(int type) {

                String typeDes = BayesSdkConfig.getURLDesByType(type);
                System.out.println(typeDes + " ok");
            }

            @Override
            public void onAdReportFailed(int type) {
                String typeDes = BayesSdkConfig.getURLDesByType(type);
                System.out.println(typeDes + " failed");

            }
        });
    }
}
