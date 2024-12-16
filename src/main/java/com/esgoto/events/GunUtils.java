package com.esgoto.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

public class GunUtils {
    public static ItemStack createUniqueItemStack(String displayName, String uniqueID){
        ItemStack gun = new ItemStack(Material.GOLD_BARDING);
        ItemMeta meta = gun.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + displayName);
        String uniqueId = UUID.randomUUID().toString();
        meta.setLore(Arrays.asList("ID: " + uniqueId));
        gun.setItemMeta(meta);
        return gun;
    }

    public static String getUUIDFromItem(ItemStack item){
        if(item.hasItemMeta() && item.getItemMeta().hasLore()){
            List<String> lore = item.getItemMeta().getLore();
            if(lore != null && !lore.isEmpty()){
                return lore.get(0).substring(4);
            }
        }
        return null;
    }
}