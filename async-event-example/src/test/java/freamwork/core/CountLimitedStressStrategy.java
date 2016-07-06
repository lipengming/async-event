/*
 * @(#)CountLimitedStressStrategy.java Created on 2013-7-8
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;


import freamwork.core.stat.NumberUtil;

import java.util.Properties;

public class CountLimitedStressStrategy extends ConfigurableStressStrategy {
	public CountLimitedStressStrategy(Properties config) {
		super(config);
	}

	private CountLimitedStressConfig clsConfig;
	public void runStress() {
		clsConfig = getStressConfig();
		int runCounts = clsConfig.getTasksPerThread();
		final StressTask task = clsConfig.getTaskClass();
		for (int i = 0; i < runCounts; i++) {
			doTask(task);
		}
	}

	@Override
	public CountLimitedStressConfig getStressConfig() {
		return new CountLimitedStressConfig(this.config);
	}
	
	public double percentageOfCompletion() {
		long processed = getStatistical().getProcessed();
		int all = clsConfig.getThreadNum() * clsConfig.getTasksPerThread();
		double percent = processed * 100 / 1.0 / all;
		return NumberUtil.roundTo(percent, 2);
	}

	static class CountLimitedStressConfig extends BaseStressConfig{
		public CountLimitedStressConfig(Properties config) {
			super(config);
		}

		// 첼몸窟넋돨훨蛟鑒
		private static final String TASK_PER_THREAD_KEY = "TASK_PER_THREAD";
		
		public int getTasksPerThread() {
			return Integer.parseInt(config.getProperty(TASK_PER_THREAD_KEY, "1000"));
		}
	}
}
