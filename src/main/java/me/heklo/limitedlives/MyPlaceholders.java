package me.heklo.limitedlives;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;


public class MyPlaceholders extends PlaceholderExpansion
{
    final private LimitedLives plugin;

    public MyPlaceholders(LimitedLives plugin)
    {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "limitedlives";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Heklo";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("lives"))
        {
            return String.valueOf(plugin.getLives(player));
        }
        return null;
    }
}