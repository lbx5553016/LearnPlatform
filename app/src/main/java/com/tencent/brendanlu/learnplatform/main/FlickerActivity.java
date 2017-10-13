package com.tencent.brendanlu.learnplatform.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.tencent.brendanlu.learnplatform.R;
import com.tencent.brendanlu.learnplatform.action.ActionManager;
import com.tencent.brendanlu.learnplatform.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 闪屏页面
 *
 * @author Brendanlu
 */

public class FlickerActivity extends BaseActivity {
	@Bind(R.id.iv_flick)
	ImageView ivFlick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flicker);
		ButterKnife.bind(this);
		ivFlick.setImageResource(R.drawable.flicker);
		uiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				ActionManager.doAction("lbxplatform://MainActivity",FlickerActivity.this);
				finish();

			}

		}, 500);
	}




}
