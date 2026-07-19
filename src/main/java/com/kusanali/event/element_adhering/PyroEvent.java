package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.WeakHashMap;

public class PyroEvent {

    private static final WeakHashMap<PlayerEntity, Integer> CREATIVE_PYRO_GRACE =
            new WeakHashMap<>();
    public static void register() {
        /* ---------- 1. 火焰伤害 → Pyro（生存 / 冒险） ---------- */
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(
                (entity, source, amount) -> {

                    if (!isFireDamage(source)) {
                        return true;
                    }

                    entity.addStatusEffect(
                            new StatusEffectInstance(
                                    ModEffects.PYRO,
                                    200,
                                    0,
                                    false,
                                    true,
                                    true
                            )
                    );

                    return true;
                }
        );

        /* ---------- 2. 创造模式玩家：火焰环境检测 ---------- */
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (var player : world.getPlayers()) {

                // 只处理创造模式
                if (!player.isCreative()) continue;

                boolean inFire = isInFireEnvironment(player, world);

                if (inFire) {

                    // 在火焰中：刷新 Pyro + 清除冷却
                    CREATIVE_PYRO_GRACE.remove(player);
                    player.addStatusEffect(
                            new StatusEffectInstance(
                                    ModEffects.PYRO,
                                    200,
                                    0,
                                    false,
                                    true,
                                    true
                            )
                    );
                } else if (player.hasStatusEffect(ModEffects.PYRO)) {
                    // 不在火焰中，但有 Pyro：进入 10 秒倒计时
                    int ticksLeft = CREATIVE_PYRO_GRACE.getOrDefault(player, -1);

                    if (ticksLeft == -1) {
                        // 刚离开火焰
                        CREATIVE_PYRO_GRACE.put(player, 200);
                    } else if (ticksLeft > 0) {
                        // 倒计时中，不刷新时间
                        CREATIVE_PYRO_GRACE.put(player, ticksLeft - 1);
                    } else {
                        // 倒计时结束，不再续命
                        CREATIVE_PYRO_GRACE.remove(player);
                    }
                }
            }
        });
    }

    /* ================== 工具方法 ================== */

    private static boolean isFireDamage(DamageSource source) {
        return source.isOf(DamageTypes.IN_FIRE)
                || source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.LAVA)
                || source.isOf(DamageTypes.HOT_FLOOR)
                || source.isOf(DamageTypes.PLAYER_EXPLOSION)
                || source.isOf(DamageTypes.EXPLOSION)
                || source.isOf(DamageTypes.FIREBALL)
                || source.isOf(DamageTypes.FIREWORKS)
                || source.isOf(DamageTypes.UNATTRIBUTED_FIREBALL);
    }

    /** 判断是否在火焰 / 岩浆环境中（不依赖伤害） */
    private static boolean isInFireEnvironment(PlayerEntity player, ServerWorld world) {
        BlockPos pos = player.getBlockPos();
        if (world.getBlockState(pos).isOf(Blocks.FIRE)) return true;

        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());
        if (world.getBlockState(eyePos).isOf(Blocks.FIRE)) return true;

        return player.isInLava();
    }
}
