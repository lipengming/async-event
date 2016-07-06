/*
 * @(#)StressStrategySupport.java Created on 2013-7-9
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import static freamwork.core.StressStrategy.KEY_STRATEGY_CLASS;

public class StressStrategySupport {
	public static StressStrategy getStressStrategy(String configFile) {
		if (configFile == null || configFile.equals("")) {
			throw new IllegalArgumentException("压力配置文件不能为空!");
		}
		final Properties props = new Properties();
		try {
            props.load(ClassLoader.getSystemResourceAsStream(configFile));
		} catch (IOException e) {
			throw new IllegalStateException("找不到配置文件: " + configFile, e);
		}
		String strategyClazz = props.getProperty(KEY_STRATEGY_CLASS);
		if (strategyClazz == null || strategyClazz.trim().equals("")) {
			throw new IllegalStateException("没有配置压力策略类，KEY=" + KEY_STRATEGY_CLASS);
		}
		try {
			Constructor<?> con = Class.forName(strategyClazz).getConstructor(Properties.class);
			return (StressStrategy) con.newInstance(props);
		} catch (Exception e) {
			throw new IllegalStateException("无法实例化压力策略类:" + strategyClazz);
		}
	}
}
