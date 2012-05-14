package com.nymtek.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class CommonConfig {

	static Logger logger = Logger.getLogger(CommonConfig.class.getName());
	// database property
	private static String PROPERTY_DB_DRIVER = "DB_DRIVER";
	private static String PROPERTY_DB_URL = "DB_URL";
	private static String PROPERTY_DB_USER = "DB_USER";
	private static String PROPERTY_DB_PASSWORD = "DB_PASSWORD";

	private static String PERIOD = "period";
	private static String NODES = "node_count";

	// border router node property
	// private static String PROPERTY_ADDRESS_ROOT_NODE = "ADDRESS_ROOT_NODE";
	// private static String PROPERTY_ADDRESS_ROOT_PC = "ADDRESS_ROOT_PC";
	// the general task property
	// private static String PROPERTY_TASK_PERIOD = "TASK_PERIOD";
	// private static String PROPERTY_TASK_DELAY = "TASK_DELAY";

	// private static String PROPERTY_CHECK_TASK_INTERVAL =
	// "CHECK_TASK_INTERVAL";
	// private static String PROPERTY_CHECK_UPDATE_INTERVAL =
	// "CHECK_UPDATE_INTERVAL";

	// private static String PROPERTY_LISNETER_PORT = "LISTENER_PORT";
	// private static String PROPERTY_SOCKET_TIMEOUT = "SOCKET_TIMEOUT";
	/******** default values *************/
	// private static final long DEFAULT_PERIOD = 20 * 1000;
	// private static final long DEFAULT_DELAY = 10 * 1000;
	// node force update information timer settings
	// private static final long DEFAULT_CHECK_INTERVAL = 5 * 1000;
	// private static final long DEFAULT_UPDATE_INTERVAL = 15 * 1000;

	// private static final String DEFAULT_NODE_IPV6 = "aaaa::ff:fe00:1";
	// private static final String DEFAULT_PC_IPV6 = "aaaa::1";

	// private static int DEFAULT_LISNETER_PORT = 3000;
	// private static int DEFAULT_SOCKET_TIME_OUT = 6000;

	private static CommonConfig instance = null;
	private Properties props;

	private CommonConfig() {
		loadProperties();
	}

	public static CommonConfig instance() {
		if (null == instance) {
			instance = new CommonConfig();
		}
		return instance;
	}

	public String getProperty(String propName) {
		if (props == null) {
			loadProperties();
		}
		return props.getProperty(propName);
	}

	public static int getPeriod() {
		String str = instance().getProperty(PERIOD);
		int ms = Integer.valueOf(str);

		if (ms < 0) {
			ms = 30;
		}
		return ms;
	}
	
	public static int getNodeCount() {
		String str = instance().getProperty(NODES);
		int ms = Integer.valueOf(str);

		if (ms < 0) {
			ms = 6;
		}
		return ms;
	}

	// public static long getTaskPeriod() {
	// String str = instance().getProperty(PROPERTY_TASK_PERIOD);
	// long ms = Long.valueOf(str);
	// if (ms < 0) {
	// ms = DEFAULT_PERIOD;
	// }
	// return ms;
	// }
	//
	// public static int getListenerPort() {
	// String str = instance().getProperty(PROPERTY_LISNETER_PORT);
	// int p = Integer.valueOf(str);
	// if (p < 0) {
	// p = DEFAULT_LISNETER_PORT;
	// }
	// return p;
	// }
	//
	// public static int getSocketTimeout() {
	// String str = instance().getProperty(PROPERTY_SOCKET_TIMEOUT);
	// int p = Integer.valueOf(str);
	// if (p < 15000) {
	// p = DEFAULT_SOCKET_TIME_OUT;
	// }
	// return p;
	// }
	//
	//
	// public static long getTaskDelay() {
	// String str = instance().getProperty(PROPERTY_TASK_DELAY);
	// long ms = Long.valueOf(str);
	// if (ms < 0) {
	// ms = DEFAULT_DELAY;
	// }
	// return ms;
	// }
	//
	// public static long getCheckInterval() {
	// String str = instance().getProperty(PROPERTY_CHECK_TASK_INTERVAL);
	// long ms = Long.valueOf(str);
	// if (ms < 0) {
	// ms = DEFAULT_CHECK_INTERVAL;
	// }
	// return ms;
	// }
	//
	// public static long getUpdateInterval() {
	// String str = instance().getProperty(PROPERTY_CHECK_UPDATE_INTERVAL);
	// long ms = Long.valueOf(str);
	// if (ms < 0) {
	// ms = DEFAULT_UPDATE_INTERVAL;
	// }
	// return ms;
	// }
	//
	// public static String getDefaultNodeAddress() {
	// String str = instance().getProperty(PROPERTY_ADDRESS_ROOT_NODE);
	// if (null == str || str.isEmpty()) {
	// str = DEFAULT_NODE_IPV6;
	// }
	// return str;
	// }
	//
	// public static String getDefaultPCAddress() {
	// String str = instance().getProperty(PROPERTY_ADDRESS_ROOT_PC);
	// if (null == str || str.isEmpty()) {
	// str = DEFAULT_PC_IPV6;
	// }
	// return str;
	// }

	public static String[] getDBConfig() {
		String[] dbConfig = new String[4];
		dbConfig[0] = instance().getProperty(PROPERTY_DB_DRIVER);
		dbConfig[1] = instance().getProperty(PROPERTY_DB_URL);
		dbConfig[2] = instance().getProperty(PROPERTY_DB_USER);
		dbConfig[3] = instance().getProperty(PROPERTY_DB_PASSWORD);
		return dbConfig;
	}

	private synchronized void loadProperties() {
		props = new Properties();
		try {
			System.out.println("Load config.properties file");
			InputStream in = new FileInputStream(new File("config.properties"));
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
