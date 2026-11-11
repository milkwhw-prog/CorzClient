package com.corz.client.utils;

import java.util.Locale;

public final class BuilderFormat {
   private int indentLevel;
   private boolean needsComma;
   private final StringBuilder stringBuilder = new StringBuilder(1048576);

   public BuilderFormat writeBooleanField(String builderText, boolean builderFlag) {
      this.writeCommaIfNeeded();
      this.writeIndent();
      this.stringBuilder.append('"').append(escapeJsonString(builderText)).append("\": ").append(builderFlag);
      this.needsComma = true;
      return this;
   }

   public BuilderFormat beginObjectField(String builderText) {
      this.writeCommaIfNeeded();
      this.writeIndent();
      this.stringBuilder.append('"').append(escapeJsonString(builderText)).append("\": {\n");
      this.indentLevel++;
      return this;
   }

   public BuilderFormat writeDoubleField(String builderRootText, double builderRootValue) {
      this.writeCommaIfNeeded();
      this.writeIndent();
      this.stringBuilder.append('"').append(escapeJsonString(builderRootText)).append("\": ").append(String.format(Locale.ROOT, "%.4f", builderRootValue));
      this.needsComma = true;
      return this;
   }

   public static String formatIntQuad(int builderFormatValue, int n2, int n3, int builderFormatValue4) {
      return "[" + builderFormatValue + "," + n2 + "," + n3 + "," + builderFormatValue4 + "]";
   }

   public BuilderFormat writeStringField(String builderText, String builderFormatText) {
      this.writeCommaIfNeeded();
      this.writeIndent();
      this.stringBuilder.append('"').append(escapeJsonString(builderText)).append("\": ");
      if (builderFormatText == null) {
         this.stringBuilder.append("null");
      } else {
         this.stringBuilder.append('"').append(escapeJsonString(builderFormatText)).append('"');
      }

      this.needsComma = true;
      return this;
   }

   private void writeIndent() {
      this.stringBuilder.append("  ".repeat(this.indentLevel));
   }

   public String finishJson() {
      this.stringBuilder.append("\n}\n");
      return this.stringBuilder.toString();
   }

   public static String formatIntTriple(int builderFormatValue, int maxValue, int intersectsCenterBound) {
      return "[" + builderFormatValue + "," + maxValue + "," + intersectsCenterBound + "]";
   }

   public BuilderFormat endArray() {
      this.stringBuilder.append('\n');
      this.indentLevel--;
      this.writeIndent();
      this.stringBuilder.append(']');
      this.needsComma = true;
      return this;
   }

   public BuilderFormat appendRawElement(String builderText) {
      if (this.needsComma) {
         this.stringBuilder.append(",\n");
         this.writeIndent();
      }

      this.stringBuilder.append(builderText);
      this.needsComma = true;
      return this;
   }

   public BuilderFormat() {
      this.stringBuilder.append("{\n");
      this.indentLevel = 1;
   }

   private static String escapeJsonString(String builderFormatText) {
      StringBuilder stringBuilder = new StringBuilder(builderFormatText.length() + 8);

      for (int i = 0; i < builderFormatText.length(); i++) {
         char charAt = builderFormatText.charAt(i);
         switch (charAt) {
            case '\t':
               stringBuilder.append("\\t");
               break;
            case '\n':
               stringBuilder.append("\\n");
               break;
            case '\r':
               stringBuilder.append("\\r");
               break;
            case '"':
               stringBuilder.append("\\\"");
               break;
            case '\\':
               stringBuilder.append("\\\\");
               break;
            default:
               if (charAt < ' ') {
                  stringBuilder.append(String.format("\\u%04x", Integer.valueOf(charAt)));
               } else {
                  stringBuilder.append(charAt);
               }
         }
      }

      return stringBuilder.toString();
   }

   public BuilderFormat beginArrayField(String builderText) {
      this.writeCommaIfNeeded();
      this.writeIndent();
      this.stringBuilder.append('"').append(escapeJsonString(builderText)).append("\": [\n");
      this.indentLevel++;
      this.writeIndent();
      return this;
   }

   private void writeCommaIfNeeded() {
      if (this.needsComma) {
         this.stringBuilder.append(",\n");
      }

      this.needsComma = false;
   }

   public BuilderFormat endObject() {
      this.stringBuilder.append('\n');
      this.indentLevel--;
      this.writeIndent();
      this.stringBuilder.append('}');
      this.needsComma = true;
      return this;
   }

   public BuilderFormat writeLongField(String builderText, long builderValue) {
      this.writeCommaIfNeeded();
      this.writeIndent();
      this.stringBuilder.append('"').append(escapeJsonString(builderText)).append("\": ").append(builderValue);
      this.needsComma = true;
      return this;
   }
}
