package com.plugish.whitelistmanager;

import com.plugish.whitelistmanager.Events.loginListener;
import com.plugish.whitelistmanager.Lang.LangSetup;
import com.plugish.whitelistmanager.Tasks.Sync;
import org.apache.http.client.utils.URIBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

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
		Set<OfflinePlayer> serverPlayers = this.getServer().getWhitelistedPlayers();
		if ( serverPlayers.isEmpty() || webWhiteList.equals( "" ) ) {
			return false;
		}

		JSONObject jsonObject = new JSONObject( webWhiteList );
		if ( ! jsonObject.has( "data" ) ) {
			return false;
		}

		JSONArray webPlayers = jsonObject.getJSONArray( "data" );
		for ( OfflinePlayer player : serverPlayers ) {
			String playerName = player.getName();
			boolean shouldBeWhiteListed = false;
			for ( int i = 0; i < webPlayers.length(); ++i ) {
				String curWebPlayer = webPlayers.getString( i );
				if ( curWebPlayer.equals( playerName ) ) {
					shouldBeWhiteListed = true;
				}
			}

			if ( shouldBeWhiteListed ) {
				player.setWhitelisted( false );

				getServer().broadcastMessage( "Removed " + playerName + " from the whitelist." );
			}
		}

		return true;
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
