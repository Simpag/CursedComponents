package com.ccteam.cursedcomponents.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

public class RenderUtil {

    public static void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, int combinedLight, int combinedOverlay, int tintColor) {
        PoseStack.Pose posestack$pose = poseStack.last();

        for (BakedQuad bakedquad : quads) {
            float f = (float) FastColor.ARGB32.alpha(tintColor) / 255.0F;
            float f1 = (float) FastColor.ARGB32.red(tintColor) / 255.0F;
            float f2 = (float) FastColor.ARGB32.green(tintColor) / 255.0F;
            float f3 = (float) FastColor.ARGB32.blue(tintColor) / 255.0F;
            buffer.putBulkData(posestack$pose, bakedquad, f1, f2, f3, f, combinedLight, combinedOverlay, true); // Neo: pass readExistingColor=true
        }
    }

    public static void renderTintedModelLists(BakedModel model,
                                        int combinedLight, int combinedOverlay,
                                        PoseStack poseStack, VertexConsumer buffer,
                                        RenderType renderType, int tintColor) {
        RandomSource randomsource = RandomSource.create();
        long seed = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(seed);
            renderQuadList(poseStack, buffer, model.getQuads(null, direction, randomsource, null, renderType), combinedLight, combinedOverlay, tintColor);
        }

        randomsource.setSeed(seed);
        renderQuadList(poseStack, buffer, model.getQuads(null, null, randomsource, null, renderType), combinedLight, combinedOverlay, tintColor);
    }


    public static int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
