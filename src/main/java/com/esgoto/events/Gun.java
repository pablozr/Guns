package com.esgoto.events;

import com.esgoto.CSDirector;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class Gun implements Listener {
    protected ItemStack item;
    protected double damage;
    protected double range;
    protected float fireRate;
    protected float rechargeTime;
    protected int currentAmmo;
    protected int maxAmmo;
    protected float accuracy;
    private long lastShotTime;
    private final String uniqueId;

    private static final HashMap<UUID, BukkitRunnable> tasks = new HashMap<>();
    protected static HashMap<UUID, HashMap<String, Integer>> playerAmmo = new HashMap<>();
    protected static HashMap<UUID, Boolean> isReloading = new HashMap<>();
    protected static HashMap<List<String>, String> playerCurrentGunId = new HashMap<>();

    public Gun(String displayName, double damage, double range, float fireRate, float rechargeTime, int maxAmmo, float accuracy) {
        this.uniqueId = UUID.randomUUID().toString();
        this.item = GunUtils.createUniqueItemStack(displayName, this.uniqueId);
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.rechargeTime = rechargeTime;
        this.currentAmmo = maxAmmo;
        this.maxAmmo = maxAmmo;
        this.accuracy = accuracy;
        this.lastShotTime = 0;
    }


    public boolean canShoot(Player p) {
//        UUID playerUUID = p.getUniqueId();
//        HashMap<String, Integer> ammoMap = playerAmmo.getOrDefault(playerUUID, new HashMap<>());
//        int ammo = ammoMap.getOrDefault(this.uniqueId, maxAmmo);
//
//        return ammo > 0 && (System.currentTimeMillis() - lastShotTime) >= (fireRate * 1000);
        return true;
    }

    public void shoot(Player p) {
        UUID playerUUID = p.getUniqueId();
        List<String> lore = item.getItemMeta().getLore();
        String currentGunId = playerCurrentGunId.get(lore);
        if (currentGunId != null && currentGunId.equals(this.uniqueId) && canShoot(p)) {
            if (p.getInventory().getItemInHand().isSimilar(item)) {
                HashMap<String, Integer> ammoMap = playerAmmo.getOrDefault(playerUUID, new HashMap<>());
                int ammo = ammoMap.getOrDefault(this.uniqueId, maxAmmo);
                ammoMap.put(this.uniqueId, --ammo);
                playerAmmo.put(playerUUID, ammoMap);
                lastShotTime = System.currentTimeMillis();
                Snowball proj = p.launchProjectile(Snowball.class);
                Vector direction = p.getLocation().getDirection();
                proj.setVelocity(direction.multiply(7));
                p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1);
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            }
        } else {
            p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.3f);
            p.sendMessage(ChatColor.RED + "Você não tem munição!");
        }
    }

    public void reload(Player p) {
        UUID playerUUID = p.getUniqueId();
        List<String> lore = item.getItemMeta().getLore();
        System.out.println("Lore da arma: " + lore);
        String currentGunId = playerCurrentGunId.get(lore);
        if (currentGunId != null && currentGunId.equals(this.uniqueId) && !isReloading.getOrDefault(playerUUID, false)) {
            isReloading.put(playerUUID, true);
            p.sendMessage(ChatColor.YELLOW + "Recarregando...");

            BukkitRunnable reloadTask = new BukkitRunnable() {
                @Override
                public void run() {
                    HashMap<String, Integer> ammoMap = playerAmmo.getOrDefault(playerUUID, new HashMap<>());
                    ammoMap.put(uniqueId, maxAmmo);
                    playerAmmo.put(playerUUID, ammoMap);
                    isReloading.put(playerUUID, false);
                    p.sendMessage(ChatColor.GREEN + "Recarregada");
                }
            };
            tasks.put(p.getUniqueId(), reloadTask);
            reloadTask.runTaskLater(CSDirector.getPlugin(), (long) (rechargeTime * 20));
        } else {
            p.sendMessage(ChatColor.RED + "A arma já está sendo carregada");
        }
    }

    public void cancelReloading(Player p) {
        UUID uuid = p.getUniqueId();

        if (isReloading.getOrDefault(uuid, false)) {
            if (tasks.containsKey(uuid)) {
                tasks.get(uuid).cancel();
                tasks.remove(uuid);
            }
            isReloading.put(uuid, false);
            p.sendMessage(ChatColor.RED + "Carregamento cancelado");
        }
    }

    public void equip(Player p) {
        List<String> lore = item.getItemMeta().getLore();
        if (lore != null) {
            playerCurrentGunId.put(lore, this.uniqueId);
            System.out.println("Arma equipada: " + this.uniqueId + " para o jogador: " + p.getName());
        }
    }

    @EventHandler
    public void onPlayerSwitchSlot(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        ItemStack newItem = p.getInventory().getItem(e.getNewSlot());

        if (newItem != null && newItem.isSimilar(this.item)) {
            this.equip(p);
        }


        if (isReloading.getOrDefault(p.getUniqueId(), false) && (newItem == null || !newItem.isSimilar(this.item))) {
            cancelReloading(p);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID uuid = p.getUniqueId();
        int heldHotbarSlot = p.getInventory().getHeldItemSlot();
        int clickedSlot = e.getSlot();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null && clickedItem.isSimilar(this.item)) {
            this.equip(p);
        }
        if (e.getWhoClicked() instanceof Player) {
            if (tasks.containsKey(uuid)) {
                if (clickedSlot == heldHotbarSlot) {
                    cancelReloading(p);
                    p.sendMessage(ChatColor.RED + "Eai vei");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action a = e.getAction();
        Player p = e.getPlayer();
        List<String> lore = item.getItemMeta().getLore();
        ItemStack itemInHand = p.getItemInHand();

        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            System.out.println("Item em mãos: " + p.getItemInHand().getType());
            System.out.println("Item da arma: " + this.item.getType());
            if (itemInHand.getItemMeta().hasLore() && this.item.getItemMeta().hasLore()) {
                String itemInHandID = GunUtils.getUUIDFromItem(itemInHand);
                String thisItemID = GunUtils.getUUIDFromItem(this.item);
                System.out.println("aaa");
                if (itemInHandID != null && itemInHandID.equals(thisItemID)) {
                    this.equip(p);
                    this.shoot(p);
                    System.out.println("bbb");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (e.isSneaking()) {
            if (p.getItemInHand().isSimilar(this.item)) {
                this.reload(p);
            }
        }
    }

    public double getDamage() {
        return damage;
    }
}