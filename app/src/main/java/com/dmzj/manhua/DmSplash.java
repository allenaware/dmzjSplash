package com.dmzj.manhua;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bayescom.sdk.BayesAdService;
import com.bayescom.sdk.BayesAdspot;
import com.bayescom.sdk.BayesSplashListener;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by allen on 2017/5/12.
 */

public class DmSplash extends RelativeLayout implements BayesAdspot,SplashADListener {
    private String adspotId;
    private String mediaId;
    private String mediaKey;
    private BayesSplashListener bl;
    private Context appContext;
    private boolean isAdReady = false;
    private BayesAdService bs;
    private Bitmap bitmap = null;
    private int waitSec = 5;
    private Timer timer;
    private TextView closeText;
    private ViewGroup skipView;
    private boolean isVideo = false;
    private String adSource;
    private ViewGroup adContainer;
    private SplashAD splashAD;

    public DmSplash(Context context, AttributeSet attrSet, String adspotId, String mediaId, String mediaKey, ViewGroup adContainer,ViewGroup skipView) {
        super(context, attrSet);
        this.adspotId = adspotId;
        this.mediaId = mediaId;
        this.mediaKey = mediaKey;
        this.appContext = context;
        this.adContainer = adContainer;
        this.skipView = skipView;
        bs = new BayesAdService(appContext, this);
    }

    public DmSplash(Context context, String adspotId, String mediaId, String mediaKey, ViewGroup adContianer, ViewGroup skipView) {
        this(context, null, adspotId, mediaId, mediaKey,adContianer,skipView);
    }

    public void adReady(Hashtable<Integer, String> texts, Hashtable<Integer, Bitmap> images, String html, Hashtable<String, String> video, String adSource) {
        this.adSource =adSource;
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
            showAd();
        if (bl != null) {
            isAdReady = true;
            bl.onAdReady();

        }

    }

    public void adFailed(String result) {
        fetchSplashAD((Activity) appContext,adContainer,skipView,Constants.APPID,Constants.SplashPosID,this,0);
//        if (bl != null) {
//            bl.onAdFailed();
//        }
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
        if (bl != null) {
            bl.onAdClose();
        }
    }

    public void setIsVideo() {
        isVideo = true;
    }

    public boolean getIsVideo() {
        return isVideo;
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

    public void setListener(BayesSplashListener bl) {
        this.bl = bl;
    }

    public void loadAd() {
        bs.loadAd();

    }

    public boolean isAdReady() {
        return isAdReady;
    }

    public void showAd() {

            ImageView iv = new ImageView(appContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageBitmap(bitmap);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bs.adDidClick(v);
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            });
            RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            adContainer.addView(iv, relLayoutParams);
            addCloseText();
            addAdSourceText(adSource);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }, 1000, 1000);

        bs.reportAdShow();

        if (bl != null) {
            bl.onAdShow();
        }
    }

    public void addCloseText() {
        closeText = new TextView(appContext);
        closeText.setText("关闭:" + waitSec + "");
        closeText.setTextColor(Color.WHITE);
        closeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.GRAY);
        gd.setCornerRadius(10);
        gd.setAlpha(100);
        closeText.setBackground(gd);
        RelativeLayout.LayoutParams rLayoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        skipView.addView(closeText, rLayoutParams);
        closeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAd();

            }
        });
    }

    public void addAdSourceText(String adSource) {
        if (adSource != null) {
            TextView adSourceText = new TextView(appContext);
            adSourceText.setText(adSource);
            adSourceText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.GRAY);
            gd.setCornerRadius(10);
            gd.setAlpha(100);
            adSourceText.setBackground(gd);
            adSourceText.setTextColor(Color.WHITE);
            LayoutParams adSourceLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            adSourceLayoutParams.setMargins(0, 0, 5, 5);
            adSourceLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            adSourceLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adContainer.addView(adSourceText, adSourceLayoutParams);
        }
    }

    public void closeAd() {
        if (bl != null) {
            bl.onAdClose();
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    waitSec = waitSec - 1;
                    if (waitSec != 0) {
                        closeText.setText("关闭:" + waitSec + "");

                    } else {
                        timer.cancel();
                        closeAd();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
        // splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
//        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
//        next();
    }
    @Override
    public void onNoAD(AdError error) {
        Log.i(
                "AD_DEMO",
                String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(),
                        error.getErrorMsg()));
        /** 如果加载广告失败，则直接跳转 */
//        this.startActivity(new Intent(this, MainActivity.class));
//        this.finish();
    }
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }
}
