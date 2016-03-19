package com.plugish.whitelistmanager.Tasks;

import com.plugish.whitelistmanager.WhitelistManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Sync extends BukkitRunnable {

	public WhitelistManager plugin = null;

	public Sync( WhitelistManager plugin ) {
		this.plugin = plugin;
	}

	public void run() {
		// Run the task
		/**
		 * Contact wordpress website
		 * get whitelisted names
		 *
		 * then
		 *
		 * get whitelist.json file
		 *
		 * create two arrayList objects
		 * remove,add
		 * for each item in the list
		 * either remove or add players
		 */
	}

}
