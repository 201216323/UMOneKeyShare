package com.example.umonekeyshare;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;


public class UMShareAgent extends PopupWindow implements View.OnClickListener {

    public static final String DESCRIPTOR = "com.umeng.share";
    private static UMSocialService mController = null;
    private Activity activity=null;
    private static UMShareAgent mUMShareAgent=null;
    private SinaShareContent sinaShareContent;
    private UMImageButtonShareItem myImageButtonWeChat,myImageButtonCircle,
            myImageButtonQQ,myImageButtonQZone,myImageButtonSina,myImageButtonOther;
    private LinearLayout ll_share_btn;
    private RelativeLayout rl_share;


    private  UMShareAgent(Activity a) {
        this.activity=a;


        initUMService();
    }


    /**
     * @Title: initUMService
     * @Description: 初始化
     * @throws
     */
    private void initUMService() {
        if (mController == null) {
            mController = UMServiceFactory.getUMSocialService(DESCRIPTOR);
            //关闭提示
            mController.getConfig().closeToast();
        }
        configPlatforms();
    }


    /**
     * @Title: getInstance
     * @Description: 获取UMShareAgent对象
     * @param @param a 当前的上下文
     * @param @return
     * @return UMShareAgent
     * @throws
     */
    public static UMShareAgent getInstance(Activity a) {
        if (mUMShareAgent == null) {
            mUMShareAgent = new UMShareAgent(a);
        }
        // 是否只有已登录用户才能打开分享选择页面
        //mController.openShare(a, false);
        return mUMShareAgent;
    }


    private Boolean isShowBtn=false;
    private WebView webView = null;
    private String copyUrl = "";
    private String title = "";
    private String content = "";
    private String imgUrl = "";
    private String shareUrl = "";


    /**
     * @Title: oneKeyShare
     * @Description: 一键分享传入的内容
     * @param @param a 当前的上下文
     * @param @param title 分享的标题
     * @param @param url 分享的内容，实际显示的就是个URL
     * @param @param imgUrl 分享的图片URL
     * @param @param shareUrl 点击后跳转目标的URL
     * @return void
     * @throws
     */
    public void oneKeyShare(Activity a,Boolean isShow,String title,String content,String imgUrl, String shareUrl) {
        if(a!=null)
            this.activity=a;
        if (title != null)
            this.title = title;
        if (content != null)
            this.content = content;
        if (imgUrl != null)
            this.imgUrl = imgUrl;
        if (shareUrl != null)
            this.shareUrl = shareUrl;
        if (isShow != null)
            this.isShowBtn = isShow;


        initView(activity,isShowBtn);


        setSharePlateform();
    }


    /**
     * @Title: oneKeyOther
     * @Description: 在友盟分享面板中添加的其他功能
     * @param @param webView 重新加载内置浏览器的内容会用�?
     * @param @param copyUrl �?要复制的网址和在浏览器中打开的网�?
     * @return void
     * @throws
     */
    public void oneKeyOther(WebView webView,String copyUrl) {
        if (webView != null)
            this.webView = webView;
        if (copyUrl != null)
            this.copyUrl = copyUrl;
    }


    /**
     * @功能描述 : 添加平台分享(新浪、QQ和QQ空间、微信和微信朋友�?)
     * @return
     */
    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加QQ、QZone平台
        addQQQZonePlatform();
        // 添加微信、微信朋友圈平台
        addWXPlatform();


        //addOtherPlatform();//在友盟分享的默认样式上添加自定义分享平台使用该方�?
        //默认分享列表中存在的平台如果�?要删除，则调用下面的代码�?
        mController.getConfig().removePlatform( SHARE_MEDIA.TENCENT);


