package com.corz.client.utils;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.PlayerCombatState;

public interface RoutedBreachingApi {
   float getRemainingRouteHp();

   double getEstimatedTicksRemaining();

   int getMaxDrop();

   AnchorRoutePlanner.Mode getRouteMode();

   int getTimeUnreachable();

   PlayerCombatState getImmuneFire();
}
