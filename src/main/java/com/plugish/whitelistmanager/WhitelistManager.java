package com.plugish.whitelistmanager;

import com.plugish.whitelistmanager.lang.Lang;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistManager extends JavaPlugin {

	public YamlConfiguration config;
	public YamlConfiguration l10n;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = ( YamlConfiguration ) getConfig();

		Lang lang = new Lang( this );
		l10n = lang.loadConfig();

		// Setup the login listener
		new loginListener( this );
	}

}