        //设置分享列表的平台排列顺序，则使用下面的代码�?
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA);
    }




    /**
     * @功能描述 : 添加自定义平台分享
     * @return
     */
    public void addOtherPlatform() {
        CustomPlatform mCustomPlatform = new CustomPlatform("OTHER_SHARE", "其他", R.drawable.ic_launcher );
        mCustomPlatform.mGrayIcon = R.drawable.ic_launcher ;// 灰色图标id
        mCustomPlatform.mClickListener = new SocializeListeners.OnSnsPlatformClickListener() {
            // 平台点击事件，必须实现，在这里填写你的实际代�?
            @Override
            public void onClick(Context context, SocializeEntity entity,
                                SocializeListeners.SnsPostListener listener) {
                // 调用系统自带的分享平�?
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT,"�?"+ title+"�?"+" "+content);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(Intent.createChooser(intent, "分享"));
            }
        };
        //�?后把平台添加到sdk�?
        mController.getConfig().addCustomPlatform(mCustomPlatform);
    }


    /**
     * @功能描述 : 添加QQ平台分享(包括QQ和QQ空间)
     * @return
     */
    private void addQQQZonePlatform() {
        String APPID = "1105216065"; //自己公司申请的APPID
        String APPKEY = "L2RHmOZNCuvORg2G"; //自己公司申请的APPKEY 
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity ,APPID, APPKEY);
        qqSsoHandler.addToSocialSDK();


        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity ,APPID, APPKEY);
        qZoneSsoHandler.addToSocialSDK();
    }


    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传�?�appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里�?要替换成你注册的AppID
        String appID = "wx1e48313855ee1630"; //自己公司申请的appID 
        String appSecret = "4d395bee2cc7ce077773e0cc9d93da97"; //自己公司申请的appSecret 


        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(activity,appID,appSecret);
        //关闭微信提示：大�?32k 压缩图片
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();
        // 支持微信朋友�?
        UMWXHandler wxCircleHandler = new UMWXHandler(activity,appID,appSecret);
        //关闭微信朋友圈提示：大于32k 压缩图片
        wxCircleHandler.showCompressToast(false);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }




    /**
     * @功能描述 : 设置分享平台
     * @return
     */
    private void setSharePlateform() {
        // 设置新浪微博分享内容
        sinaShareContent = new SinaShareContent();
        setShareContent(sinaShareContent);


        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        setShareContent(qzone);


        // 设置分享给QQ好友的内�?
        QQShareContent qqShareContent = new QQShareContent();
        setShareContent(qqShareContent);


        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        setShareContent(weixinContent);


        //设置微信朋友圈分享内�?
        CircleShareContent circleMedia = new CircleShareContent();
        setShareContent(circleMedia);
    }


    /**
     * @功能描述 : 设置对应平台分享的内�?
     * baseShareContent �?要传入分享的平台
     * @return
     */
    private void setShareContent(BaseShareContent baseShareContent) {
        if(baseShareContent.equals(sinaShareContent)){
            baseShareContent.setTitle( title);
            //设置分享文字
            baseShareContent.setShareContent(title+" "+content+" @sina  ");
            //设置分享图片
            baseShareContent.setShareImage(new UMImage(activity,imgUrl));
            //设置点击消息的跳转URL
            baseShareContent.setTargetUrl(shareUrl);
            mController.setShareMedia(baseShareContent);
        }else{
            baseShareContent.setTitle(title);
            //设置分享文字
            baseShareContent.setShareContent(content);
            //设置分享图片
            baseShareContent.setShareImage(new UMImage(activity,imgUrl));
            //设置点击消息的跳转URL
            baseShareContent.setTargetUrl(shareUrl);
            mController.setShareMedia(baseShareContent);
        }
    }


    /**
     * @Title: initView
     * @Description: 初始化友盟分享自定义面板上的控件，设置点击事�?
     * @param @param context
     * @return void
     * @throws
     */
    @SuppressWarnings("deprecation")
    private void initView(Context context,Boolean isShowBtn) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.umeng_share_custom_board, null);


        rl_share= (RelativeLayout) rootView.findViewById(R.id.rl_share);
        rl_share.setOnClickListener(this);


        myImageButtonWeChat=(UMImageButtonShareItem) rootView.findViewById(R.id.wechat);
        myImageButtonWeChat.setOnClickListener(this);


        myImageButtonCircle=(UMImageButtonShareItem) rootView.findViewById(R.id.wechat_circle);
        myImageButtonCircle.setOnClickListener(this);


        myImageButtonQQ=(UMImageButtonShareItem) rootView.findViewById(R.id.qq);
        myImageButtonQQ.setOnClickListener(this);


        myImageButtonQZone=(UMImageButtonShareItem) rootView.findViewById(R.id.qzone);
        myImageButtonQZone.setOnClickListener(this);


        myImageButtonSina=(UMImageButtonShareItem) rootView.findViewById(R.id.sina);
        myImageButtonSina.setOnClickListener(this);


        myImageButtonOther=(UMImageButtonShareItem) rootView.findViewById(R.id.other);
        myImageButtonOther.setOnClickListener(this);


        rootView.findViewById(R.id.share_other_copyurl).setOnClickListener(this);
        rootView.findViewById(R.id.share_other_open).setOnClickListener(this);
        rootView.findViewById(R.id.share_other_reload).setOnClickListener(this);
        ll_share_btn= (LinearLayout) rootView.findViewById(R.id.ll_share_btn);
        //使用内置浏览器时，需要显示刷新，复制网页链接，在浏览器中打开等按�?
        if(isShowBtn){
            ll_share_btn.setVisibility(View.VISIBLE);
        }
        setContentView(rootView);
        rootView.getBackground().setAlpha(100);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }


    /** (�? Javadoc)
     * Title: onClick
     * Description:分享面板中对应条目的点击事件
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.wechat:
                setAnimationAndSharePlates(SHARE_MEDIA.WEIXIN,myImageButtonWeChat.getImageViewbutton(),true);
                break;
            case R.id.wechat_circle:
                setAnimationAndSharePlates(SHARE_MEDIA.WEIXIN_CIRCLE,myImageButtonCircle.getImageViewbutton(),true);
                break;
            case R.id.qq:
                setAnimationAndSharePlates(SHARE_MEDIA.QQ,myImageButtonQQ.getImageViewbutton(),true);
                break;
            case R.id.qzone:
                setAnimationAndSharePlates(SHARE_MEDIA.QZONE,myImageButtonQZone.getImageViewbutton(),true);
                break;
            case R.id.sina:
                setAnimationAndSharePlates(SHARE_MEDIA.SINA,myImageButtonSina.getImageViewbutton(),true);
                break;
            case R.id.other:
                setAnimationAndSharePlates(null,myImageButtonOther.getImageViewbutton(),false);
                break;
            case R.id.share_other_copyurl:
                if(copyUrl!=null||copyUrl!=""){
                    if (Integer.valueOf(android.os.Build.VERSION.SDK) > 10) {
                        try {
                            ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(copyUrl);
                            Toast.makeText(activity, "已复制到剪切�?",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.dismiss();
                break;
            case R.id.share_other_open:
                if(copyUrl!=null||copyUrl!=""){
                    try {
                        Uri uri = Uri.parse(copyUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(activity, "抱歉，链接地�?错误", Toast.LENGTH_SHORT).show();
                    }
                }
                this.dismiss();
                break;
            case R.id.share_other_reload:
                if(webView!=null){
                    webView.reload();
                }
                this.dismiss();
                break;
            case R.id.rl_share:
                if(this.isShowing()){
                    this.dismiss();
                }
                break;
            default:
                break;
        }
    }


    /**
     * @Title: setAnimationAndSharePlates
     * @Description: TODO设置点击的动画和动画后跳转的分享平台
     * @param @param platform 分享的平�?
     * @param @param imageView 产生动画效果的Image
     * @param @param flag 是否是友盟分享（true是友盟分享，false是调用系统默认的分享功能�?
     * @return void
     * @throws
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setAnimationAndSharePlates(final SHARE_MEDIA platform,ImageView imageView,final boolean flag) {
        PropertyValuesHolder pvhX1 = PropertyValuesHolder.ofFloat("alpha", 1f,0f, 1f);
        PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("scaleX", 1f,0, 1f);
        PropertyValuesHolder pvhZ1 = PropertyValuesHolder.ofFloat("scaleY", 1f,0, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhX1, pvhY1,pvhZ1);
        objectAnimator.setDuration(200);


        objectAnimator.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animation) {


            }


            @Override
            public void onAnimationRepeat(Animator animation) {


            }


            @Override
            public void onAnimationEnd(Animator animation) {
                if(flag==true){
                    //自定义友盟分享操�?
                    performShare(platform);
                }else{
                    //调用系统的分享面�?
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, title+" "+content);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(Intent.createChooser(intent, "分享"));
                    dismiss();
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {


            }
        });
        objectAnimator.start();
    }


    /**
     *
     * @Title: performShare
     * @Description: 自定义友盟分享操作的接口回调方法
     * @param @param platform
     * @return void
     * @throws
     */
    private void performShare(SHARE_MEDIA platform) {
        // 参数1为Context类型对象�? 参数2为要分享到的目标平台�? 参数3为分享操作的回调接口
        mController.postShare(activity, platform, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {


            }
            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = platform.toString();
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showText += "平台分享成功";
                } else {
                    showText += "平台分享失败";
                }
                Toast.makeText(activity, showText, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
