package com.plugish.whitelistmanager.Tasks;

import com.plugish.whitelistmanager.Connection.Connection;
import com.plugish.whitelistmanager.WhitelistManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Sync extends BukkitRunnable {

	public WhitelistManager plugin = null;

	public Sync( WhitelistManager plugin ) {
		this.plugin = plugin;
	}

	public void run() {
		plugin.sync();
	}

}
