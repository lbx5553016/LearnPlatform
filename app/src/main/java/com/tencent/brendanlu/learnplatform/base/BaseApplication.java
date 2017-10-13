package com.tencent.brendanlu.learnplatform.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.tencent.brendanlu.learnplatform.BuildConfig;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import java.lang.ref.SoftReference;
import java.util.Stack;

import static com.tencent.brendanlu.learnplatform.util.AppConst.PUB_KEY;

/**
 * 基础的application 对其进行整合
 * @author Brendanlu
 */

public class BaseApplication  extends Application {

	private String TAG = "BroadcastApplication";


	private static BaseApplication mInstance;

	private static Stack<SoftReference<Activity>> mActivityStack = new Stack<SoftReference<Activity>>();


	public static Context mContext = null;

	public Activity mKeepAliveActivity;

	public static Handler sHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof Runnable) {
				((Runnable) msg.obj).run();
			}
			super.handleMessage(msg);
		}
	};

	private boolean mAppProc = false;
	private String processName;



	/**
	 * 返回当前进程是否App主进程
	 *
	 * @return
	 */
	public boolean isAppProcess() {
		return mAppProc;
	}

	public void setMainAppProc() {
		mAppProc = true;
		processName = getAppContext().getPackageName();
	}

	public String getProcessName() {
		return processName;
	}


	public static void post(Runnable runnable) {
		sHandler.post(runnable);
	}

	public static void postDelayed(Runnable runnable, long delayMillis) {
		sHandler.postDelayed(runnable, delayMillis);
	}

	public static void removeCallbacks(Runnable runnable) {
		sHandler.removeCallbacks(runnable);
	}



	public void finishKeepAliveActivity() {
		if (mKeepAliveActivity != null) {
			mKeepAliveActivity.finish();
		}
	}

	public static  boolean DEVELOPER_MODE = false;

	@Override
	public void onCreate() {

		//采用严苛模式
		if (DEVELOPER_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectNetwork()   // or .detectAll() for all detectable problems
					.penaltyLog()
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects()
					.detectLeakedClosableObjects()
					.penaltyLog()
					.penaltyDeath()
					.build());
		}

		super.onCreate();
		mContext = this;
		mInstance = this;

		System.loadLibrary("stlport_shared");
		System.loadLibrary("marsxlog");
		final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
		final String logPath = SDCARD + "/LearnPlatform/log";

// this is necessary, or may cash for SIGBUS
		final String cachePath = this.getFilesDir() + "/xlog";

//init xlog
		if (BuildConfig.DEBUG) {
			Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", PUB_KEY);
			Xlog.setConsoleLogOpen(true);

		} else {
			Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", PUB_KEY);
			Xlog.setConsoleLogOpen(false);
		}

		Log.setLogImp(new Xlog());

	}



	public static BaseApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

	}

	// 以下用户管理整个应用的activity

	public static Activity getTopActivity() {

		if (!mActivityStack.isEmpty()) {
			Activity activity = mActivityStack.peek().get();
			if (activity != null) {
				return activity;
			}
		}
		return null;


	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		mContext = this;

		//支持使用multidex
		MultiDex.install(base);



	}

	public void exitAplication() {

		finishAllActivity();
		ActivityManager activityMgr = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityMgr.killBackgroundProcesses(this.getPackageName());
		Process.killProcess(Process.myPid());

	}

	public void activityCreated(Activity activity) {
		if (activity == null) {
			return;
		}
		SoftReference<Activity> softActivity = new SoftReference<Activity>(
				activity);
		mActivityStack.push(softActivity);
	}

	public void activityDestroyed(final Activity activity) {
		if (activity == null || mActivityStack.isEmpty() == true) {
			return;
		}
		// 可能被垃圾回收了
		if (mActivityStack.peek().get() != activity) {
			return;
		}
		mActivityStack.pop();

		// 坑，有可能出现两个LiveContainerActivity实例
//		if (mActivityStack != null && mActivityStack.size() > 0) {
//			SoftReference<Activity> softReference = mActivityStack.peek();
//			if (activity instanceof LiveContainerActivity && softReference != null) {
//				final Activity activity1 = softReference.get();
//				if (activity1 != null && activity1 instanceof LiveContainerActivity) {
//					final LiveContainerActivity containerActivity = (LiveContainerActivity) activity1;
//					LooperHelper.runOnMainLooper(new Runnable() {
//						@Override
//						public void run() {
//							if (containerActivity != null && !containerActivity.isDestroyed()) {
//								containerActivity.finish();
//							}
//						}
//					});
//				}
//			}
//		}
	}

	public void finishAllActivity() {
		while (mActivityStack.isEmpty() == false) {
			Activity activity = mActivityStack.pop().get();
			if (activity != null) {
				activity.finish();
			}
		}
		mActivityStack.clear();
	}

	public void finishStackActivity() {
		Activity activityTop = null;
		if (!mActivityStack.isEmpty()) {
			activityTop = mActivityStack.pop().get();
		} else {
			return;
		}

		while (mActivityStack.isEmpty() == false) {
			Activity activity = mActivityStack.pop().get();
			if (activity != null) {
				activity.finish();
			}
		}
		mActivityStack.clear();
		mActivityStack.push(new SoftReference<Activity>(activityTop));
	}

	/**
	 * 获取全局的appContext
	 *
	 * @return
	 */
	public static Context getAppContext() {
		return mContext;
	}


}
