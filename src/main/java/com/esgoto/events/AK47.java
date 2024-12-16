package com.esgoto.events;

import org.bukkit.entity.Player;

public class AK47 extends Gun {
    public AK47() {
        super(AK47ItemStack.createAK47().toString(),
                1.85F, // dano
                50.0, // alcance
                0.000001F, // taxa de disparo
                2.0f, // tempo de recarga
                30, // munição máxima
                0.8f // precisão
        );
    }

    @Override
    public void equip(Player p){
        super.equip(p);
    }
}