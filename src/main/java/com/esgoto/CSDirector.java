package com.esgoto;

import com.esgoto.events.*;
import com.esgoto.events.AK47ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class CSDirector extends JavaPlugin implements Listener {

    private static CSDirector plugin;

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("guns").setExecutor(new GunsCommand());
        getServer().getPluginManager().registerEvents(this, this);

        AK47ItemStack.init();

        registerListener();
    }

    public void registerListener(){
        PluginManager pm = Bukkit.getPluginManager();


        pm.registerEvents(new ProjectileListener(), this);
    }

    @Override
    public void onDisable() {
    }

    public static CSDirector getPlugin() {
        return plugin;
    }
}