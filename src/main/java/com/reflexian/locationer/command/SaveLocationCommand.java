package com.reflexian.locationer.command;

import com.reflexian.locationer.Locationer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class SaveLocationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Locationer.getConnection().createStatement().execute("DELETE from data;");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Locationer.getConnection().createStatement().execute("REPLACE INTO data(uuid, location) VALUES ('" + onlinePlayer.getUniqueId().toString() + "', '" + onlinePlayer.getLocation().getBlockX() + ", " + onlinePlayer.getLocation().getBlockY() + ", " + onlinePlayer.getLocation().getBlockZ() +"');");
            }
            sender.sendMessage(ChatColor.GREEN + "Saving all location data!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Something went wrong! " + throwables.getLocalizedMessage());
        }
        return true;
    }
}
