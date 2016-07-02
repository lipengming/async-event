/*
 * @(#)ConfigurableStressStrategy.java Created on 2013-7-9
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

import java.util.Properties;

public abstract class ConfigurableStressStrategy extends AbstractStatisticalStressStrategy {
	protected Properties config;

	public ConfigurableStressStrategy(Properties config) {
		this.config = config;
	}

	public abstract BaseStressConfig getStressConfig();
	
	@Override
	protected int getTaskPeriod() {
		return getStressConfig().getTaskPeriod();
	}
}
