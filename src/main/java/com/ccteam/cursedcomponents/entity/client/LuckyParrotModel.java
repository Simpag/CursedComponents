package com.ccteam.cursedcomponents.entity.client;

import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LuckyParrotModel extends HierarchicalModel<LuckyParrot> {
    private final ModelPart luckyParrot;

    public LuckyParrotModel(ModelPart root) {
        this.luckyParrot = root.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition luckyParrot = partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(-4, -4).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(LuckyParrot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        luckyParrot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return luckyParrot;
    }

    public void renderOnShoulder(
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float limbSwing,
            float limbSwingAmount,
            float netHeadYaw,
            float headPitch,
            int tickCount
    ) {
        //this.prepare(ParrotModel.State.ON_SHOULDER);
        //this.setupAnim(ParrotModel.State.ON_SHOULDER, tickCount, limbSwing, limbSwingAmount, 0.0F, netHeadYaw, headPitch);
        //this.root.render(poseStack, buffer, packedLight, packedOverlay);
        this.luckyParrot.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
