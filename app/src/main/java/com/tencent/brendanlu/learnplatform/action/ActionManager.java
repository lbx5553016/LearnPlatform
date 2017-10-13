package com.tencent.brendanlu.learnplatform.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.brendanlu.learnplatform.base.BaseApplication;
import com.tencent.brendanlu.learnplatform.dao.Action;
import com.tencent.brendanlu.learnplatform.main.MainActivity;
import com.tencent.brendanlu.learnplatform.util.AppUtils;
import com.tencent.mars.xlog.Log;

import java.util.HashMap;


/**
 * 用来实现系统action相关的跳转。
 * @author Brendanlu
 */

public class ActionManager {

	private static final String TAG = "ActionManager";

	private static Handler mUiHandler = new Handler(Looper.getMainLooper());
	private static String sPreActionUrl = "";



	/**
	 * 从action 对象中获取目标页名称
	 *
	 * @return
	 */
	public static String getActionName(String actionUrl) {
		if (TextUtils.isEmpty(actionUrl)) {
			return null;
		}

		if(actionUrl.startsWith(ActionConst.KActionUrlPrefix)) {
			int startIndex = actionUrl.indexOf(ActionConst.KActionUrlPrefix);
			int endIndex = actionUrl.indexOf("?");
			if (endIndex == -1) {
				return actionUrl.substring(startIndex + ActionConst.KActionUrlPrefix.length());
			} else {
				return actionUrl.substring(startIndex + ActionConst.KActionUrlPrefix.length(), endIndex);
			}
			//留给特殊活动的跳转响应
		}
		return null;
	}

	/**
	 * 从action里面获取参数
	 *
	 * @param actionUrl
	 * @return
	 */
	public static HashMap<String, String> getActionParams(String actionUrl) {
		if (TextUtils.isEmpty(actionUrl)) {
			return null;
		}
		int startIndex = actionUrl.indexOf("?");
		if (startIndex == -1) {
			return null;
		}
		return getKVFromStr(actionUrl.substring(startIndex + 1));
	}

	public static HashMap<String, String> getKVFromStr(String paramsString) {
		if (TextUtils.isEmpty(paramsString)) {
			return null;
		}
		HashMap<String,String> paramsMap = new HashMap<String,String>();
		String[] keyValueString = paramsString.split("&");
		for (String string : keyValueString) {
			String[] keyValue = string.split("=");
			if (keyValue.length == 2) {
				String key = keyValue[0];
				String value = keyValue[1];
				if (TextUtils.isEmpty(key) == false && TextUtils.isEmpty(value) == false) {
					paramsMap.put(key, value);
				}
			}
		}
		return paramsMap;
	}





	/**
	 * 执行指定的action
	 *
	 * @param url
	 */
	public static void doAction(final String url, final Context context) {
		Action action = new Action();

		action.url = url;
		doAction(action, context, false, Integer.MIN_VALUE, false);
	}

	/**
	 * 执行指定的action
	 *
	 * @param action
	 */
	public static void doAction(final Action action, final Context context) {
		doAction(action,context,false, Integer.MIN_VALUE,false);
	}

	public static void doAction(final Action action, final Context context, int resuestCode) {
		doAction(action,context,false,resuestCode,false);
	}



	/**
	 * 执行指定的action
	 *
	 * @param action
	 * @param isOut 是否来自外部拉起
	 */
	public static void doAction(final Action action, final Context context, boolean isOut, final int resuestCode, boolean immediate) {
		if(!justDoAction(action,context,isOut,resuestCode,immediate))
			return;

	}




	private static boolean justDoAction(final Action action, final Context context, boolean isOut, final int resuestCode){
		return justDoAction(action,context,isOut,resuestCode,false);
	}

	private static boolean justDoAction(final Action action, final Context context, boolean isOut, final int resuestCode, boolean immediate){

		Log.i(TAG, action == null ? "doAction(null)" : "doAction:" + action.url);
		if (action != null && !TextUtils.isEmpty(action.url)) {
			TastyToast.makeText(context,action.url,TastyToast.LENGTH_SHORT,TastyToast.INFO);
		}


		//如果action、url为null或者快速点击则直接返回
		if (action == null || TextUtils.isEmpty(action.url) || (sPreActionUrl.equals(action.url) && AppUtils.isFastDoubleClick())) {
			return false;
		}
		sPreActionUrl = action.url;

		if(immediate){
			execAction(action.url,context,resuestCode);
		}else{
			mUiHandler.post(new Runnable() {

				@Override
				public void run() {
					execAction(action.url,  context, resuestCode);

				}
			});
		}
		return true;
	}

	/**
	 * 具体的执行逻辑
	 *
	 * @param actionUrl
	 */
	private static void execAction(String actionUrl, Context context, int resuestCode) {
		Log.i(TAG,"execAction actionUrl : " + actionUrl);
		if (TextUtils.isEmpty(actionUrl) || context == null) {
			return;
		}

		String actionName = getActionName(actionUrl);
		if (actionName == null) {
			return;
		}

		if (actionName.equals(ActionConst.KActionName_MainActivity)) {
			doMainActivityAction(actionUrl, context); //首页跳转
		}

		else
		{
			doDefaultAction(actionUrl);
		}
	}









	private static void doDefaultAction(String actionUrl){
		Log.i(TAG,"doDefaultAction : actionUrl = " + actionUrl);
	}


	protected static void doMainActivityAction(String actionUrl, Context context) {
		Activity topActivity = BaseApplication.getTopActivity();
		if (topActivity != null && (topActivity instanceof MainActivity)) {
			MainActivity activity = (MainActivity)topActivity;
			activity.onDoAction(actionUrl);
		} else {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ActionConst.KActionIntent_Key, actionUrl);
			context.startActivity(intent);
		}
	}



	/**
	 * 从actionUrl解析后找到key对应value
	 *
	 * @param actionUrl
	 * @return
	 */
	public static String getParamsValue(String actionUrl, String key) {
		if (TextUtils.isEmpty(actionUrl) || TextUtils.isEmpty(key)) {
			return null;
		}
		HashMap<String,String> parmsMap = getKVFromStr(actionUrl);
		if (AppUtils.isEmpty(parmsMap)) {
			return null;
		}
		return parmsMap.get(key);
	}



}
