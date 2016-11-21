package com.lianyao.ftf.sdk.inter;

import com.lianyao.ftf.sdk.config.CallStats;

/**
 * 通话统计信息监听
 */
public interface CallStatsObserver {
	/**
	 * 通话统计信息监听
	 * @param callStats
	 */
	public void onCallStats (CallStats callStats) ;
}
