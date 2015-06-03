package com.sky.cookbooksa.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class ShowDialog {

	public ShowDialog(){

	}

	public static AlertDialog showAlert(final Context context, final String msg, final String title) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String content) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setMessage(content);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final int msgId, final int titleId) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final int msgId, final int titleId, final DialogInterface.OnClickListener l) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton("确定", l);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String title, final String msg, final DialogInterface.OnClickListener l) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", l);
		builder.setNegativeButton("取消", null);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context,final String msg, String ok, String cancel, final DialogInterface.OnClickListener l) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", l);
		builder.setNegativeButton("取消", null);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final int msgId, final int titleId, final DialogInterface.OnClickListener lOk, final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton("确定", lOk);
		builder.setNegativeButton("取消", lCancel);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final int msg, final int title, final int yes, final int no, final DialogInterface.OnClickListener lOk,
			final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(yes, lOk);
		builder.setNegativeButton(no, lCancel);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String msg, final String title, final DialogInterface.OnClickListener lOk, final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", lOk);
		builder.setNegativeButton("取消", lCancel);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String msg, final String title, final String yes, final String no, final DialogInterface.OnClickListener lOk,
			final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		//		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(yes, lOk);
		builder.setNegativeButton(no, lCancel);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final View view, final DialogInterface.OnClickListener lOk) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		builder.setPositiveButton("确定", lOk);
		builder.setNegativeButton("取消", null);
		// builder.setCancelable(true);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String title, final View view, final DialogInterface.OnClickListener lOk) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton("确定", lOk);
		// builder.setCancelable(true);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String title, final View view, final String ok, final String cancel, final DialogInterface.OnClickListener lOk,
			final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton(ok, lOk);
		builder.setNegativeButton(cancel, lCancel);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String title, final String ok, final View view, final DialogInterface.OnClickListener lOk) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton(ok, lOk);
		// builder.setCancelable(true);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String title, final String msg, final View view, final DialogInterface.OnClickListener lOk,
			final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setView(view);
		builder.setPositiveButton("确定", lOk);
		builder.setNegativeButton("取消", lCancel);
		// builder.setCancelable(true);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (lCancel != null) {
					lCancel.onClick(dialog, 0);
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static AlertDialog showAlert(final Context context, final String title, final View view, final DialogInterface.OnCancelListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		// builder.setCancelable(true);
		builder.setOnCancelListener(lCancel);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static ProgressDialog showDialog(Context context, String strText) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage(strText);
		dialog.show();
		return dialog;
	}
}
