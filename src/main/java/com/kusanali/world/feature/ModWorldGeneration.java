package com.kusanali.world.feature;

import com.kusanali.world.feature.flower.ModFlowerGenerator;
import com.kusanali.world.feature.tree.ModTreeGeneration;

public class ModWorldGeneration {
    public static void register() {
        ModTreeGeneration.registerTree();
        ModFlowerGenerator.registerFlower();
    }
}
