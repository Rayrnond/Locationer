package com.reflexian.locationer;

import com.reflexian.libmanager.utils.download.DownloadCallback;
import com.reflexian.libmanager.utils.download.DownloadUtil;
import com.reflexian.locationer.command.SaveLocationCommand;
import lombok.Getter;
import lombok.var;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Objects;

public final class Locationer extends JavaPlugin {

    @Getter private static Locationer instance;
    @Getter private static Connection connection;

    @Override
    public void onEnable() {
        instance=this;
        saveDefaultConfig();
        getCommand("savelocation").setExecutor(new SaveLocationCommand());
        getLibs();

        var var5 = new File(this.getDataFolder() + File.separator + "libs");
        var var6 = 0;
        if (!var5.exists()) {
            var5.mkdir();
        }

        URL var17 = null;

        var var7 = Objects.requireNonNull(var5.listFiles());
        for (File var10 : var7) {
            try {
                var var11 = new URL("jar:" + var10.toURI().toURL().toExternalForm() + "!/");
                var var12 = (URLClassLoader) ClassLoader.getSystemClassLoader();
                var var13 = URLClassLoader.class;
                var var14 = var13.getDeclaredMethod("addURL", URL.class);
                var14.setAccessible(true);
                var14.invoke(var12, var11);
                ++var6;
            } catch (Exception var15) {
                var15.printStackTrace();
            }
        }

        createNewDatabase("locations.db");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void getLibs() {
        DownloadUtil.download("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.34.0/sqlite-jdbc-3.34.0.jar", getDataFolder() + "/libs", new DownloadCallback() {
            @Override
            public void onSuccess() {
                getLogger().info("Downloaded SQLite JDBC Library!");
            }

            @Override
            public void onError(int var1) {
                getLogger().warning("Failed to download SQLite JDBC");
            }

            @Override
            public void onExist() {
                getLogger().info("Found SQLite JDBC, Skipping download!");
            }
        });

        DownloadUtil.download("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar", getDataFolder() + "/libs", new DownloadCallback() {
            @Override
            public void onSuccess() {
                getLogger().info("Downloaded MySQL Library!");
            }

            @Override
            public void onError(int var1) {
                getLogger().warning("Failed to download MySQL");
            }

            @Override
            public void onExist() {
                getLogger().info("Found MySQL, Skipping download!");
            }
        });
    }


    public void createNewDatabase(String fileName) {

        var var5 = new File(this.getDataFolder() + File.separator + "db");
        var var6 = 0;
        if (!var5.exists()) {
            var5.mkdir();
        }
        String url = "";
        if (getConfig().getString("database.type", "SQLITE").toUpperCase().equalsIgnoreCase("SQLITE")) {
            url = "jdbc:sqlite:" + getDataFolder() + "/db/" + fileName;
            try {
                connection=DriverManager.getConnection(url);
                String sql = "CREATE TABLE  if not exists data (uuid VARCHAR(50) PRIMARY KEY , location VARCHAR(50) NOT NULL);";
                connection.createStatement().execute(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            // jdbc:mysql://localhost:3306/sonoo
            url = "jdbc:mysql://" + getConfig().getString("database.url", "127.0.0.1") + ":" + getConfig().getInt("database.port", 3306) + "/" + getConfig().getString("database.database", "database");
            try{
                connection=DriverManager.getConnection(url, getConfig().getString("database.username", "root"), getConfig().getString("database.password", "password"));
                String sql = "CREATE TABLE  if not exists data (uuid VARCHAR(50) PRIMARY KEY , location VARCHAR(50) NOT NULL);";
                connection.createStatement().execute(sql);
            }catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            ResultSet r = connection.createStatement().executeQuery("SELECT * FROM data");
            getLogger().info("------ LOGGING SAVED LOCATIONS ------");
            while (r.next()) {
                getLogger().info(r.getString("uuid") + "  »  " + r.getString("location"));
            }
            getLogger().info("------ LOGGING SAVED LOCATIONS ------");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
