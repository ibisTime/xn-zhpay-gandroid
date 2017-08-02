package com.zhenghui.zhqb.gift.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.activity.LoginActivity;


public class LogoutReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent arg1) {

		System.out.println("LogoutReceiver()-------->onReceive");

		try {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setTitle("警告");
			dialogBuilder
					.setMessage("帐号在其他设备登录,请重新登录!");
			dialogBuilder.setCancelable(false);
			dialogBuilder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MyApplication.getInstance().exit();
							Intent intent = new Intent(context, LoginActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent); // 重新启动LoginActivity
							dialog.dismiss();
						}
					});
			AlertDialog alertDialog = dialogBuilder.create();
			// 需要设置AlertDialog的类型，保证在广播接收器中可以正常弹出
			alertDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alertDialog.show();

			System.out.println("LogoutReceiver()-------->end");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
