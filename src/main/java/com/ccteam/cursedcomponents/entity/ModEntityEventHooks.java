package com.ccteam.cursedcomponents.entity;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID)
public class ModEntityEventHooks {

    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            Optional<CompoundTag> leftShoulder = Optional.ofNullable(player.getShoulderEntityLeft());
            Optional<CompoundTag> rightShoulder = Optional.ofNullable(player.getShoulderEntityRight());

            leftShoulder.ifPresent(compoundtag -> {
                if (EntityType.byString(compoundtag.getString("id")).isPresent() &&
                        EntityType.byString(compoundtag.getString("id")).get() == ModEntities.LUCKY_PARROT.get()) {
                    LuckyParrot.tickOnShoulder(player);
                }
            });

            rightShoulder.ifPresent(compoundtag -> {
                if (EntityType.byString(compoundtag.getString("id")).isPresent() &&
                        EntityType.byString(compoundtag.getString("id")).get() == ModEntities.LUCKY_PARROT.get()) {
                    LuckyParrot.tickOnShoulder(player);
                }
            });
        }
    }

}
