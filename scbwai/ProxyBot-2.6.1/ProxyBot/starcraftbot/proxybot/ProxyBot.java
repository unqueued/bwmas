package starcraftbot.proxybot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import jade.core.Agent;

import starcraftbot.proxybot.bot.ExampleStarCraftBot;
import starcraftbot.proxybot.bot.StarCraftBot;

/**
 * ProxyBot.
 * 
 * Manages socket connections with StarCraft and handles the agent <-> StarCraft
 * communication.
 */
public class ProxyBot extends Agent{

	/** port to start the server socket on */
	public static int port = 12345;

	/** allow the user to control units */
	public static boolean allowUserControl = true;

	/** turn on complete information */
	public static boolean completeInformation = true;

	/** display agent commands in SC? */
	public static boolean logCommands = false;

	/** display agent commands in SC? */
	public static boolean terrainAnalysis = false;

	/** run the game very fast ? */
	public static boolean speedUp = true;

	public void setup() {
		new ProxyBot().start();
	}

	/**
	 * Starts the ProxyBot.
	 * 
	 * A server socket is opened and waits for client connections.
	 */
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);

			while (true) {
				System.out.println("Waiting for client connection");

				Socket clientSocket = serverSocket.accept();
				System.out.println("Received a client connection");
				runGame(clientSocket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manages communication with StarCraft.
	 */
	private void runGame(Socket socket) {
		final StarCraftBot bot = new ExampleStarCraftBot();
		Game gameRef = null;

		try {
			// 1. get the initial game information
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String playerData = reader.readLine();

			// 2. respond with bot options
			String botOptions = (allowUserControl ? "1" : "0")
					+ (completeInformation ? "1" : "0")
					+ (logCommands ? "1" : "0") + (terrainAnalysis ? "1" : "0");

			socket.getOutputStream().write(botOptions.getBytes());

			// 3. get the starting locations and map information
			String locationData = reader.readLine();
			String mapData = reader.readLine();

			// TA
			String regionsData = "Regions:";
			String chokesData = "Chokes:";
			String basesData = "Bases:";

			if (terrainAnalysis) {
				regionsData = reader.readLine();
				chokesData = reader.readLine();
				basesData = reader.readLine();
			}

			final Game game = new Game(playerData, locationData, mapData, chokesData, basesData, regionsData);
			gameRef = game;
			boolean firstFrame = true;

			if (speedUp) {
				game.getCommandQueue().setGameSpeed(0);
			}
			// 4. game updates
			while (true) {

				String update = reader.readLine();
				if (update.startsWith("ended")) {
					break;
				} else if (update == null) {
					break;
				} else {
					// update the game
					game.updateData(update);

					if (firstFrame) {
						firstFrame = false;

						// start the agent
						new Thread() {
							public void run() {
								bot.start(game);
							}
						}.start();
					}

					// 5. send commands
					socket.getOutputStream().write(
							game.getCommandQueue().getCommands().getBytes());
				}
			}
		} catch (SocketException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("StarCraft game over");

			// stop update thread
			gameRef.stop();

			// stop the bot
			if (bot != null) {
				bot.stop();
			}
		}
	}
}
