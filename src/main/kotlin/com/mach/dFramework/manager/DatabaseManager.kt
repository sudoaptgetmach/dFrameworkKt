package com.mach.dFramework.manager

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.SQLException
import java.util.*

class DatabaseManager(configManager: ConfigManager, plugin: JavaPlugin) {
    private val config: FileConfiguration? = null
    private var dataSource: HikariDataSource? = null
    private var plugin: JavaPlugin? = null

    fun setup() {
        val dbType: String = Objects.requireNonNull(config!!.getString("database.type"))?.lowercase() ?: "sqlite"
        val url: String?
        val username: String?
        val password: String?
        val driver: String?
        when (dbType) {
            "mysql", "mariadb" -> {
                driver = if (dbType == "mysql")
                    "com.mysql.cj.jdbc.Driver"
                else
                    "org.mariadb.jdbc.Driver"
                url = String.format(
                    "jdbc:%s://%s:%s/%s",
                    if (dbType == "mysql") "mysql" else "mariadb",
                    config.getString("database.host"),
                    config.getString("database.port"),
                    config.getString("database.name")
                )
                username = config.getString("database.username")
                password = config.getString("database.password")
            }

            "sqlite" -> {
                driver = "org.sqlite.JDBC"
                url = "jdbc:sqlite:" + plugin!!.dataFolder.absolutePath + "/data.db"
                username = ""
                password = ""
            }

            else -> throw IllegalArgumentException("Unsupported database type: $dbType")
        }

        try {
            Class.forName(driver)
        } catch (_: ClassNotFoundException) {
            throw ExceptionInInitializerError("$driver not found")
        }

        val hc = HikariConfig()
        hc.jdbcUrl = url
        hc.username = username
        hc.password = password
        hc.setDriverClassName(driver)
        hc.maximumPoolSize = 10
        hc.connectionTimeout = 30000

        dataSource = HikariDataSource(hc)

        applySqlMigrations(dbType)
    }

    private fun applySqlMigrations(dbType: String) {
        val folder = when (dbType) {
            "mysql", "mariadb" -> "migrations/mysql"
            "sqlite" -> "migrations/sqlite"
            else -> throw IllegalArgumentException("No migration folder for: " + dbType)
        }
        try {
            getConnection().use { conn ->
                val mm = MigrationManager(conn, folder, dbType)
                mm.applyMigrations()
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to apply SQL migrations", e)
        }
    }

    @Throws(SQLException::class)
    fun getConnection(): Connection {
        return dataSource?.connection ?: Connection::class.java.cast(null)
    }

    fun shutdown() {
        dataSource?.close()
    }
}