package dev.jamieisgeek.superultrastaffchat;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class SuperUltraStaffChat extends Plugin {
    private Configuration configuration;
    private Manager manager;

    @Override
    public void onEnable() {
        try {
            this.setupConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager = new Manager(this);
        this.setupChannels();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setupConfig() throws IOException {
        if (!getDataFolder().exists()) {
            getLogger().info("Created config folder: " + getDataFolder().mkdir());
        }

        File configFile = new File(getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }

        if (!getDataFolder().exists()) {
            getLogger().info("Created config folder: " + getDataFolder().mkdir());
        }

        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    }

    private void setupChannels() {
        Configuration channels = configuration.getSection("channels");
        for (String channelName : channels.getKeys()) {
            Configuration channel = channels.getSection(channelName);
            String displayName = channel.getString("displayName");
            String permission = channel.getString("permission");
            String chatColor = channel.getString("chatColor");
            String chatPrefix = channel.getString("chatPrefix");
            String command = channel.getString("command");
            String[] aliases = channel.getStringList("aliases").toArray(new String[0]);
            manager.addChannel(new Channel(channelName, displayName, permission, chatColor, chatPrefix, command, aliases));
        }

        for(Channel channel : manager.getChannels()) {
            getProxy().getPluginManager().registerCommand(this, new ChatCommand(channel.command(), channel, manager));
        }
    }
}
