package com.kusanali.world.feature;

import com.kusanali.world.feature.flower.ModFlowerGenerator;
import com.kusanali.world.feature.tree.ModTreeGeneration;

public class ModWorldGeneration {
    public static void register() {
        // TODO: Register world generation features here
        ModTreeGeneration.registerTree();
        ModFlowerGenerator.registerFlower();
    }
}
