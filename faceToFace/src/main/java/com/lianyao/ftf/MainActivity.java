//package com.lianyao.ftf;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.lianyao.ftf.sdk.MtcClient;
//import com.lianyao.ftf.sdk.inter.FTFCallback;
//
//public class MainActivity extends Activity implements FTFCallback {
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		
//		/**
//		 * 02-22 04:24:11.057: I/demo(1934): {"rsc":{"user":{"name":"15010563146","uid":"15010563146_oBNDTesLS9","aor":"15010563146_oBNDTesLS9@chinamobile.com","appid":"oBNDTesLS9","tm":{"tid":"15010563146_oBNDTesLS9_1","ttype":"1","needact":0,"token":"BaCKo","expire":"1458724359"}},"st":{"rspcode":200,"rspreason":"True"}}}--用户名：15010563146_oBNDTesLS9_1; 密码：BaCKo
//
//		 */
//		
////		new FTFService().FTF_Register("oBNDTesLS9", "15010563146", "61CCD0A5B73104866AA7091DC2160869", this);
////		new FTFService().FTF_Exit();
//		new MtcClient().FTF_Information("15010563146_oBNDTesLS9", "53Axc", this);
//		
////		new FTFService().FTF_Refresh("15010563146_oBNDTesLS9", "oBNDTesLS9", "15010563146_oBNDTesLS9_1", "61CCD0A5B73104866AA7091DC2160869", this);
//	}
//
//	@Override
//	public void onFailure(String err) {
//		Log.e("demo", err);
//	}
//
//	@Override
//	public void onResponse(String str) {
//		//注册
////		{"rsc":{"st":{"rspcode":200,"rspreason":"True"},"user":{"uid":"15010563146_oBNDTesLS9","tm":{"expire":"1458716496","token":"dCG0b","needact":0,"ttype":"1","tid":"15010563146_oBNDTesLS9_1"},"aor":"15010563146_oBNDTesLS9@chinamobile.com","name":"15010563146","appid":"oBNDTesLS9"}}}
//		
////		JSONObject json = JSONObject.parseObject(str);
////		JSONObject jsonUser = json.getJSONObject("rsc").getJSONObject("user");
////		JSONObject jsonTm = jsonUser.getJSONObject("tm");
////		String username = jsonTm.getString("tid");
////		String password = jsonTm.getString("token");
////		
////		Log.i("demo", str + "--" + "用户名：" + username + "; 密码：" + password);
//		
//		Log.i("demo", str);
//		
//		//刷新
//		//{"rsc":{"user":{"name":"15010563146","uid":"15010563146_oBNDTesLS9","aor":"15010563146_oBNDTesLS9@chinamobile.com","appid":"oBNDTesLS9","tm":{"tid":"15010563146_oBNDTesLS9_1","ttype":"1","needact":0,"token":"53Axc","expire":"1458737854"}},"st":{"rspcode":200,"rspreason":"True"}}}
//		
//		// 个人信息
//		//{"rsc":{"user":{"name":"15010563146","uid":"15010563146_oBNDTesLS9","aor":"15010563146_oBNDTesLS9@chinamobile.com","appid":"oBNDTesLS9","tm":{"tid":"15010563146_oBNDTesLS9_1","ttype":"1","needact":0,"token":"53Axc","expire":"1458737953"}},"st":{"rspcode":200,"rspreason":"True"}}}
//
//	}
//
// }
