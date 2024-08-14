package com.ccteam.cursedcomponents.entity.client;

import com.ccteam.cursedcomponents.entity.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LuckyParrotOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    //private final LuckyParrotModel model;

    public LuckyParrotOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> renderer) { //, EntityRendererProvider.Context pContext) {
        super(renderer);
        //this.model = new LuckyParrotModel(pContext.bakeLayer(ModModelLayers.LUCKY_PARROT));
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
        CompoundTag compoundtag = leftShoulder ? livingEntity.getShoulderEntityLeft() : livingEntity.getShoulderEntityRight();
        EntityType.byString(compoundtag.getString("id"))
                .filter(entityType -> entityType == ModEntities.LUCKY_PARROT.get())
                .ifPresent(
                        entityType -> {
//                            poseStack.pushPose();
//                            poseStack.translate(leftShoulder ? 0.4F : -0.4F, livingEntity.isCrouching() ? -1.3F : -1.5F, 0.0F);
//
//                            VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(LuckyParrotRenderer.LOCATION));
//                            this.model
//                                    .renderOnShoulder(
//                                            poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, livingEntity.tickCount
//                                    );
//                            poseStack.popPose();

                            poseStack.pushPose();
                            poseStack.translate(0.0F, 0.0F, 0.125F);
                            double d0 = Mth.lerp((double)limbSwingAmount, limbSwing, headPitch) - Mth.lerp((double)limbSwingAmount, netHeadYaw, livingEntity.getX());
                            double d1 = Mth.lerp((double)limbSwingAmount, limbSwing, headPitch) - Mth.lerp((double)limbSwingAmount, netHeadYaw, livingEntity.getY());
                            double d2 = Mth.lerp((double)limbSwingAmount, limbSwing, headPitch) - Mth.lerp((double)limbSwingAmount, netHeadYaw, livingEntity.getZ());
                            float f = Mth.rotLerp(limbSwingAmount, limbSwing, headPitch);
                            double d3 = (double)Mth.sin(f * (float) (Math.PI / 180.0));
                            double d4 = (double)(-Mth.cos(f * (float) (Math.PI / 180.0)));
                            float f1 = (float)d1 * 10.0F;
                            f1 = Mth.clamp(f1, -6.0F, 32.0F);
                            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                            f2 = Mth.clamp(f2, 0.0F, 150.0F);
                            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                            f3 = Mth.clamp(f3, -20.0F, 20.0F);
                            if (f2 < 0.0F) {
                                f2 = 0.0F;
                            }

                            float f4 = Mth.lerp(limbSwingAmount, 0f, 100f);
                            f1 += Mth.sin(Mth.lerp(limbSwingAmount, 0f, 100f) * 6.0F) * 32.0F * f4;
                            if (livingEntity.isCrouching()) {
                                f1 += 25.0F;
                            }

                            poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                            poseStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
                            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));
                            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entitySolid(LuckyParrotRenderer.LOCATION));
                            this.getParentModel().renderCloak(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
                            poseStack.popPose();
                        }
                );
    }

}
