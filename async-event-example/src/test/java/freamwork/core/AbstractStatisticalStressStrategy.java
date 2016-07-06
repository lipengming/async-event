/*
 * @(#)AbstractStatisticalStressStrategy.java Created on 2013-7-9
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

import freamwork.core.stat.DefaultStatisticImpl;
import freamwork.core.stat.Statistical;

public abstract class AbstractStatisticalStressStrategy implements StressStrategy {
	protected Statistical stat = new DefaultStatisticImpl(this);// 默认

	public Statistical getStatistical() {
		return stat;
	}

	public void setStatistical(Statistical stat) {
		this.stat = stat;
	}

	protected abstract int getTaskPeriod();

	protected void doTask(final StressTask task) {
		try {
			long start = System.currentTimeMillis();
			boolean result = task.doTask();
			long end = System.currentTimeMillis();
			stat.addTimeUsed((int) (end - start));
			stat.addCount(result);
			if (getTaskPeriod() > 0) {
				Thread.sleep(getTaskPeriod());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			stat.addFail();
		}
	}
}
