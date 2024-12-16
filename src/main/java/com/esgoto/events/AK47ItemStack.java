package com.esgoto.events;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class AK47ItemStack {

    public static ItemStack AK47;

    public static void init() {
        AK47 = createAK47();
    }

    public static ItemStack createAK47() {
        return GunUtils.createUniqueItemStack("AK-47",UUID.randomUUID().toString());
    }
}
