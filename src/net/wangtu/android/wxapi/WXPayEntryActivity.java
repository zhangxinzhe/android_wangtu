package net.wangtu.android.wxapi;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.util.pay.WechatPayUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		//Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();

//		switch (req.getType()) {
//			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//				goToGetMsg();
//				break;
//			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//				goToShowMsg((ShowMessageFromWX.Req) req);
//				break;
//			case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//				//Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
//				break;
//			default:
//				break;
//		}
	}

	@Override
	public void onResp(BaseResp resp) {
		WechatPayUtil.payCallBack(resp);
		finish();
	}
}