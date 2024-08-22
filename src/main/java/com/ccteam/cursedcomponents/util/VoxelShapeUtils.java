package com.ccteam.cursedcomponents.util;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtils {
    public static VoxelShape combineShapes(VoxelShape... shapes) {
        VoxelShape ret = Shapes.empty();

        for (VoxelShape shape : shapes) {
            ret = Shapes.joinUnoptimized(ret, shape, BooleanOp.OR);
        }

        return ret.optimize();
    }
}
