package com.tencent.brendanlu.learnplatform.action;

/**
 * 声明跳转的一些常量
 *
 * @author Brendanlu
 */

public class ActionConst {


/**
 * 说明

 * eg. lbxplatform://XXXActivity?Data=xxx;
 * XXXActivity指示跳转的页面 后面的参数以Doget的形式出现
 */

	public static final String KActionIntent_Key = "actionUrl";				//action 在intent中传递的时候使用的key
	public static final String KActionUrlPrefix = "lbxplatform://";				//固定前缀



	public static final String KActionName_MainActivity = "MainActivity"; // 首页跳转协议的action

}

