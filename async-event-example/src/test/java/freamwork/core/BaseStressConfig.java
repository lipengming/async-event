/*
 * @(#)BaseStressConfig.java Created on Apr 25, 2012
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

import java.util.Properties;

public class BaseStressConfig {
	protected Properties config;

	public BaseStressConfig(Properties config) {
		this.config = config;
	}

	public int getThreadNum() {
		return Integer.parseInt(config.getProperty(StressStrategy.KEY_THREAD_NUM, "10"));
	}

	public int getTaskPeriod() {
		return Integer.parseInt(config.getProperty(StressStrategy.KEY_TASK_PERIOD, "0"));
	}
	
	public int getStatPeriod() {
		return Integer.parseInt(config.getProperty(StressStrategy.KEY_STAT_PERIOD, "1000"));
	}

	public StressTask getTaskClass() {
		String taskClass = config.getProperty(StressStrategy.KEY_TASK_CLASS, null);
		if (taskClass == null)
			throw new IllegalArgumentException("未配置任务实现类!");
		try {
			return (StressTask) Class.forName(taskClass.trim()).newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("加载任务实现类错误:" + taskClass, e);
		}
	}

	public String getOutputFileName() {
		return config.getProperty("RESULT_PATH", "").trim();
	}

	public final String getResultPath() {
		return config.getProperty(StressStrategy.KEY_RESULT_PATH, null);
	}

	@Override
	public String toString() {
		return String.format("配置信息[线程数:%d, 任务间隔:%dms, 任务实现类:%s]%n", getThreadNum(), getTaskPeriod(), getTaskClass().getClass().getName());
	}
}
