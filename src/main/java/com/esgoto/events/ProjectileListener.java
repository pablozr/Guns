package com.esgoto.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileListener implements Listener {

    private Gun getPlayerGun(Player p) {
        ItemStack itemHand = p.getItemInHand();
        if(itemHand.isSimilar(AK47ItemStack.AK47)) {
            return new AK47();
            //} else if (itemHand.isSimilar(BarretItemStack.Barret)) {
            //return new Barret();
        }
        return null;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        Entity damager = e.getDamager();
        if(damager instanceof Snowball){
            Snowball snowball = (Snowball) damager;
            Entity shooter = (Entity) snowball.getShooter();

            if(shooter instanceof Player){
                Player player = (Player) shooter;
                Gun weapon = getPlayerGun(player);
                if (weapon != null) {
                    if(e.getEntity() instanceof LivingEntity){
                        LivingEntity entity = (LivingEntity) e.getEntity();
                        entity.damage(weapon.getDamage());
                    }
                }
            }
        }
    }
}