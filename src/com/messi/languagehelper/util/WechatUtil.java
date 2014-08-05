package com.messi.languagehelper.util;


public class WechatUtil {
	
	public static final String APP_ID = "wxeb69070155cec01c";
	
	/**注册到微信
	 * @param mContext
	 */
//	public static IWXAPI regToWx(Context mContext){
//		IWXAPI api = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
//		api.registerApp(APP_ID); 
//		return api;
//	}
//	
//	/**发送信息到微信联系人
//	 * @param mContext
//	 * @param api
//	 * @param text
//	 */
//	public static void sendMsgToWechat(Context mContext, String text){
//		IWXAPI api = WXAPIFactory.createWXAPI(mContext, APP_ID);
//		WXTextObject textObj = new WXTextObject();
//		textObj.text = text;
//		
//		// 用WXTextObject对象初始化一个WXMediaMessage对象
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		// 发送文本类型的消息时，title字段不起作用
//		// msg.title = "Will be ignored";
//		msg.description = text;
//		
//		// 构造一个Req
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
//		req.message = msg;
//		req.scene = SendMessageToWX.Req.WXSceneSession;
//		
//		// 调用api接口发送数据到微信
//		api.sendReq(req);
//	}
//	
//	/**响应微信的请求
//	 * @param mContext
//	 * @param api
//	 * @param text
//	 */
//	public static void respMsgToWechat(Context mContext, Bundle bundle, String text){
//		IWXAPI api = WXAPIFactory.createWXAPI(mContext, APP_ID);
//		WXTextObject textObj = new WXTextObject();
//		textObj.text = text;
//		
//		// 用WXTextObject对象初始化一个WXMediaMessage对象
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		// 发送文本类型的消息时，title字段不起作用
//		// msg.title = "Will be ignored";
//		msg.description = text;
//		
//		// 构造一个Req
//		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
//		resp.transaction = new GetMessageFromWX.Req(bundle).transaction;
//		resp.message = msg;
//		// 调用api接口发送数据到微信
//		api.sendResp(resp);
//	}
	
	public static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

}
