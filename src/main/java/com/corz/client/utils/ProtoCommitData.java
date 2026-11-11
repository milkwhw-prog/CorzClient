package com.corz.client.utils;

import com.google.gson.JsonObject;
import java.util.Locale;

public record ProtoCommitData(
   int protocolVersion, String minecraftVersion, String plannerGeneration, String gitCommit, String buildDigest, String schematicDigest, String transformDigest
) {

   public String formatDigestVersion(ProtoCommitData peer, ProtoCommitData.Verdict v) {
      if (v == ProtoCommitData.Verdict.OK) {
         return "compatible";
      } else {
         String digestVersionText;
         String digestVersionText2;
         switch (v) {
            case PROTOCOL_MISMATCH:
               digestVersionText = String.valueOf(this.protocolVersion);
               digestVersionText2 = peer == null ? "unknown" : String.valueOf(peer.protocolVersion);
               break;
            case MINECRAFT_MISMATCH:
               digestVersionText = this.minecraftVersion;
               digestVersionText2 = peer.minecraftVersion;
               break;
            case GENERATION_MISMATCH:
               digestVersionText = this.plannerGeneration;
               digestVersionText2 = peer.plannerGeneration;
               break;
            case BUILD_DIGEST_MISMATCH:
               digestVersionText = this.buildDigest;
               digestVersionText2 = peer.buildDigest;
               break;
            default:
               digestVersionText = this.schematicDigest;
               digestVersionText2 = peer.schematicDigest;
         }

         return "MultiBuilder version mismatch (" + v + ")\n  Host: " + digestVersionText + "\n  You:  " + digestVersionText2;
      }
   }

   public boolean canDigestTransform(ProtoCommitData peer) {
      return peer != null && !canFlag(this.transformDigest, peer.transformDigest);
   }

   public boolean canFlag(ProtoCommitData peer) {
      return this.getMismatchDigest(peer) == ProtoCommitData.Verdict.OK;
   }

   public static ProtoCommitData getProtoBuild(JsonObject o) {
      if (o != null && o.has("proto") && o.has("build")) {
         try {
            return new ProtoCommitData(
               o.get("proto").getAsInt(),
               formatText(o, "mc"),
               formatText(o, "gen"),
               formatText(o, "commit"),
               formatText(o, "build"),
               formatText(o, "schem"),
               formatText(o, "xform")
            );
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String formatText(JsonObject k, String o) {
      return k.has(o) ? k.get(o).getAsString() : "";
   }

   private static boolean canFlag(String a, String b) {
      return a == null ? b == null : a.equals(b);
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "protocol=%d mc=%s generation=%s commit=%s build=%s schematic=%s transform=%s",
         this.protocolVersion,
         this.minecraftVersion,
         this.plannerGeneration,
         this.gitCommit,
         this.buildDigest,
         this.schematicDigest,
         this.transformDigest
      );
   }

   public JsonObject getPropertyCommit() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("proto", this.protocolVersion);
      jsonObject.addProperty("mc", this.minecraftVersion);
      jsonObject.addProperty("gen", this.plannerGeneration);
      jsonObject.addProperty("commit", this.gitCommit);
      jsonObject.addProperty("build", this.buildDigest);
      jsonObject.addProperty("schem", this.schematicDigest);
      jsonObject.addProperty("xform", this.transformDigest);
      return jsonObject;
   }

   public ProtoCommitData.Verdict getMismatchDigest(ProtoCommitData peer) {
      if (peer == null) {
         return ProtoCommitData.Verdict.PROTOCOL_MISMATCH;
      } else if (peer.protocolVersion != this.protocolVersion) {
         return ProtoCommitData.Verdict.PROTOCOL_MISMATCH;
      } else if (!canFlag(this.minecraftVersion, peer.minecraftVersion)) {
         return ProtoCommitData.Verdict.MINECRAFT_MISMATCH;
      } else if (!canFlag(this.plannerGeneration, peer.plannerGeneration)) {
         return ProtoCommitData.Verdict.GENERATION_MISMATCH;
      } else if (!canFlag(this.buildDigest, peer.buildDigest)) {
         return ProtoCommitData.Verdict.BUILD_DIGEST_MISMATCH;
      } else {
         return !canFlag(this.schematicDigest, peer.schematicDigest) ? ProtoCommitData.Verdict.SCHEMATIC_MISMATCH : ProtoCommitData.Verdict.OK;
      }
   }

   public static enum Verdict {
      OK,
      PROTOCOL_MISMATCH,
      MINECRAFT_MISMATCH,
      GENERATION_MISMATCH,
      BUILD_DIGEST_MISMATCH,
      SCHEMATIC_MISMATCH;
   }
}
