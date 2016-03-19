package com.plugish.whitelistmanager.Events;

import com.plugish.whitelistmanager.WhitelistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class loginListener implements Listener {

	WhitelistManager plugin;

	public loginListener( WhitelistManager plugin ) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
	}

	@EventHandler
	public void normalLogin( PlayerLoginEvent event ) {
		Player player = event.getPlayer();
	}

}
