package com.example.spin_spawner.data;

import com.example.spin_spawner.BaseSpinSpawner.Result;
import com.simibubi.create.Create;

import static com.example.spin_spawner.SpinSpawnerMod.REGISTRATE;

public class ModLang {
    public static void register() {
        addCreateRawLang("gui.spawner.spawn_failed", "Last Spawning Failed");
        addCreateRawLang(Result.INVALID_ENTITY.langKey, "No entity or invalid entity in spawner.");
        addCreateRawLang(Result.TOO_MANY_ENTITIES.langKey, "Too many of the same entities within %1$d block radius.");
        addCreateRawLang(Result.INSUFFICIENT_SPACE.langKey, "%1$d block radius have no enough space to be spawn.");
        addCreateRawLang(Result.DIFFICULTY_PEACEFUL.langKey, "Difficulty is peaceful but entity that will spawn are not.");

        addCreateRawLang("gui.spawner.generate_failed", "Generating Failed");
        addCreateRawLang("gui.spawner.incorrect_spawn_rule_1", "Need to be matched in order entity's spawning conditions in spawner to generate stress.");
        addCreateRawLang("gui.spawner.incorrect_spawn_rule_2", "Tip: some entity have a probability of failing to spawn even if spawning conditions is acceptable.");
    }

    private static void addCreateRawLang(String key, String value) {
        REGISTRATE.addRawLang(Create.ID + '.' + key, value);
    }
}
