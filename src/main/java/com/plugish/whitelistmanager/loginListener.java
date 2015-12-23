package com.plugish.whitelistmanager;

import com.plugish.whitelistmanager.SQL.SQLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * Created by Jay on 12/17/2015.
 */
public class loginListener implements Listener {

	WhitelistManager plugin;

	public loginListener( WhitelistManager plugin ) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
	}

	@EventHandler
	public void normalLogin( PlayerLoginEvent event ) {
		Player player = event.getPlayer();

		SQLite sqlite = new SQLite( plugin );
		sqlite.load();

		boolean exists = sqlite.playerExists( player );
		plugin.getLogger().info( player.getName() + " has logged in, checking database." );

		if( ! exists ) {
			plugin.getLogger().info( "Player does not exist, inserting player data." );
			sqlite.insertPlayer( player );
		} else {
			plugin.getLogger().info( "Player Exists, attempting to update his data." );
			sqlite.updatePlayer( player );
		}
	}

}
