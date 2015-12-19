package com.plugish.whitelistmanager.SQL;

import com.plugish.whitelistmanager.WhitelistManager;

import java.util.logging.Level;

public class Error {
	public static void execute( WhitelistManager plugin, Exception ex ) {
		plugin.getLogger().log( Level.SEVERE, "Couldn't execute MySQL statement: ", ex );
	}

	public static void close( WhitelistManager plugin, Exception ex ) {
		plugin.getLogger().log( Level.SEVERE, "Failed to close MySQL connection: ", ex );
	}
}
