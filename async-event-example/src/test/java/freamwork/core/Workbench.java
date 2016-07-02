/*
 * @(#)Workbench.java Created on 2012-11-15
 *
 * Copyright 2003-2012 UMPay, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package freamwork.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class Workbench {

	private ExecutorService servicePool;
	private ScheduledExecutorService scheduledThreadPool;
	private CountDownLatch finish;

	private BaseStressConfig config;

	private FileWriter resultWriter;;

	private volatile boolean initialized;

	private ConfigurableStressStrategy stressStrategy;

	public Workbench(String configFile) throws IOException {
		initWorkbench(configFile);
	}

	/**
	 * @throws java.io.IOException
	 */
	private void initWorkbench(String configFile) throws IOException {
		if (initialized) {
			return;
		}
		this.stressStrategy = getStressStrategy(configFile);
		this.config = stressStrategy.getStressConfig();
		if (this.config == null) {
			throw new NullPointerException("未配置压力测试参数!");
		}
		servicePool = new ThreadPoolExecutor(config.getThreadNum(), config.getThreadNum(), 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2000), new CallerRunsPolicy());
		finish = new CountDownLatch(config.getThreadNum());

		scheduledThreadPool = Executors.newScheduledThreadPool(1);
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			public void run() {
				statistic();
			}
		}, config.getStatPeriod(), config.getStatPeriod(), TimeUnit.MILLISECONDS);

		if (!"".equals(config.getOutputFileName())) {
			resultWriter = new FileWriter(config.getOutputFileName());
		}
		this.initialized = true;// 初始化完成
	}

	public ConfigurableStressStrategy getStressStrategy(String configFile) {
		return (ConfigurableStressStrategy) StressStrategySupport.getStressStrategy(configFile);
	}

	public void startPerf() throws Exception {
		if (!initialized) {
			throw new IllegalStateException("未初始化!");
		}

		final int threadNum = config.getThreadNum();
		for (int i = 0; i < threadNum; i++) {
			servicePool.execute(new Runnable() {
				public void run() {
					stressStrategy.runStress();
					finish.countDown();
				}
			});
		}
		finish.await();
		scheduledThreadPool.shutdownNow();
		statistic();
		servicePool.shutdown();
	}

	void statistic() {
		String info = stressStrategy.getStatistical().getStatInfo();
		if (resultWriter != null) {
			try {
				resultWriter.write(info);
				resultWriter.flush();
			} catch (IOException e) {
			}
		} else {
			System.out.print(info);
		}
	}

}
