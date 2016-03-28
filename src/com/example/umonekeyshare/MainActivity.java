package com.example.umonekeyshare;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	 private static UMSocialService mController = null;
	 public static final String DESCRIPTOR = "com.umeng.share";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mController = UMServiceFactory.getUMSocialService(DESCRIPTOR);
		findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UMShareAgent umShareAgent = UMShareAgent.getInstance(MainActivity.this);
	            umShareAgent.oneKeyShare(MainActivity.this,false,"title","content","http://123.56.162.207:8786/activity/banner2.png","https://www.baidu.com/");
	            umShareAgent.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
			}
		});
	}
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}

}
