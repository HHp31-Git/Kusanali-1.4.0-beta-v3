package com.kusanali.world.dimension;

import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class Dream_1Type {
    public static final DimensionType PINK_SKY_DIMENSION = new DimensionType(
            OptionalLong.of(6000), // 固定时间为正午
            false, // 有天空
            false, // 有天花板
            false, // 超平坦世界没有天花板
            true,  // 床工作
            1.0,   // 坐标缩放
            false, // 创建龙蛋
            true, // 有闪电
            -64,     // 最小Y
            384,   // 高度
            384,   // 逻辑高度
            BlockTags.INFINIBURN_OVERWORLD, // 无限燃烧标签
            DimensionTypes.OVERWORLD_ID,     // 使用Overworld的渲染器
            9.0f,  // 环境光
            new DimensionType.MonsterSettings(false, false, ConstantIntProvider.create(0), 0)
    );
}
