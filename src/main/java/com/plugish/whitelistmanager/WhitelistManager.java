package com.plugish.whitelistmanager;

import com.plugish.whitelistmanager.Lang.LangSetup;
import com.plugish.whitelistmanager.Tasks.Sync;
import org.apache.http.client.utils.URIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Set;

public class WhitelistManager extends JavaPlugin {

	public YamlConfiguration config;
	public YamlConfiguration l10n;

	public String lang = "en";

	public Sync sync = null;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = ( YamlConfiguration ) getConfig();

		this.lang = config.getString( "lang" );
		if ( null == this.lang ) {
			getLogger().warning( "No default l10n set, defaulting to english" );
			this.lang = "en";
		}

		sync = new Sync( this );
		sync.runTaskTimerAsynchronously( this, config.getInt( "update_interval" ) * 20, config.getInt( "update_interval" ) * 20 );
	}

	public String getLang( String path ) {
		if ( null == this.l10n ) {
			LangSetup lang = new LangSetup( this );
			l10n = lang.loadConfig();
		}

		return this.l10n.getString( path );
	}

	/**
	 * Syncs the white-list with the WebServer feed.
	 */
	public boolean sync() {
		String url = getConfig().getString( "url" );
		String key = getConfig().getString( "key" );
		URIBuilder uriBuilder = null;

		try {
			uriBuilder = new URIBuilder( url );
			uriBuilder.addParameter( "key", key );
			uriBuilder.addParameter( "mc-whitelist-feed", "1" );
		} catch ( Exception e ) {
			getLogger().severe( e.getMessage() );
		}

		if ( null == uriBuilder ) {
			return false;
		}

		// Reassign the variable.
		url = uriBuilder.toString();
		if ( url.equals( "" ) ) {
			return false;
		}

		String webWhiteList = getPageContent( url );

		// Reload the whitelist before checking
		Bukkit.getServer().reloadWhitelist();

		Set<OfflinePlayer> serverPlayers = Bukkit.getServer().getWhitelistedPlayers();
		if ( serverPlayers.isEmpty() || webWhiteList.equals( "" ) ) {
			return false;
		}

		JSONObject jsonObject = new JSONObject( webWhiteList );
		if ( ! jsonObject.has( "data" ) ) {
			return false;
		}

		JSONArray webPlayers = jsonObject.getJSONArray( "data" );
		removePlayers( serverPlayers, webPlayers );
		addPlayers( serverPlayers, webPlayers );

		getLogger().info( "Whitelist Sync'd" );

		return true;
	}

	/**
	 * Adds missing players to the white-list.
	 *
	 * @param serverPlayers Server-side white-listed players
	 * @param webPlayers Web-based white-listed players
	 */
	public void addPlayers( Set<OfflinePlayer> serverPlayers, JSONArray webPlayers ) {
		for ( int i = 0; i < webPlayers.length(); ++i ) {
			String webPlayer = webPlayers.getString( i );
			boolean foundInList = false;
			for ( OfflinePlayer player : serverPlayers ) {
				String offlinePlayerName = player.getName();
				if ( offlinePlayerName.equals( webPlayer ) || offlinePlayerName.isEmpty() ) {
					// This player exists.
					foundInList = true;
				}
			}

			if ( ! foundInList ) {
				Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "whitelist add " + webPlayer );
				Bukkit.getServer().broadcastMessage( ChatColor.GREEN + webPlayer + " has been added to the whitelist." );
			}
		}
	}

	/**
	 * Removes players not on the web-based whitelist.
	 *
	 * @param serverPlayers Server-side white-listed players
	 * @param webPlayers Web-based white-listed players
	 */
	public void removePlayers( Set<OfflinePlayer> serverPlayers, JSONArray webPlayers ) {
		for ( OfflinePlayer serverPlayer : serverPlayers ) {

			// Get the player's name
			String playerName = serverPlayer.getName();

			if ( isPlayerInIgnoredList( playerName ) ) {
				continue;
			}

			// Default whitelist to false
			boolean shouldBeWhiteListed = false;

			// Loop through web-based player list
			for ( int i = 0; i < webPlayers.length(); ++i ) {

				// Get the current player name in the loop.
				String curWebPlayer = webPlayers.getString( i );

				// If they're on the web-based player list, AND we've seen them, skip 'em
				if ( curWebPlayer.equals( playerName ) ) {
					shouldBeWhiteListed = true;
				}
			}

			// Check the ignored players
//			shouldBeWhiteListed = isPlayerInIgnoredList( playerName );

			// Removes players from the white-list.
			if ( ! shouldBeWhiteListed ) {
				serverPlayer.setWhitelisted( false );
				if ( ! playerName.equals( "" ) ) {
					Bukkit.getServer().broadcastMessage( ChatColor.AQUA + playerName + " was removed from the server." );
				} else {
					Bukkit.getServer().broadcastMessage( ChatColor.DARK_PURPLE + "A player has been removed from the white-list." );
				}
			}
		}
	}

	public boolean isPlayerInIgnoredList( String playerToCheck ) {
		List<String> ignoredPlayers = getConfig().getStringList( "ignoreRemove" );

		for ( String ignoredPlayer : ignoredPlayers ) {
			if ( ignoredPlayer.equalsIgnoreCase( playerToCheck ) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets page content from a URL.
	 *
	 * @param urlToRead String
	 * @return String
	 */
	public String getPageContent( String urlToRead ) {
		StringBuilder result = new StringBuilder();

		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL( urlToRead );
		} catch ( MalformedURLException e ) {
			getLogger().severe( e.getMessage() );
		}

		if ( null == url ) {
			return "";
		}

		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11" );
			connection.setUseCaches( false );
		} catch ( IOException e ) {
			getLogger().severe( e.getMessage() );
		}

		if ( null == connection ) {
			return "";
		}

		try {
			connection.setRequestMethod( "GET" );
		} catch ( ProtocolException e ) {
			getLogger().severe( e.getMessage() );
		}

		InputStream inputStream = null;

		try {
			inputStream = connection.getInputStream();
		} catch ( IOException e ) {
			getLogger().severe( e.getMessage() );
		}

		if ( null == inputStream ) {
			return "";
		}

		BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
		String line;
		try {
			while ( null != ( line = reader.readLine() ) ) {
				result.append( line );
			}
			reader.close();
		} catch ( IOException e ) {
			getLogger().severe( e.getMessage() );
		}

		return result.toString();
	}

}
