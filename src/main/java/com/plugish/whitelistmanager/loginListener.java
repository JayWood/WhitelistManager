package com.plugish.whitelistmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

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

		plugin.getLogger().info( player.getName() + " has logged in." );

	}

}
