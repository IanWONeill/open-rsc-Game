package org.openrsc.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.model.World;
import org.openrsc.server.networking.RSCConnectionHandler;
import org.openrsc.server.networking.WebConnectionHandler;

import com.rscdaemon.concurrent.ConfigurableThreadFactory;
import com.rscdaemon.concurrent.ConfigurableThreadFactory.ConfigurationBuilder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openrsc.server.util.Formulae;

public final class Server
{       
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
    
	private static GameEngine engine = new GameEngine();
	
	private static Server instance;
	
	public static Server getServer()
	{
		return instance;
	}
	
	private SocketAcceptor webAcceptor;
	
	public static SocketAcceptor acceptor;
	
	public static GameEngine getEngine()
	{
		return engine;
	}

	public Server() throws Exception
	{
		Logger logger = new Logger();
		logger.setDaemon(true);
		logger.start();
        Formulae.populateExperience();
		World.load();
		engine.start();
		try
		{
			acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1, Executors.newCachedThreadPool(new ConfigurableThreadFactory(
					new ConfigurationBuilder().setDaemon(true))));
			acceptor.getDefaultConfig().getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool(new ConfigurableThreadFactory(
					new ConfigurationBuilder().setDaemon(true)))));
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setThreadModel(ThreadModel.MANUAL);
			config.setDisconnectOnUnbind(true);
			((SocketSessionConfig)config.getSessionConfig()).setReuseAddress(true);
			((SocketSessionConfig)config.getSessionConfig()).setTcpNoDelay(true);
			acceptor.bind(new InetSocketAddress(Config.getServerIp(), Config.getServerPort()), new RSCConnectionHandler(engine.messageQueue), config);
		}
		catch (Exception e) {
			System.out.println(dateFormat.format(date)+": Unable to bind to: " + Config.getServerIp() + " (" + Config.getServerPort() + ")");
			System.exit(-1);
		}
		try {
			this.webAcceptor = new SocketAcceptor();
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setDisconnectOnUnbind(true);
			((SocketSessionConfig)config.getSessionConfig()).setReuseAddress(true);
			this.webAcceptor.bind(new InetSocketAddress(Config.getServerIp(), Config.getWebPort()), new WebConnectionHandler(engine.webMessageQueue), config);
		} catch (Exception e) {
			System.out.println(dateFormat.format(date)+": Unable to bind to: " + Config.getServerIp() + " (" + Config.getWebPort() + ")");
			System.exit(-1);
		}

	}

	/**
	 * Gracefully exits the openrsc server instance
	 * 
	 * @param autoRestart should the auto-restart script be invoked?
	 * 
	 */
	public void shutdown(boolean autoRestart)
	{
		System.out.println(dateFormat.format(date)+": " + Config.getServerName() + " is shutting down...");
		/// Remove all players
		engine.emptyWorld();
		World.getWorldLoader().saveAuctionHouse();
		engine.kill();
		/// Shutdown the networking
		try
		{
			acceptor.unbindAll();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			webAcceptor.unbindAll();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/// Disconnect from MySQL (waits to finish all pending queries, then shuts down)
		try
		{
			System.out.println(dateFormat.format(date)+": Waiting for database service to close...");
			ServerBootstrap.getDatabaseService().close();
			System.out.println(dateFormat.format(date)+": Database service has shut down...");
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		/// Invoke the auto-restart script
		if (autoRestart)
		{
			try
			{
				System.out.println(dateFormat.format(date)+": Launching automatic game server restart script...");
				Runtime.getRuntime().exec("./run_server.sh");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		try {
            String configFile   = args.length < 1 ? "config/config.xml" : args[0];
            File file           = new File(configFile);
			if(!file.exists())
			{
				System.err.println("Could not find configuration file: " + configFile);
				return;
			}
			Config.initConfig(file);
		} catch (IOException ex)
		{
			System.err.println("An error has been encountered while loading configuration: ");
			ex.printStackTrace();
		}
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
		System.out.println(dateFormat.format(date)+": " + Config.getServerName() + " is starting up...");
		Class.forName("org.openrsc.server.ServerBootstrap");
		try
		{
			instance = new Server();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			System.exit(-1);
		}
		System.out.println(dateFormat.format(date)+": " + Config.getServerName() + " is now online!");
	}

}
