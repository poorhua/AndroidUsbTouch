package com.ou.thread;

import com.ou.base.BoardConfig;
import com.ou.base.Function;
import com.ou.base.HardwareSignal;
import com.ou.common.ComFunc;
import com.ou.common.Constant;

import android.os.Handler;

public class HardwareTestWorkThread extends Thread {
	private boolean bWork = true;
	private Handler mHandler;
	private Function mFunc;
	private BoardConfig mBoardConfig;

	
	

	public HardwareTestWorkThread(Handler handler, BoardConfig conf) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
		mFunc = Function.getTpUsbFunction();
		if (mFunc == null)
			bWork = false;

		mBoardConfig = conf;
	}

	private HardwareSignal fillAllSignal() {
		mFunc = Function.getTpUsbFunction();
		HardwareSignal signal = new HardwareSignal(mBoardConfig);
		for (int dir = 0; dir < Constant.LED_EMIT_DIRECTION_TOTAL_NUM; dir++) {
			byte [] bs = mFunc.readImage(dir, mBoardConfig.getTotalLedNumber());
			if (bs == null)
				return null;
			signal.setXLedSignal(dir, bs,true);
			signal.setYLedSignal(dir, bs,true);
		}
		return signal;
	}

	private void sendImage(HardwareSignal sig) {
		mHandler.obtainMessage(Constant.MSG_UPDATE_IMAGE, sig).sendToTarget();
	}
	@Override
	public void run() {
		while (bWork) {
			if (DetectUsbThread.isUsbEnable() == false) {
				bWork = false;
				break;
			}
			HardwareSignal sig= fillAllSignal();
			if (sig == null) {
				ComFunc.sleep(1);
				continue;
			}
			
			sendImage(sig);
			ComFunc.sleep(1);
		}
	}

	public void startWork() {
		bWork = true;
		start();
	}

	public void stopWork() {
		bWork = false;
	}
}
