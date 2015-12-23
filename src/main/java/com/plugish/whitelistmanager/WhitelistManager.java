package com.plugish.whitelistmanager;

import com.plugish.whitelistmanager.SQL.SQLite;
import com.plugish.whitelistmanager.lang.Lang;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistManager extends JavaPlugin {

	public YamlConfiguration config;
	public YamlConfiguration l10n;

	public SQLite sqlite;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = ( YamlConfiguration ) getConfig();

		sqlite = new SQLite( this );
		sqlite.load();

		Lang lang = new Lang( this );
		l10n = lang.loadConfig();

		// Setup the login listener
		new loginListener( this );
	}

}
