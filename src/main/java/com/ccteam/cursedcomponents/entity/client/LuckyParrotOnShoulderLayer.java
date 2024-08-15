package com.ccteam.cursedcomponents.entity.client;

import com.ccteam.cursedcomponents.entity.ModEntities;
import com.ccteam.cursedcomponents.entity.attachments.ModEntityAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class LuckyParrotOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    private final LuckyParrotModel model;

    public LuckyParrotOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new LuckyParrotModel(modelSet.bakeLayer(ModModelLayers.LUCKY_PARROT));
    }

    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.render(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
        this.render(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }

    private void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float netHeadYaw,
            float headPitch,
            boolean leftShoulder
    ) {
        String uuidStr = livingEntity.getData(ModEntityAttachments.entityPlayerAttachment);
        if (uuidStr.isEmpty()) {
            LogUtils.getLogger().info("FAILED RENDER ON SHOULDER");
            return;
        }

        LogUtils.getLogger().info("Render lucky parrot on shoulder");
        poseStack.pushPose();
        poseStack.translate(leftShoulder ? 0.4F : -0.4F, livingEntity.isCrouching() ? -1.3F : -1.5F, 0.0F);

        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(LuckyParrotRenderer.LOCATION));
        this.model
                .renderOnShoulder(
                        poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, livingEntity.tickCount
                );
        poseStack.popPose();
    }

}
