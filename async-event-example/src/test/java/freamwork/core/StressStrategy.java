/*
 * @(#)StressStrategy.java Created on 2013-7-8
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

public interface StressStrategy {
	// 线程数
	String KEY_THREAD_NUM = "THREAD_NUM";
	// 交易间隔
	String KEY_TASK_PERIOD = "TASK_PERIOD";
	// 统计时间间隔
	String KEY_STAT_PERIOD = "STAT_PERIOD";
	// 任务类
	String KEY_TASK_CLASS = "TASK_CLASS";
	// 日志路径
	String KEY_RESULT_PATH = "RESULT_PATH";
	// 压力策略实现类
	String KEY_STRATEGY_CLASS = "STRATEGY_CLASS";

	void runStress();
	double percentageOfCompletion();
}
