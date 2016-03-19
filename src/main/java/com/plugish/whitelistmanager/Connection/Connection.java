package com.plugish.whitelistmanager.Connection;

import com.plugish.whitelistmanager.WhitelistManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {

	public HttpURLConnection connection = null;
	public WhitelistManager plugin;
	private static DataOutputStream outputStream = null;

	/**
	 * Connect to a URL
	 *
	 * @param plugin   Instance of WhitelistManager
	 * @param url_path The URL to the web site.
	 * @param key      The Key needed to access the website.
	 */
	public Connection( WhitelistManager plugin, String url_path ) {

		this.plugin = plugin;

		try {
			URL url = new URL( url_path );

			// Type-cast to HTTPURLConnection since it extends URLConnection which is what's returned from openConnection()
			connection = ( HttpURLConnection ) url.openConnection();
			connection.setRequestMethod( "POST" );
			connection.setRequestProperty( "User-Agent", "Mozilla/5.0" );
			connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
			connection.setRequestProperty( "Accept-Charset", "UTF-8" );
			connection.setRequestProperty( "charset", "utf-8" );
			connection.setDoInput( true );
			connection.setDoOutput( true );
			connection.setUseCaches( false );

		} catch ( IOException e ) {
			plugin.getLogger().warning( e.getMessage() );
		}

	}

	/**
	 * Sets the DataOutputStream for accessability
	 */
	private void setOutputStream() {
		try {
			OutputStream con = this.connection.getOutputStream();
			outputStream = new DataOutputStream( con );
		} catch ( IOException e ) {
			plugin.getLogger().severe( e.getMessage() );
		}
	}

	/**
	 * Gets the data output stream, sets it if necessary.
	 *
	 * @return DataOutputStream
	 */
	private DataOutputStream GetOutputStream() {
		if ( null == outputStream ) {
			setOutputStream();
		}

		return outputStream;
	}
}
