package com.corz.client.config;

import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class XorEncryptedString implements CharSequence, AutoCloseable {
   private final char[] keyChars;
   private boolean closed = false;
   private final char[] encryptedChars;
   private static final SecureRandom SECURE_RANDOM = new SecureRandom();
   private final int charCount;

   @NotNull
   public String formatText() {
      this.ensureNotClosed();
      return this.toString();
   }

   @Override
   public int hashCode() {
      this.ensureNotClosed();
      int hashCode = 0;

      for (int i = 0; i < this.charCount; i++) {
         hashCode = 31 * hashCode + this.charAt(i);
      }

      return hashCode;
   }

   private void ensureNotClosed() {
      if (this.closed) {
         throw new IllegalStateException("This EncryptedString has been closed and cannot be used");
      }
   }

   public static XorEncryptedString fromString(String encryptedNeitherText) {
      return new XorEncryptedString(encryptedNeitherText);
   }

   public XorEncryptedString(char[] encryptedNeitherChars, char[] encryptedNeitherChars2) {
      if (encryptedNeitherChars == null || encryptedNeitherChars2 == null) {
         throw new IllegalArgumentException("Neither encrypted value nor key can be null");
      } else if (encryptedNeitherChars2.length == 0) {
         throw new IllegalArgumentException("Encryption key cannot be empty");
      } else {
         this.charCount = encryptedNeitherChars.length;
         this.encryptedChars = Arrays.copyOf(encryptedNeitherChars, encryptedNeitherChars.length);
         this.keyChars = Arrays.copyOf(encryptedNeitherChars2, encryptedNeitherChars2.length);
      }
   }

   public CharBuffer toReadOnlyBuffer() {
      this.ensureNotClosed();
      CharBuffer allocate = CharBuffer.allocate(this.charCount);

      for (int i = 0; i < this.charCount; i++) {
         allocate.put(i, this.charAt(i));
      }

      allocate.flip();
      return allocate.asReadOnlyBuffer();
   }

   @Override
   public boolean equals(Object obj) {
      this.ensureNotClosed();
      if (this == obj) {
         return true;
      } else if (!(obj instanceof CharSequence)) {
         return false;
      } else if (this.charCount != ((CharSequence)obj).length()) {
         return false;
      } else {
         for (int i = 0; i < this.charCount; i++) {
            if (this.charAt(i) != ((CharSequence)obj).charAt(i)) {
               return false;
            }
         }

         return true;
      }
   }

   @NotNull
   @Override
   public CharSequence subSequence(int subSequenceLimit, int subSequenceLimit2) {
      this.ensureNotClosed();
      if (subSequenceLimit >= 0 && subSequenceLimit2 <= this.charCount && subSequenceLimit <= subSequenceLimit2) {
         int deltaN = subSequenceLimit2 - subSequenceLimit;
         char[] chars = new char[deltaN];
         System.arraycopy(this.encryptedChars, subSequenceLimit, chars, 0, deltaN);
         char[] chars2 = new char[deltaN];

         for (int i = 0; i < deltaN; i++) {
            chars2[i] = this.keyChars[(subSequenceLimit + i) % this.keyChars.length];
         }

         xorInPlace(chars, this.keyChars, 0, deltaN);
         xorInPlace(chars, chars2, 0, deltaN);
         return new XorEncryptedString(chars, chars2);
      } else {
         throw new IndexOutOfBoundsException("Invalid subsequence range: " + subSequenceLimit + " to " + subSequenceLimit2 + " (length: " + this.charCount);
      }
   }

   @Override
   public int length() {
      this.ensureNotClosed();
      return this.charCount;
   }

   @Override
   public void close() {
      if (!this.closed) {
         Arrays.fill(this.encryptedChars, '\u0000');
         Arrays.fill(this.keyChars, '\u0000');
         this.closed = true;
      }
   }

   @Override
   public char charAt(int index) {
      this.ensureNotClosed();
      if (index >= 0 && index < this.charCount) {
         return (char)(this.encryptedChars[index] ^ this.keyChars[index % this.keyChars.length]);
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + this.charCount);
      }
   }

   public static XorEncryptedString fromEncryptedAndKey(String neitherEncryptedText, String neitherEncryptedText2) {
      if (neitherEncryptedText != null && neitherEncryptedText2 != null) {
         return new XorEncryptedString(neitherEncryptedText.toCharArray(), neitherEncryptedText2.toCharArray());
      } else {
         throw new IllegalArgumentException("Neither encrypted data nor key can be null");
      }
   }

   @NotNull
   @Override
   public String toString() {
      this.ensureNotClosed();
      char[] chars = new char[this.charCount];

      for (int i = 0; i < this.charCount; i++) {
         chars[i] = this.charAt(i);
      }

      String stringText = new String(chars);
      Arrays.fill(chars, '\u0000');
      return stringText;
   }

   private static void xorInPlace(char[] encryptedNeitherChars, char[] encryptedNeitherChars2, int encryptedNeitherValue, int encryptedNeitherLimit) {
      for (int i = 0; i < encryptedNeitherLimit; i++) {
         int index = encryptedNeitherValue + i;
         encryptedNeitherChars[index] ^= encryptedNeitherChars2[i % encryptedNeitherChars2.length];
      }
   }

   private static char[] generateRandomKey(int secureRandomSize) {
      char[] chars = new char[secureRandomSize];

      for (int i = 0; i < secureRandomSize; i++) {
         chars[i] = (char)SECURE_RANDOM.nextInt(65536);
      }

      return chars;
   }

   public XorEncryptedString(String encryptedNeitherText) {
      if (encryptedNeitherText == null) {
         throw new IllegalArgumentException("Input string cannot be null");
      } else {
         this.charCount = encryptedNeitherText.length();
         this.keyChars = generateRandomKey(Math.min(this.charCount, 128));
         this.encryptedChars = new char[this.charCount];
         encryptedNeitherText.getChars(0, this.charCount, this.encryptedChars, 0);
         xorInPlace(this.encryptedChars, this.keyChars, 0, this.charCount);
      }
   }
}
