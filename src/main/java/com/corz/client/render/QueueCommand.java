package com.corz.client.render;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class QueueCommand {
   public OrderedRenderCommandQueue commandQueue;
   public final Matrix4f projectionMatrix;
   public final MatrixStack matrices;
   public final float tickProgress;

   public QueueCommand(MatrixStack matrices, Matrix4f matrix4f2, float tickProgress) {
      this.matrices = matrices;
      this.projectionMatrix = matrix4f2;
      this.tickProgress = tickProgress;
   }
}
