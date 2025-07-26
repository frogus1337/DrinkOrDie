package com.frogus.drinkordie.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModServerConfig {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ServerConfig SERVER;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {
        public final ForgeConfigSpec.IntValue bottleStackSize;
        public final ForgeConfigSpec.IntValue bucketStackSize;
        public final ForgeConfigSpec.BooleanValue bottlesRemoveSource;
        public final ForgeConfigSpec.BooleanValue vanillaBottlesRemoveSource;
        public final ForgeConfigSpec.IntValue bottleMbContent;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("DrinkOrDie");

            bottleStackSize = builder
                    .comment("Maximale Stackgröße für Flaschen (1 bis 64)")
                    .defineInRange("bottleStackSize", 16, 1, 64);

            bucketStackSize = builder
                    .comment("Maximale Stackgröße für Eimer (1 bis 64)")
                    .defineInRange("bucketStackSize", 16, 1, 64);

            bottlesRemoveSource = builder
                    .comment("Entfernen eigene Flaschen beim Befüllen die Quelle? (true = Quelle verschwindet)")
                    .define("bottlesRemoveSource", true);

            vanillaBottlesRemoveSource = builder
                    .comment("Entfernen Vanilla-Wasserflaschen beim Befüllen die Wasserquelle? (true = Quelle verschwindet)")
                    .define("vanillaBottlesRemoveSource", false);

            bottleMbContent = builder
                    .comment("Wieviel mB (Millibuckets) Flüssigkeit enthält eine Flasche? (Standard: 250, Vanilla-Bucket: 1000)")
                    .defineInRange("bottleMbContent", 1000, 1, 1000);

            builder.pop();
        }
    }
}
