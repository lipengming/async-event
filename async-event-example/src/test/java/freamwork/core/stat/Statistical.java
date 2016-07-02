/*
 * @(#)Statistical.java Created on 2013-7-9
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core.stat;

public interface Statistical {
    long addCount(boolean success);

    long getFail();

    long addSucess();

    long addFail();

    long addTimeUsed(int msec);

    long getProcessed();

	double tps();

	double trt();

	String getStatInfo();
}