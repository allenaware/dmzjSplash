package com.dmzj.manhua;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bayescom.sdk.BayesAdService;
import com.bayescom.sdk.BayesAdspot;
import com.bayescom.sdk.BayesBannerListener;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by allen on 2017/5/12.
 */

public class DmBanner extends RelativeLayout implements BayesAdspot {
    private String adspotId;
    private String mediaId;
    private String mediaKey;
    private BayesBannerListener bl;
    private Context appContext;
    //    private int interval = 0;
    BayesAdService bs;
    private BannerView bv;

    public DmBanner(Context context, AttributeSet attrSet, String adspotId, String mediaId, String mediaKey) {
        super(context, attrSet);
        this.adspotId = adspotId;
        this.mediaId = mediaId;
        this.mediaKey = mediaKey;
        this.appContext = context;
        bs = new BayesAdService(appContext, this);
        reloadAd();
//        if (interval == 0) {
//            interval = BayesSdkConfig.refreshInterval;
//        }
//        Timer timer = new Timer(true);
//        TimerTask reloadTask = new TimerTask() {
//            @Override
//            public void run() {
//                reloadAd();
//            }
//        };
//        timer.schedule(reloadTask, 0, interval * 1000);

    }

    public DmBanner(Context context, String adspotId, String mediaId, String mediaKey) {
        this(context, null, adspotId, mediaId, mediaKey );
    }

//    public void setRefreshInterval(int userInterval) {
//        this.interval = userInterval;
//    }

    public void adReady(Hashtable<Integer, String> texts, Hashtable<Integer, Bitmap> images, String html, Hashtable<String, String> video, String adSource) {
        this.removeAllViews();
        LayoutParams plp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        plp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.setLayoutParams(plp);
        ImageView iv = new ImageView(appContext);
        Bitmap bitmap = null;
        Enumeration<Bitmap> e = images.elements();
        while (e.hasMoreElements()) {
            bitmap = e.nextElement();
        }
        if (bitmap == null) {
            if (bl != null) {
                bl.onAdFailed();
            }
            return;
        }
        //show the adspot directly
        iv.setImageBitmap(bitmap);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(iv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //设置广告来源
        if (adSource != null) {
            TextView tv = new TextView(appContext);
            tv.setText(adSource);
            LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.setMargins(0, 0, 5, 5);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.GRAY);
            gd.setCornerRadius(10);
            gd.setAlpha(100);
            tv.setTextColor(Color.WHITE);
            tv.setBackground(gd);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            this.addView(tv, lp);
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bs.adDidClick(v);
            }
        });
        bs.reportAdShow();
        if (bl != null) {
            bl.onAdReady();
        }
        if (bl != null) {
            bl.onAdShow();
        }
    }

    public void adFailed(String result) {
//        if (bl != null) {
//            bl.onAdFailed();
//        }
        initGdtBanner();
    }

    public void adClick(String result) {
        if (bl != null) {
            bl.onAdClick();
        }

    }

    public void adReportOk(int type) {
        if (bl != null) {
            bl.onAdReportOk(type);
        }

    }

    public void adReportFailed(int type) {
        if (bl != null) {
            bl.onAdReportFailed(type);
        }

    }

    public void adClose(String result) {
        //banner never close
    }

    public void setIsVideo() {

    }

    public boolean getIsVideo() {
        //banner is not video
        return false;
    }

    public String getAdspotId() {
        return this.adspotId;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public String getMediaKey() {
        return this.mediaKey;
    }

    public void setListener(BayesBannerListener bl) {
        this.bl = bl;
    }

    public void reloadAd() {
        bs.loadAd();
//        initGdtBanner();
    }
    public void initGdtBanner()
    {
        bv = new BannerView((Activity)appContext, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
        bv.setRefresh(0);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                if(bl!=null)
                {
                    bl.onAdFailed();
                }
            }

            @Override
            public void onADReceiv() {
                if(bl!=null)
                {
                    bl.onAdReady();
                }
            }
            @Override
            public void onADExposure()
            {
                bs.reportToUrl(Constants.DmShowTrackUrl);
                if(bl!=null)
                {
                    bl.onAdShow();
                }

            }
            @Override
            public void onADClicked()
            {
                bs.reportToUrl(Constants.DmClickTrackUrl);
                if(bl!=null)
                {
                    bl.onAdClick();
                }
            }
        });
        this.addView(bv);
        /* 发起广告请求，收到广告数据后会展示数据   */
        bv.loadAD();
    }

    @Override
    public void onDetachedFromWindow() {
//        System.out.println("DETATACHED!!");

    }


}
