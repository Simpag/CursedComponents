package com.ccteam.cursedcomponents;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Dimensional Quarry
    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_CONSUMPTION_BASE = BUILDER
            .comment("Dimensional Quarry Settings")
            .push("dimensional_quarry")
            .comment("Energy Consumption")
            .push("consumption")
            .comment("How much FE/t the dimensional quarry uses")
            .defineInRange("base", 40_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_UNBREAKING_1_DECREASE = BUILDER
            .defineInRange("unbreaking_I", 0.85, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_UNBREAKING_2_DECREASE = BUILDER
            .defineInRange("unbreaking_II", 0.7, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_UNBREAKING_3_DECREASE = BUILDER
            .defineInRange("unbreaking_III", 0.5, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_EFFICIENCY_1_INCREASE = BUILDER
            .defineInRange("efficiency_I", 1.2, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_EFFICIENCY_2_INCREASE = BUILDER
            .defineInRange("efficiency_II", 1.525, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_EFFICIENCY_3_INCREASE = BUILDER
            .defineInRange("efficiency_III", 1.85, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_EFFICIENCY_4_INCREASE = BUILDER
            .defineInRange("efficiency_IV", 2.175, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_EFFICIENCY_5_INCREASE = BUILDER
            .defineInRange("efficiency_V", 2.5, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_SILK_TOUCH_INCREASE = BUILDER
            .defineInRange("silk_touch", 1.5, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_FORTUNE_1_INCREASE = BUILDER
            .defineInRange("fortune_I", 1.2, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_FORTUNE_2_INCREASE = BUILDER
            .defineInRange("fortune_II", 1.35, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue DIMENSIONAL_QUARRY_FORTUNE_3_INCREASE = BUILDER
            .defineInRange("fortune_III", 1.5, 1.0, Double.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_BASE = BUILDER
            .pop()
            .comment("Quarry speed (Tick/Block)")
            .push("speed")
            .comment("How many ticks between each block mined")
            .defineInRange("base", 10, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_1 = BUILDER
            .defineInRange("efficiency_I", 8, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_2 = BUILDER
            .defineInRange("efficiency_II", 6, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_3 = BUILDER
            .defineInRange("efficiency_III", 4, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_4 = BUILDER
            .defineInRange("efficiency_IV", 2, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DIMENSIONAL_QUARRY_TPB_5 = BUILDER
            .defineInRange("efficiency_V", 1, 1, Integer.MAX_VALUE);

    // Sponge on Stick
    private static final ModConfigSpec.IntValue SPONGE_ON_STICK_CAPACITY = BUILDER
            .pop(2)
            .comment("Sponge On a Stick")
            .push("sponge_on_a_stick")
            .comment("Energy capacity")
            .defineInRange("capacity", 10_000, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue SPONGE_ON_STICK_USAGE = BUILDER
            .comment("Energy usage per use")
            .defineInRange("usage", 500, 0, Integer.MAX_VALUE);


    static final ModConfigSpec SPEC = BUILDER.build();

    /* CONFIG VALUES */
    public static Integer dimensionalQuarryBaseConsumption;
    public static List<Double> dimensionalQuarryUnbreakingConsumptionDecrease;
    public static List<Double> dimensionalQuarryEfficiencyConsumptionIncrease;
    public static List<Double> dimensionalQuarryFortuneConsumptionIncrease;
    public static Double dimensionalQuarrySilkTouchConsumptionIncrease;
    public static List<Integer> dimensionalQuarrySpeed;
    public static Integer spongeOnStickCapacity;
    public static Integer spongeOnStickUsage;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        dimensionalQuarryBaseConsumption = DIMENSIONAL_QUARRY_CONSUMPTION_BASE.get();

        dimensionalQuarryUnbreakingConsumptionDecrease = List.of(
                DIMENSIONAL_QUARRY_UNBREAKING_1_DECREASE.get(),
                DIMENSIONAL_QUARRY_UNBREAKING_2_DECREASE.get(),
                DIMENSIONAL_QUARRY_UNBREAKING_3_DECREASE.get()
        );

        dimensionalQuarryEfficiencyConsumptionIncrease = List.of(
                DIMENSIONAL_QUARRY_EFFICIENCY_1_INCREASE.get(),
                DIMENSIONAL_QUARRY_EFFICIENCY_2_INCREASE.get(),
                DIMENSIONAL_QUARRY_EFFICIENCY_3_INCREASE.get(),
                DIMENSIONAL_QUARRY_EFFICIENCY_4_INCREASE.get(),
                DIMENSIONAL_QUARRY_EFFICIENCY_5_INCREASE.get()
        );

        dimensionalQuarryFortuneConsumptionIncrease = List.of(
                DIMENSIONAL_QUARRY_FORTUNE_1_INCREASE.get(),
                DIMENSIONAL_QUARRY_FORTUNE_2_INCREASE.get(),
                DIMENSIONAL_QUARRY_FORTUNE_3_INCREASE.get()
        );

        dimensionalQuarrySilkTouchConsumptionIncrease = DIMENSIONAL_QUARRY_SILK_TOUCH_INCREASE.get();

        dimensionalQuarrySpeed = List.of(
                DIMENSIONAL_QUARRY_TPB_BASE.get(),
                DIMENSIONAL_QUARRY_TPB_1.get(),
                DIMENSIONAL_QUARRY_TPB_2.get(),
                DIMENSIONAL_QUARRY_TPB_3.get(),
                DIMENSIONAL_QUARRY_TPB_4.get(),
                DIMENSIONAL_QUARRY_TPB_5.get()
        );

        spongeOnStickCapacity = SPONGE_ON_STICK_CAPACITY.get();
        spongeOnStickUsage = SPONGE_ON_STICK_USAGE.get();
    }
}
