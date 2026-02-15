package com.mach.dFramework.manager

import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.SQLException

class MigrationManager(
    private val connection: Connection,
    private val migrationFolder: String,
    private val dbType: String
) {
    init {
        val createSchemaTable = if (dbType == "sqlite") {
            "CREATE TABLE IF NOT EXISTS schema_version (version TEXT PRIMARY KEY)"
        } else {
            "CREATE TABLE IF NOT EXISTS schema_version (version VARCHAR(50) PRIMARY KEY)"
        }

        connection.prepareStatement(createSchemaTable).use { stmt ->
            stmt.execute()
        }
    }

    @Throws(SQLException::class, IOException::class, URISyntaxException::class)
    fun applyMigrations() {
        val resource = requireNotNull(javaClass.classLoader.getResource(migrationFolder)) {
            "Migration folder not found: $migrationFolder"
        }
        val uri = resource.toURI()

        if (uri.scheme == "jar") {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fs ->
                applyMigrationsFromRoot(fs.getPath(migrationFolder))
            }
        } else {
            applyMigrationsFromRoot(Paths.get(uri))
        }
    }

    private fun applyMigrationsFromRoot(root: Path) {
        Files.walk(root).use { paths ->
            val migrationFiles = paths
                .filter(Files::isRegularFile)
                .filter { path -> path.fileName.toString().endsWith(".$dbType.sql") }
                .sorted()
                .toList()

            for (migrationFile in migrationFiles) {
                val version = getMigrationVersion(migrationFile)
                if (!isMigrationApplied(version)) {
                    println("Aplicando migração: $version")
                    applyMigration(migrationFile)
                    recordMigration(version)
                }
            }
        }
    }

    @Throws(SQLException::class)
    private fun isMigrationApplied(version: String): Boolean {
        val query = "SELECT COUNT(*) FROM schema_version WHERE version = ?"
        connection.prepareStatement(query).use { stmt ->
            stmt.setString(1, version)
            stmt.executeQuery().use { rs ->
                return rs.next() && rs.getInt(1) > 0
            }
        }
    }

    @Throws(SQLException::class, IOException::class)
    private fun applyMigration(migrationFile: Path) {
        val sql: String = Files.readString(migrationFile)

        val statements = sql.split("(?<=;)(\\s*\\R)".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        connection.autoCommit = false
        try {
            connection.createStatement().use { stmt ->
                for (statement in statements) {
                    val trimmed = statement.trim { it <= ' ' }
                    if (trimmed.isNotEmpty()) {
                        stmt.execute(trimmed)
                    }
                }
                connection.commit()
            }
        } catch (e: SQLException) {
            connection.rollback()
            throw e
        } finally {
            connection.autoCommit = true
        }
    }

    @Throws(SQLException::class)
    private fun recordMigration(version: String) {
        val insert = "INSERT INTO schema_version (version) VALUES (?)"
        connection.prepareStatement(insert).use { stmt ->
            stmt.setString(1, version)
            stmt.executeUpdate()
        }
    }

    private fun getMigrationVersion(migrationFile: Path): String {
        val filename = migrationFile.fileName.toString()
        return filename.split("__").first()
    }
}