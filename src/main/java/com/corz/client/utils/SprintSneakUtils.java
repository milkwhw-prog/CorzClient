package com.corz.client.utils;

public final class SprintSneakUtils {
   private SprintSneakUtils() {
   }

   public static SprintSneakUtils.Write getSprintSneak(SprintSneakUtils.Write write, boolean sprintSneakFlag, SprintSneakUtils.Write write2, boolean recoveryHandled) {
      SprintSneakUtils.Write write3 = write2 == null ? SprintSneakUtils.Write.INSTANCE : write2;
      if (recoveryHandled) {
         return new SprintSneakUtils.Write(write.forward(), write.jump(), write.sprint(), write.sneak());
      } else {
         boolean sprintSneakFlag3 = sprintSneakFlag ? write.sneak() : write3.sneak();
         boolean sprintSneakFlag4 = write.jump() && sprintSneakFlag;
         boolean sprintSneakFlag5 = sprintSneakFlag ? write.sprint() : write3.sprint();
         if (sprintSneakFlag4 && !write3.sprint()) {
            sprintSneakFlag5 = false;
         }

         return new SprintSneakUtils.Write(write.forward(), sprintSneakFlag4, sprintSneakFlag5, sprintSneakFlag3);
      }
   }

   public static boolean canSprintJump(SprintSneakUtils.Write write, SprintSneakUtils.Write sprintSneak) {
      return write.jump() != sprintSneak.jump() || write.sprint() != sprintSneak.sprint() || write.sneak() != sprintSneak.sneak();
   }

   public record Write(boolean forward, boolean jump, boolean sprint, boolean sneak) {
      public static final SprintSneakUtils.Write INSTANCE = new SprintSneakUtils.Write(false, false, false, false);
   }
}
