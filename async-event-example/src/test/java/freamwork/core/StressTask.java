/*
 * @(#)StressTask.java Created on Apr 25, 2012
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

public interface StressTask {

	/**
	 * 压力测试需要执行的任务
	 * 
	 * @return 任务成功执行返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	boolean doTask() throws Exception;
}
