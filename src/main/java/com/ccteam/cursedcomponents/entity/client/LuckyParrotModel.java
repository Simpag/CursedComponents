package com.ccteam.cursedcomponents.entity.client;

import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LuckyParrotModel extends HierarchicalModel<LuckyParrot> {

    private final ModelPart luckyParrot;

    public LuckyParrotModel(ModelPart root) {
        this.luckyParrot = root.getChild("lucky_parrot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition luckyParrotDefinition = partdefinition.addOrReplaceChild("lucky_parrot", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = luckyParrotDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16, 10).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 5).addBox(-1.0F, -2.5F, -3.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 18).addBox(-0.5F, -1.5F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 18).addBox(-0.5F, -1.75F, -2.95F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, -8.0F, -0.5F));

        PartDefinition feather = head.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(0, 5).addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, -2.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition body = luckyParrotDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.5F, -1.0F));

        PartDefinition left_wing = luckyParrotDefinition.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(1.5F, -7.1F, -0.8F));

        PartDefinition left_wing_rotation = left_wing.addOrReplaceChild("left_wing_rotation", CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition right_wing = luckyParrotDefinition.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-1.5F, -7.1F, -0.8F));

        PartDefinition right_wing_rotation = right_wing.addOrReplaceChild("right_wing_rotation", CubeListBuilder.create().texOffs(8, 10).addBox(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition left_leg = luckyParrotDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 15).addBox(-2.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -2.0F, -1.0F));

        PartDefinition right_leg = luckyParrotDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 5).addBox(1.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -2.0F, -1.0F));

        PartDefinition tail = luckyParrotDefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.9F, 1.2F));

        return LayerDefinition.create(meshdefinition, 32, 32);
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
