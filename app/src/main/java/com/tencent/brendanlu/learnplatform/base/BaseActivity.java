package com.tencent.brendanlu.learnplatform.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Brendanlu
 */

public class BaseActivity extends AppCompatActivity {

	protected final String TAG = "BaseActivity";

	private boolean mDestroyed;

	public static final String ACTIVITY_ORIGINAL_FROM="original_from";

	public static final String ACTIVITY_ORIGINAL_FROM_CLASS_NAME="original_from_class_name";

	public Handler uiHandler = new Handler(Looper.getMainLooper());


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		// 管理堆
		BaseApplication.getInstance().activityCreated(this);


		mDestroyed = false;
	}

	protected void onStart() {

		super.onStart();

	}

	protected void onStop() {

		super.onStop();

	}

	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().activityDestroyed(this);
		mDestroyed = true;
	}

	@Override
	public boolean isDestroyed() {
		if (android.os.Build.VERSION.SDK_INT < 17) {
			return mDestroyed;
		} else {
			return super.isDestroyed();
		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}



	//主动调用finish
	@Override
	public void finish() {
		super.finish();
		BaseApplication.getInstance().activityDestroyed(this);
	}


}
