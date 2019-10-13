package com.loader.openrsc;

public class Constants {

	// 43594 openrsc / 43595 cabbage / 43596 preservation / 43597 openpk / 43598 wk / 43599 dev

	// RSC Cabbage
	public static final String RSCC_GAME_NAME = "RSC Cabbage";
	public static final String RSCC_WORLD_STATS_URL = "https://openrsc.com/stats"; // Used for the world statistics
	static final String RSCC_SERVER_DOMAIN = "game.openrsc.com"; // Used for the server status display
	static final int RSCC_SERVER_PORT = 43595;

	// Open RSC
	public static final String ORSC_GAME_NAME = "Open RSC";
	public static final String ORSC_WORLD_STATS_URL = "https://openrsc.com/orscstats"; // Used for the world statistics
	static final String ORSC_SERVER_DOMAIN = "game.openrsc.com"; // Used for the server status display
	static final int ORSC_SERVER_PORT = 43594;

	// RSC Preservation
	public static final String RSCP_GAME_NAME = "RSC Preservation (Alpha)";
	//public static final String RSCP_WORLD_STATS_URL = "https://openrsc.com/cabbagestats"; // Used for the world statistics
	static final String RSCP_SERVER_DOMAIN = "game.openrsc.com"; // Used for the server status display
	static final int RSCP_SERVER_PORT = 43596;

	// Open PK
	public static final String OPENPK_GAME_NAME = "Open PK (Alpha)";
	//public static final String OPENPK_WORLD_STATS_URL = "https://openrsc.com/cabbagestats"; // Used for the world statistics
	static final String OPENPK_SERVER_DOMAIN = "game.openrsc.com"; // Used for the server status display
	static final int OPENPK_SERVER_PORT = 43597;

	// Dev World
	public static final String DEV_GAME_NAME = "Dev World";
	static final String DEV_SERVER_DOMAIN = "game.openrsc.com"; // Used for the server status display
	static final int DEV_SERVER_PORT = 43599;

	// Localhost
	public static final String LOCALHOST_GAME_NAME = "Single Player";
	static final String LOCALHOST_SERVER_DOMAIN = "localhost"; // Used for the server status display
	static final int LOCALHOST_SERVER_PORT = 43594;

	// Launcher + Cache
	public static final String Title = "Open RSC Game Launcher";
	public static final String BASE_URL = "https://game.openrsc.com/"; // Cache and client jar download locations depend on this
	public static final String CONF_DIR = "Cache";
	public static final String CLIENT_FILENAME = "Open_RSC_Client.jar";
	public static final String CACHE_URL = BASE_URL + "downloads/";
	public static final Double VERSION_NUMBER = 20191006.131300; //YYYYMMDD.HHMMSS format
	public static final String VERSION_UPDATE_URL = "https://gitlab.openrsc.com/open-rsc/Game/raw/3.0.0/Launcher/src/com/loader/openrsc/Constants.java";
	public static final String UPDATE_JAR_URL = "https://game.openrsc.com/downloads/OpenRSC.jar";
	public static final String JAR_FILENAME = "OpenRSC.jar";

	// Link Buttons
	public static final String BUTTON1 = "Discord";
	public static final String BUTTON2 = "Bug Reports";
	public static final String BUTTON3 = "Bot Reports";
	public static final String BUTTON4 = "RSC Wiki";
}
