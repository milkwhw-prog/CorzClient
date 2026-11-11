package com.corz.client.utils;

import com.corz.client.gui.VertexGuiUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.FlameCorzUtils;
import com.corz.client.render.GhostBuilderUtils;
import com.corz.client.render.SamplerBuilderUtils;
import com.corz.client.render.TexBuilderUtils;
import com.corz.client.render.VertexBuilderUtils;

public class Utils170 {
   public static void update() {
      CorzRoundedUtils.updateRegisterBuild();
      VertexGuiUtils.updateTriangleFan();
      VertexBuilderUtils.updateRegisterBuild();
      FlameCorzUtils.updateRegisterBuild();
      TexBuilderUtils.update();
      SamplerBuilderUtils.update();
      GhostBuilderUtils.update();
   }
}
