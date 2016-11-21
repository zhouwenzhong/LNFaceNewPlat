/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.lianyao.ftf.sdk.webrtc;

import java.util.concurrent.Executor;

import android.os.Handler;
import android.os.Looper;

import com.lianyao.ftf.sdk.uitl.MtcLog;

/**
 * Looper based executor class.
 */
public class WebrtcLooper extends Thread implements Executor {
	private static final String TAG = "LooperExecutor";
	// Object used to signal that looper thread has started and Handler instance
	// associated with looper thread has been allocated.
	private final Object looperStartedEvent = new Object();
	private Handler handler = null;
	private boolean running = false;
	private long threadId;

	@Override
	public void run() {
		Looper.prepare();
		synchronized (looperStartedEvent) {
			MtcLog.d(TAG, "Looper thread started.");
			handler = new Handler();
			threadId = Thread.currentThread().getId();
			looperStartedEvent.notify();
		}
		Looper.loop();
	}

	public synchronized void requestStart() {
		if (running) {
			return;
		}
		running = true;
		handler = null;
		start();
		// Wait for Hander allocation.
		synchronized (looperStartedEvent) {
			while (handler == null) {
				try {
					looperStartedEvent.wait();
				} catch (InterruptedException e) {
					MtcLog.e(TAG, "Can not start looper thread");
					running = false;
				}
			}
		}
	}

	public synchronized void requestStop() {
		if (!running) {
			return;
		}
		running = false;
		handler.post(new Runnable() {
			@Override
			public void run() {
				Looper.myLooper().quit();
				MtcLog.d(TAG, "Looper thread finished.");
			}
		});
	}

	// Checks if current thread is a looper thread.
	public boolean checkOnLooperThread() {
		return (Thread.currentThread().getId() == threadId);
	}

	@Override
	public synchronized void execute(final Runnable runnable) {
		if (!running) {
			MtcLog.w(TAG, "Running looper executor without calling requestStart()");
			return;
		}
		if (Thread.currentThread().getId() == threadId) {
			runnable.run();
		} else {
			handler.post(runnable);
		}
	}

}
