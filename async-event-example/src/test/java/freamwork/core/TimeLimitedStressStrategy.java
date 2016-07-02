/*
 * @(#)TimeLimitedStressStrategy.java Created on 2013-7-8
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;


import freamwork.core.stat.NumberUtil;

import java.util.Properties;

public class TimeLimitedStressStrategy extends ConfigurableStressStrategy {
	private volatile long startTime;
	public TimeLimitedStressStrategy(Properties config) {
		super(config);
	}

	private TimeLimitedStressConfig tlsConfig;
	public void runStress() {
		tlsConfig = getStressConfig();
		int runMSec = tlsConfig.getTimeTestLimit() * 1000;
		final StressTask task = tlsConfig.getTaskClass();
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() < startTime + runMSec){
			doTask(task);
		}
		
	}
	
	@Override
	public TimeLimitedStressConfig getStressConfig() {
		return new TimeLimitedStressConfig(config);
	}

	public double percentageOfCompletion() {
		long now = System.currentTimeMillis();
		double percent = (now - startTime) * 100 / 1000.0 / tlsConfig.getTimeTestLimit();
		return NumberUtil.roundTo(percent, 2);
	}

	static class TimeLimitedStressConfig extends BaseStressConfig {
		public TimeLimitedStressConfig(Properties config) {
			super(config);
		}

		// 每个线程的时间(秒)
		public static final String TIME_TEST_LIMIT = "TIME_TEST_LIMIT";

		public int getTimeTestLimit() {
			return Integer.parseInt(config.getProperty(TIME_TEST_LIMIT, "10"));
		}
	}
}
