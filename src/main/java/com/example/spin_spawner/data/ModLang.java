package com.example.spin_spawner.data;

import com.example.spin_spawner.SpinSpawnerMod;
import com.example.spin_spawner.BaseSpinSpawner.Result;
import com.simibubi.create.Create;
import net.createmod.ponder.Ponder;

import static com.example.spin_spawner.SpinSpawnerMod.REGISTRATE;

public class ModLang {
    public static void register() {
        addCreateRawLang("gui.spawner.spawn_failed", "Last Spawning Failed");
        addCreateRawLang(Result.INVALID_ENTITY.langKey, "No entity or invalid entity in spawner.");
        addCreateRawLang(Result.TOO_MANY_ENTITIES.langKey, "Too many of the same entities within %1$d block radius.");
        addCreateRawLang(Result.INSUFFICIENT_SPACE.langKey, "%1$d block radius have no enough space to be spawn.");

        addCreateRawLang("gui.spawner.generate_failed", "Generating Failed");
        addCreateRawLang("gui.spawner.incorrect_spawn_rule_1", "Need to be matched in order entity's spawning conditions in spawner to generate stress.");
        addCreateRawLang("gui.spawner.incorrect_spawn_rule_2", "Tip: some entity have a probability of failing to spawn even if spawning conditions is acceptable.");

        addSpawnerPonderLang("shared.this_is", "This is a Spin Spawner");
        addSpawnerPonderLang("shared.see_other", "%1$s");

        addSpawnerPonderLang("spin_spawner_spawner",
                "Spin Spawner Spawner Mode",
                "It can ignore spawning conditions except empty space.",
                "Required stress is proportional to the sqrt of the volume of entity."
        );
        addSpawnerPonderLang("spin_spawner_generator",
                "Spin Spawner Generator Mode",
                "When it has redstone signal, instead of spawning entities, it will generate stress.",
                "This mode has a fixed speed of %1$d rpm and the same stress as the spawner mode."
        );

    }

    private static void addCreateRawLang(String key, String value) {
        REGISTRATE.addRawLang(Create.ID + '.' + key, value);
    }

    private static void addSpawnerPonderLang(String key, String value) {
        REGISTRATE.addRawLang(SpinSpawnerMod.MODID + '.' + Ponder.MOD_ID + "." + key, value);
    }

    private static void addSpawnerPonderLang(String key, String header, String... texts) {
        addSpawnerPonderLang(key + ".header", header);
        for (int i = 0; i < texts.length; i++) {
            addSpawnerPonderLang(key + ".text_" + (i + 1), texts[i]);
        }
    }
}
