/*
 * @(#)DefaultStatisticImpl.java Created on 2013-7-9
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core.stat;

import freamwork.core.StressStrategy;

import java.util.concurrent.atomic.AtomicLong;

public class DefaultStatisticImpl implements Statistical {
	private long startTime;
	private StressStrategy stressStrategy;
	private AtomicLong fail = new AtomicLong(0);
	private AtomicLong success = new AtomicLong(0);
	private AtomicLong timeUsed = new AtomicLong(0);

	public DefaultStatisticImpl(StressStrategy stressStrategy) {
		this.startTime = System.currentTimeMillis();
		this.stressStrategy = stressStrategy;
	}
	
	public long addCount(boolean success) {
		return success ? addSucess() : addFail();
	}

	public long getFail() {
		return fail.get();
	}

	public long addSucess() {
		return success.incrementAndGet();
	}

	public long addFail() {
		return fail.incrementAndGet();
	}

	public long addTimeUsed(int msec) {
		return timeUsed.addAndGet(msec);
	}
	
	public long getTimeUsed() {
		return timeUsed.get();
	}
	
	public long getProcessed() {
		return success.get() + fail.get();
	}

	public double tps() {
		long timeSpent = System.currentTimeMillis() - startTime;
		return NumberUtil.roundTo(getProcessed() * 1000 * 1.0 / timeSpent, 2);
	}

	public double trt() {
		return NumberUtil.roundTo(getTimeUsed() / 1.0 / getProcessed(), 2);
	}

	public String getStatInfo() {
        long processed = getProcessed();
        long fail = getFail();
		double trt = trt();
		double failPercent = NumberUtil.roundTo(fail / 1.0 / processed * 100, 2);
		double percentageOfCompletion = stressStrategy.percentageOfCompletion();
		double tps = tps();
		String info = String.format("Progress[%s%%(%d Processed)],TRT[%s ms],TPS[%s],Fail[%d],FP[%s%%]\r\n", percentageOfCompletion, processed, trt, tps, fail, failPercent);
		return info;
	}
}
