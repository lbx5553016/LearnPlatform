package com.tencent.brendanlu.learnplatform.main;

import android.os.Bundle;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.brendanlu.learnplatform.R;
import com.tencent.brendanlu.learnplatform.base.BaseActivity;
import com.tencent.mars.xlog.Log;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends BaseActivity {
	@Bind(R.id.tv_welcome) TextView tvWelcom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
	}

	public boolean onDoAction(String url){
		return true;
	}

	@Override
	public void onBackPressed() {
		long now = System.currentTimeMillis();

		if ((now - lastPressExitTime) <= MAX_EXIT_INTERVAL) {
			onAPPExit();
			Log.d(TAG,"exitBy2Click-onAPPExit");

		} else {
			onFirstBackPress(now);
			Log.d(TAG,"exitBy2Click-onFirstBackPress");
		}
	}

	private long lastPressExitTime = 0;    //双击退出
	private static final long MAX_EXIT_INTERVAL = 2000;//退出时间间隔

	private void onFirstBackPress(long now) {
		TastyToast.makeText(this, "再按一次退出腾讯直播",TastyToast.LENGTH_SHORT,TastyToast.WARNING);
		lastPressExitTime = now;
	}
	private void onAPPExit(){
		uiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "System.exit(0)");
				System.exit(0);
			}
		}, 100);
	}
}
