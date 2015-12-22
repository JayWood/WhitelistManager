package com.plugish.whitelistmanager.SQL;

/**
 * Created by Jay on 12/19/2015.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.plugish.whitelistmanager.WhitelistManager;
import org.bukkit.entity.Player;

public abstract class Database {
	WhitelistManager plugin;
	Connection connection;

	public String table = "players";

	public Database( WhitelistManager instance ) {
		plugin = instance;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();

	public void initialize() {
		connection = getSQLConnection();
		try {
			PreparedStatement ps = connection.prepareStatement( "SELECT * FROM " + table + " WHERE id = ?" );
			ResultSet rs = ps.executeQuery();
			close( ps, rs );
		} catch ( SQLException ex ) {
			plugin.getLogger().log( Level.SEVERE, "Unable to retrieve connection", ex );
		}
	}

	public String lastLogin( Player player ) {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String uuid = player.getUniqueId().toString();

		try {
			conn = getSQLConnection();
			preparedStatement = conn.prepareStatement( "SELECT * FROM " + table + " WHERE id = '" + uuid + "';" );
			resultSet = preparedStatement.executeQuery();

			while ( resultSet.next() ) {
				if ( resultSet.getString( "id" ).equalsIgnoreCase( uuid.toLowerCase() ) ) {
					return resultSet.getString( "last_online" );
				}
			}

		} catch ( SQLException ex ) {
			plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionExecute(), ex );
		} finally {
			try {
				if ( preparedStatement != null ) {
					preparedStatement.close();
				}

				if ( conn != null ) {
					conn.close();
				}
			} catch ( SQLException ex ) {
				plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionClose(), ex );
			}
		}

		return "";
	}

	/**
	 * Checks the database against the player.
	 *
	 * @param player Instance of org.bukkit.entity.Player
	 * @return true on success, false otherwise
	 */
	public boolean playerExists( Player player ) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String uuid = player.getUniqueId().toString();
		String query = "SELECT * FROM " + table + " WHERE id = '" + uuid + "'";
		boolean found = false;

		try {
			connection = getSQLConnection();
			preparedStatement = connection.prepareStatement( query );

			// Returns true if there is a result, false otherwise.
			found = preparedStatement.execute();
		} catch ( SQLException ex ) {
			plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionExecute(), ex );
		} finally {
			try {
				if ( preparedStatement != null )
					preparedStatement.close();
				if ( connection != null )
					connection.close();
			} catch ( SQLException ex ) {
				plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionClose(), ex );
			}
		}

		return found;
	}

	/**
	 * Attempts to insert a new player into the database
	 *
	 * @param player Instance of org.bukkit.entity.Player
	 */
	public void insertPlayer( Player player ) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String uuid = player.getUniqueId().toString();

		try {
			connection = getSQLConnection();
			preparedStatement = connection.prepareStatement( "REPLACE INTO " + table + "(id,player,added,last_online) VALUES(?,?,date('now'),date('now'))" );
			preparedStatement.setString( 1, uuid );
			preparedStatement.setString( 2, player.getName() );

			int rows = preparedStatement.executeUpdate();
			if ( rows < 1 ) {
				plugin.getLogger().warning( "Failed inserting player " + player.getName() + " in the database." );
			}
		} catch ( SQLException ex ) {
			plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionExecute(), ex );
		} finally {
			try {
				if ( preparedStatement != null )
					preparedStatement.close();
				if ( connection != null )
					connection.close();
			} catch ( SQLException ex ) {
				plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionClose(), ex );
			}
		}
	}

	/**
	 * Updates the database row for the current player to the current date
	 *
	 * @param player Instance of org.bukkit.entity.Player
	 */
	public void updatePlayer( Player player ) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String uuid = player.getUniqueId().toString();

		try {
			connection = getSQLConnection();
			preparedStatement = connection.prepareStatement( "UPDATE " + table + " SET `last_login` = date('now') WHERE id = '" + uuid + "'" );
			int rows = preparedStatement.executeUpdate();
			if ( rows < 1 ) {
				plugin.getLogger().warning( "Failed updating player " + player.getName() + " in the database." );
			}
		} catch ( SQLException ex ) {
			plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionExecute(), ex );
		} finally {
			try {
				if ( preparedStatement != null )
					preparedStatement.close();
				if ( connection != null )
					connection.close();
			} catch ( SQLException ex ) {
				plugin.getLogger().log( Level.SEVERE, Errors.sqlConnectionClose(), ex );
			}
		}
	}


	public void close( PreparedStatement ps, ResultSet rs ) {
		try {
			if ( ps != null )
				ps.close();
			if ( rs != null )
				rs.close();
		} catch ( SQLException ex ) {
			Error.close( plugin, ex );
		}
	}
}
