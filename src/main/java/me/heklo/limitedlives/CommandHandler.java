package me.heklo.limitedlives;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor {
    final private LimitedLives plugin;
    public CommandHandler(LimitedLives plugin)
    {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        String errorMsg = "&6[LimitedLives] &7Here is a list of commands:\n";
        errorMsg += "&f/limitedlives setlives <Player> <Amount>\n";
        errorMsg += "&f/limitedlives reload";
        List<String> aliases = Arrays.asList("ll","limitedlives");

        if(!aliases.contains(label.toLowerCase()))
            return true;
		
		if(args.length > 0)
		{
			if(!sender.hasPermission("limitedlives.admin"))
			{
				errorMsg = "&6[LimitedLives] &7You do not have permission to use LimitedLives commands.";
			}
			else if(args[0].equalsIgnoreCase("reload"))
			{
				String successMsg = "&6[LimitedLives] &7Reloading plugin...";
				successMsg = ChatColor.translateAlternateColorCodes('&', successMsg);
				sender.sendMessage(successMsg);
				plugin.reloadConfig();
				successMsg = "&6[LimitedLives] &7Reload Complete.";
				successMsg = ChatColor.translateAlternateColorCodes('&', successMsg);
				sender.sendMessage(successMsg);
				return true;
			}

			else if(args[0].equalsIgnoreCase("setlives"))
			{
				if(args.length != 3)
				{
					errorMsg = "&6[LimitedLives] &7Usage: &f/limitedlives setlives <Player> <Amount>&7.";
				}
				else
				{
					OfflinePlayer player = Bukkit.getPlayer(args[1]);
					if(player == null)
					{
						errorMsg = "&6[LimitedLives] &7Error: Player not found.";
					}
					else
					{
						try
						{
							int amount = Integer.parseInt(args[2]);
							plugin.setLives(player, amount);
							String successMsg = "&6[LimitedLives] &7Setting &f%player%&7's lives to &f%amount%&7."
									.replaceAll("%player%", player.getName())
									.replaceAll("%amount%", String.valueOf(amount));
							successMsg = ChatColor.translateAlternateColorCodes('&', successMsg);
							sender.sendMessage(successMsg);
							return true;
						}
						catch (NumberFormatException exception)
						{
							errorMsg = "&6[LimitedLives] &7Error: Amount of lives must be an integer value.";
						}
					}
				}
			}
		}
        errorMsg = ChatColor.translateAlternateColorCodes('&', errorMsg);
        sender.sendMessage(errorMsg);
        return true;
    }
}
