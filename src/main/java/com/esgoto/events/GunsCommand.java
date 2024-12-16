package com.esgoto.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GunsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("guns")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ItemStack item = AK47ItemStack.createAK47();
                p.getInventory().addItem(item);
                //p.getInventory().addItem(BarretItemStack.Barret);

            } else {
                System.out.println("Only players can use this command.");
            }
        }
        return false;
    }
}