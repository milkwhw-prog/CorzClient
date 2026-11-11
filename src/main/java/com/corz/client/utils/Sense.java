package com.corz.client.utils;

public enum Sense {
   STRUCTURE("Build"),
   LIGHT("Light"),
   LIFE("Life"),
   ACTIVITY("Activity"),
   PRESENCE("Players");
   public final String senseText;

   private Sense(String text) {
      this.senseText = text;
   }
}
