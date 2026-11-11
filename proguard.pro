-dontshrink
-dontoptimize
-dontwarn
# javac's own StackMapTable is correct; ProGuard's preverifier recomputes
# frames and mistypes invokedynamic results as Object, which the JVM rejects.
# Keep the original frames and skip recomputation entirely.
-dontpreverify
-allowaccessmodification
-overloadaggressively
-useuniqueclassmembernames

-dontnote **

-libraryjars 'C:\Users\dhaur\.gradle\caches\fabric-loom\1.21.11\minecraft-merged.jar'
-libraryjars 'C:\Users\dhaur\.gradle\caches\modules-2\files-2.1\net.fabricmc\fabric-loader\0.19.5\ff9e65cffca4a67f31523e1807fe0855940fcbfa\fabric-loader-0.19.5.jar'
-libraryjars 'C:\Users\dhaur\.gradle\caches\modules-2\files-2.1\net.fabricmc.fabric-api\fabric-api\0.141.3+1.21.11\50de67eb221f0b38216da8668b09ca311327040e\fabric-api-0.141.3+1.21.11.jar'

-keep class com.corz.client.CorzClient { *; }
-keep class com.corz.client.mixins.** { *; }
-keep interface com.corz.client.mixins.** { *; }

# Gson-serialized DTOs keep their shape; everything else renames.
-keep class ai.auth.LicenseToken { *; }
-keep class ai.auth.LicenseStatus { *; }
-keep class ai.auth.ApiResponse { *; }

# Enum machinery is read reflectively by the JDK (EnumMap.getEnumConstantsShared
# looks up "$VALUES" by name) — these names must never be renamed.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    public static final ** $VALUES;
}
-keepattributes Enum

# Entry annotations and mixin annotations must survive on renamed classes.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,LineNumberTable,SourceFile,Deprecated,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,StackMapTable

-adaptclassstrings com.corz.client.mixins.**

-repackageclasses 'com.corz.client'
-classobfuscationdictionary proguard-class-dict.txt
-obfuscationdictionary proguard-member-dict.txt
-packageobfuscationdictionary proguard-class-dict.txt
