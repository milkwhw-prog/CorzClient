package com.corz.client.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Model;
import com.corz.client.utils.C2Clips;

public final class C2Viewmodel {

   public static Vector3f getClassicsTransform(C2Knife c2Knife, C2Model c2Model) {
      Vector3f vector3f = new Vector3f();
      if (c2Knife != null && !c2Knife.isClassics() && !"classics".equals(c2Knife.classics)) {
         C2Model.Clip unmodifiablemap = c2Model.unmodifiableMap.get(C2Clips.formatStaticIdle(c2Knife, c2Model));
         if (unmodifiablemap == null) {
            return vector3f;
         } else {
            float[] bonesMesh = c2Model.getBonesMeshMeshModelBones();
            unmodifiablemap.handleTracks(0.0F, bonesMesh);
            Matrix4f[] matrix4fs = c2Model.getBonesMesh();
            c2Model.handlePivotParent(bonesMesh, matrix4fs);
            Vector3f vector3f2 = new Vector3f();
            Vector3f vector3f3 = new Vector3f(Float.POSITIVE_INFINITY);
            Vector3f vector3f4 = new Vector3f(Float.NEGATIVE_INFINITY);
            Vector3f vector3f5 = new Vector3f();
            int classicsTransformCounter = 0;
            Matrix4f matrix4f = new Matrix4f();

            for (int i = 0; i < c2Model.meshModelBones.length; i++) {
               C2Model.Bone bone = c2Model.meshModelBones[i];
               if (!(Math.abs(matrix4fs[i].determinant3x3()) < 1.0E-10F)) {
                  c2Model.mulTranslationMatrix4f.mul(matrix4fs[i], matrix4f);
                  if (bone.arm() != 0) {
                     float[] classicsTransformValues = bone.grip() == null ? new float[3] : bone.grip();
                     vector3f2.add(matrix4f.transformPosition(classicsTransformValues[0], classicsTransformValues[1] - 0.75F, classicsTransformValues[2], vector3f5));
                     classicsTransformCounter++;
                  } else {
                     float[] vertices = bone.vertices();

                     for (int j = 0; j < vertices.length; j += 8) {
                        matrix4f.transformPosition(vertices[j], vertices[j + 1], vertices[j + 2], vector3f5);
                        vector3f3.min(vector3f5);
                        vector3f4.max(vector3f5);
                     }
                  }
               }
            }

            if (classicsTransformCounter != 0 && !(vector3f3.x > vector3f4.x)) {
               vector3f2.div(classicsTransformCounter);
               float classicsTransformBound = 1.3F + vector3f2.z;
               float classicsTransformLimit = -(vector3f3.z + vector3f4.z) / 2.0F;
               if (classicsTransformLimit > 0.0F) {
                  classicsTransformBound = Math.min(classicsTransformBound, classicsTransformLimit * (vector3f4.distance(vector3f3) / classicsTransformLimit / 0.62F - 1.0F));
               }

               float classicsTransformValue = Float.isFinite(classicsTransformBound) ? Math.clamp(classicsTransformBound, 0.0F, 0.6F) : 0.0F;
               float routedPartialValue = vector3f2.y + 0.41F * (classicsTransformValue - vector3f2.z);
               float classicsTransformValue3 = Float.isFinite(routedPartialValue) ? Math.clamp(routedPartialValue, 0.0F, 0.4F) : 0.0F;
               return vector3f.set(0.0F, -classicsTransformValue3, -classicsTransformValue);
            } else {
               return vector3f;
            }
         }
      } else {
         return vector3f;
      }
   }

   private C2Viewmodel() {
   }
}
