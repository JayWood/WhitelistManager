package com.plugish.whitelistmanager;

import com.plugish.whitelistmanager.Events.loginListener;
import com.plugish.whitelistmanager.Lang.LangSetup;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistManager extends JavaPlugin {

	public YamlConfiguration config;
	public YamlConfiguration l10n;

	public String lang = "en";

	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = ( YamlConfiguration ) getConfig();

		this.lang = config.getString( "lang" );
		if ( null == this.lang ) {
			getLogger().warning( "No default l10n set, defaulting to english" );
			this.lang = "en";
		}

		// Setup the login listener
		new loginListener( this );
	}

	public String getLang( String path ) {
		if ( null == this.l10n ) {
			LangSetup lang = new LangSetup( this );
			l10n = lang.loadConfig();
		}

		return this.l10n.getString( path );
	}

}
