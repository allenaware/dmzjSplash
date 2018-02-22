package com.dmzj.manhua;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bayescom.sdk.BayesSdkConfig;
import com.bayescom.sdk.BayesSplashListener;



public class DmSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm_splash);
        RelativeLayout adContainer = this.findViewById(R.id.dm_ad_container);
        TextView skipView = this.findViewById(R.id.dm_ad_skip_view);
        final DmSplash dmSplash = new DmSplash(this,"10000559","100255","757d5119466abe3d771a211cc1278df7",adContainer,skipView);
        dmSplash.setListener(new BayesSplashListener() {
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
                getHome();
            }
            @Override
            public void onAdReportOk(int type) {

                String typeDes = BayesSdkConfig.getURLDesByType(type);
                System.out.println(typeDes+" ok");
            }

            @Override
            public void onAdReportFailed(int type) {
                String typeDes = BayesSdkConfig.getURLDesByType(type);
                System.out.println(typeDes+" failed");

            }
            @Override
            public  void  onAdClose()
            {
                System.out.println("get Ad close");
                getHome();
            }

        });
        dmSplash.loadAd();


    }
    public void getHome()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
