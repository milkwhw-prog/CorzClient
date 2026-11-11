package com.corz.client.utils;

import java.util.ArrayList;
import org.joml.Matrix4f;
import com.corz.client.utils.C2Model;

public final class C2Frame {
   public static final String[] bladeDownTexts = new String[]{"blade2", "up", "down"};
   public static final String[] downBladeTexts = new String[]{"c2_blade", "c2_up", "c2_down"};

   public static Matrix4f[] getInvertMul(C2Model c2Model) {
      Matrix4f[] matrix4fs = getMatrix4fs(c2Model);
      Matrix4f invert = getInvert();
      Matrix4f[] matrix4fs2 = new Matrix4f[bladeDownTexts.length];

      for (int i = 0; i < matrix4fs2.length; i++) {
         int bladeTexts = getBladeTexts(c2Model, i);
         matrix4fs2[i] = bladeTexts < 0 ? null : new Matrix4f(matrix4fs[bladeTexts]).invert().mul(invert);
      }

      return matrix4fs2;
   }

   public static Matrix4f getInvert() {
      return getMul().invert();
   }

   public static Matrix4f[] getMatrix4fs(C2Model c2Model) {
      Matrix4f[] bonesMesh = c2Model.getBonesMesh();
      c2Model.handlePivotParent(c2Model.getBonesMeshMeshModelBones(), bonesMesh);
      return bonesMesh;
   }

   private C2Frame() {
   }

   public static int[] getBonesMesh(C2Model c2Model, int bonesMeshValue) {
      int bladeTexts = getBladeTexts(c2Model, bonesMeshValue);
      ArrayList arrayList = new ArrayList();
      if (bladeTexts >= 0) {
         for (int i = 0; i < c2Model.meshModelBones.length; i++) {
            for (int j = i; j >= 0; j = c2Model.meshModelBones[j].parent()) {
               if (j == bladeTexts) {
                  arrayList.add(i);
                  break;
               }
            }
         }
      }

      int[] ints = new int[arrayList.size()];

      for (int k = 0; k < ints.length; k++) {
         ints[k] = (Integer)arrayList.get(k);
      }

      return ints;
   }

   public static Matrix4f getMul() {
      return new Matrix4f().translate(-11.802F, 2.403F, 8.0F).scale(21.55F).mul(getMatrix4f());
   }

   public static Matrix4f getMatrix4f() {
      float matrix4fValue = -1.0F;
      return new Matrix4f(0.0F, 0.0F, matrix4fValue, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
   }

   public static int getBladeTexts(C2Model c2Model, int index) {
      return index >= 0 && index < bladeDownTexts.length ? c2Model.getBonesMesh(bladeDownTexts[index]) : -1;
   }
}
