package com.ccteam.cursedcomponents;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_CONSUMPTION_BASE = BUILDER
            .comment("Dimensional Quarry Settings")
            .push("dimensional_quarry")
            .comment("Energy Consumption")
            .push("consumption")
            .comment("How much FE/t the dimensional quarry uses")
            .defineInRange("base", 40_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_CONSUMPTION_1 = BUILDER
            .defineInRange("unbreakingI", 35_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_CONSUMPTION_2 = BUILDER
            .defineInRange("unbreakingII", 30_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_CONSUMPTION_3 = BUILDER
            .defineInRange("unbreakingIII", 25_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_BASE = BUILDER
            .pop()
            .comment("Quarry speed (Block/Tick)")
            .push("speed")
            .comment("How many ticks between each block mined")
            .defineInRange("base", 15, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_1 = BUILDER
            .defineInRange("efficiencyI", 12, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_2 = BUILDER
            .defineInRange("efficiencyII", 9, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_3 = BUILDER
            .defineInRange("efficiencyIII", 6, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_4 = BUILDER
            .defineInRange("efficiencyIV", 3, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_5 = BUILDER
            .defineInRange("efficiencyV", 1, 1, Integer.MAX_VALUE);


    static final ModConfigSpec SPEC = BUILDER.build();

    /* CONFIG VALUES */
    public static List<Integer> dimensionalQuarryConsumptions;
    public static List<Integer> dimensionalQuarrySpeed;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        dimensionalQuarryConsumptions = List.of(
                DIMENSIONAL_QUARRY_CONSUMPTION_BASE.get(),
                DIMENSIONAL_QUARRY_CONSUMPTION_1.get(),
                DIMENSIONAL_QUARRY_CONSUMPTION_2.get(),
                DIMENSIONAL_QUARRY_CONSUMPTION_3.get()
        );

        dimensionalQuarrySpeed = List.of(
                DIMENSIONAL_QUARRY_TPB_BASE.get(),
                DIMENSIONAL_QUARRY_TPB_1.get(),
                DIMENSIONAL_QUARRY_TPB_2.get(),
                DIMENSIONAL_QUARRY_TPB_3.get(),
                DIMENSIONAL_QUARRY_TPB_4.get(),
                DIMENSIONAL_QUARRY_TPB_5.get()
        );
    }
}
