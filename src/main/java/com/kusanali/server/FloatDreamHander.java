package com.kusanali.server;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

import static net.minecraft.block.Block.getDroppedStacks;

public class FloatDreamHander {
    // 注册用于服务端DataTracker的追踪数据，用于在服务端存储和同步R技能的冷却结束时间戳
    private static final TrackedData<Long> R_COOLDOWN = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.LONG);
    // 注册用于服务端DataTracker的追踪数据，用于在服务端存储和同步G技能的冷却结束时间戳
    private static final TrackedData<Long> G_COOLDOWN = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.LONG);

    /**
     * 设置技能冷却结束时间
     * @param player 目标玩家
     * @param cooldownKey 技能标识键
     * @param cooldownEnd 冷却结束的毫秒级时间戳
     */
    private static void setCooldownTime(ServerPlayerEntity player, String cooldownKey, long cooldownEnd) {
        if ("r_ability_cooldown".equals(cooldownKey)) {
            player.getDataTracker().set(R_COOLDOWN, cooldownEnd);
        } else if ("g_ability_cooldown".equals(cooldownKey)) {
            player.getDataTracker().set(G_COOLDOWN, cooldownEnd);
        }
    }

    /**
     * 获取技能冷却结束时间
     * @param player 目标玩家
     * @param cooldownKey 技能标识键
     * @return 冷却结束时间戳，若无匹配则返回0
     */
    private static long getCooldownTime(ServerPlayerEntity player, String cooldownKey) {
        if ("r_ability_cooldown".equals(cooldownKey)) {
            return player.getDataTracker().get(R_COOLDOWN);
        } else if ("g_ability_cooldown".equals(cooldownKey)) {
            return player.getDataTracker().get(G_COOLDOWN);
        }
        return 0;
    }

    public static void register() {
        // 监听实体加载事件，为登录的服务端玩家初始化DataTracker，开始追踪冷却数据
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                player.getDataTracker().startTracking(R_COOLDOWN, 0L);
                player.getDataTracker().startTracking(G_COOLDOWN, 0L);
            }
        });

        // 注册R键技能（心景幻成）的网络包接收器
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "activate_r_ability"),
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    // 校验玩家是否持有法器，若未持有则拦截请求
                    if (validateAbilityUse(player)) return;

                    // 校验冷却：若当前时间早于记录的结束时间，说明冷却尚未结束
                    if (getCooldownTime(player, "r_ability_cooldown") > System.currentTimeMillis()) {
                        return;
                    }

                    // 执行R键技能逻辑
                    executeRAbility(player);
                }));

        // 注册G键技能（所识遍记）的网络包接收器
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "activate_g_ability"),
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    if (validateAbilityUse(player)) return;

                    // 检查G技能冷却
                    if (getCooldownTime(player, "g_ability_cooldown") > System.currentTimeMillis()) {
                        return;
                    }

                    // 执行G键技能逻辑
                    executeGAbility(player);
                }));
    }

    /**
     * 校验玩家是否具备释放技能的资格（主手或副手必须持有FloatDream）
     * @return 若未持有法器返回true，持有则返回false
     */
    private static boolean validateAbilityUse(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        return mainHand.getItem() != ModItems.FLOAT_DREAM && offHand.getItem() != ModItems.FLOAT_DREAM;
    }

    /**
     * 执行R键技能
     */
    private static void executeRAbility(ServerPlayerEntity player) {
        // 设置30秒冷却（30000毫秒）
        long cooldownEnd = System.currentTimeMillis() + 30000;
        setCooldownTime(player, "r_ability_cooldown", cooldownEnd);

        // 给玩家自身施加力量I效果，持续20秒（400 ticks）
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20, 0));

        // 获取以玩家为中心30格半径范围内的AABB检测盒
        Box area = new Box(player.getBlockPos()).expand(30);
        // 筛选该区域内的敌对生物（Monster实例）
        player.getWorld().getEntitiesByClass(
                net.minecraft.entity.LivingEntity.class, area,
                entity -> entity instanceof Monster && entity != player
        ).forEach(entity -> {
            // 施加自定义魔法伤害效果（20秒），持续造成伤害
            entity.addStatusEffect(new StatusEffectInstance(ModEffects.MAGIC_DAMAGE, 20 * 20, 0));
            entity.addStatusEffect(new StatusEffectInstance(ModEffects.DENDRO, 15));
        });

        // 构建网络包，将冷却结束时间同步给客户端，用于渲染HUD冷却条
        PacketByteBuf packetByteBuf1 = new PacketByteBuf(PacketByteBufs.create().writeLong(cooldownEnd));
        ServerPlayNetworking.send(player, new Identifier("kusanali", "r_cooldown_update"),
                packetByteBuf1);
    }

    /**
     * 执行G键技能
     */
    private static void executeGAbility(ServerPlayerEntity player) {
        // 初始设置5秒冷却（5000毫秒）
        long cooldownEnd = System.currentTimeMillis() + 5000;
        setCooldownTime(player, "g_ability_cooldown", cooldownEnd);

        // 获取玩家朝向向量与脚部坐标
        Vec3d playerPos = player.getPos();
        Vec3d lookVec = player.getRotationVec(1.0F);

        // 构建前方7格长度的AABB检测区域，并向四周扩展3格，形成粗略的柱形检测区
        Vec3d areaEnd = playerPos.add(lookVec.multiply(7));
        Box detectionBox = new Box(playerPos, areaEnd).expand(3);

        // 获取检测区域内的所有存活着生物实体
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                detectionBox,
                entity -> entity != player && entity.isAlive()
        );

        // 计算扇形检测的阈值：cos(30°) ≈ 0.866，用于限制左右各30度的攻击张角
        final double THRESHOLD = Math.cos(Math.toRadians(30));

        // 在粗略检测的基础上，通过点乘精确筛选扇形内的实体，并取最近的一个作为目标
        LivingEntity target = entities.stream()
                .filter(entity -> {
                    Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                    return toEntity.dotProduct(lookVec) > THRESHOLD;
                })
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(playerPos)))
                .orElse(null);

        if (target != null) {
            // === 命中实体逻辑 ===
            DamageSources sources = player.getWorld().getDamageSources();
            target.damage(sources.magic(), 6.0f); // 造成6点魔法伤害
            target.addStatusEffect(new StatusEffectInstance(ModEffects.TRIBBLE, 9 * 20, 0));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 9 * 20, 0));
            target.addStatusEffect(new StatusEffectInstance(ModEffects.DENDRO, 15));
            // 若命中敌对生物，冷却时间缩短为3秒
            if (target instanceof Monster) {
                cooldownEnd = System.currentTimeMillis() + 3000;
            }
        } else {
            // === 未命中实体逻辑：尝试采集视线聚焦的植物 ===
            // 发射射线检测准星指向的方块，最远距离11格
            HitResult hitResult = player.raycast(11.0, 0.0F, false);

            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState blockState = player.getWorld().getBlockState(pos);
                Block block = blockState.getBlock();

                // 判断目标方块是否为可采集的植物类型（花、树苗、草、珊瑚等）
                if (block instanceof FlowerBlock ||
                        block instanceof SaplingBlock ||
                        block instanceof TallFlowerBlock ||
                        block instanceof GrassBlock ||
                        block instanceof SeaPickleBlock ||
                        block instanceof CoralFanBlock ||
                        block instanceof FernBlock ||
                        block instanceof SweetBerryBushBlock ||
                        block instanceof MushroomPlantBlock) {

                    // 模拟方块被破坏后的掉落物
                    List<ItemStack> drops = getDroppedStacks(
                            blockState,
                            (ServerWorld) player.getWorld(),
                            pos, null, player, player.getMainHandStack()
                    );

                    // 直接将掉落物插入玩家背包
                    drops.forEach(player.getInventory()::insertStack);

                    // 移除该方块（不产生额外掉落，false表示不触发方块掉落逻辑）
                    player.getWorld().breakBlock(pos, false, player);

                    // 命中植物时，将冷却时间缩短为3秒（3000毫秒）
                    cooldownEnd = System.currentTimeMillis() + 3000;
                }
            }
        }

        // 无论命中实体还是植物，最终将实际的冷却结束时间同步给客户端渲染
        PacketByteBuf packetByteBuf2 = new PacketByteBuf(PacketByteBufs.create().writeLong(cooldownEnd));
        ServerPlayNetworking.send(player, new Identifier("kusanali", "g_cooldown_update"),
                packetByteBuf2);
    }
}

