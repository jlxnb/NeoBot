package dev.neovoxel.neobot.util;

import dev.neovoxel.neobot.script.Script;
import lombok.Data;
import org.graalvm.polyglot.Value;

@Data
public class ValueWithScript {
    private final Value value;
    private final Script script;
}
