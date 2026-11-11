package com.corz.client.utils;

import java.io.IOException;

public record Pcm(short[] samples, int channels, int rate) {

   public Pcm getChannelsRate(int startMs, int lengthMs) {
      int channelsSamples = this.getChannelsSamples();
      int channelsRateFactor = getValue((int)Math.round((double)startMs * this.rate / 1000.0), channelsSamples);
      int channelsRateValue = getValue(channelsRateFactor + (int)Math.round((double)lengthMs * this.rate / 1000.0), channelsSamples);
      short[] shorts = new short[(channelsRateValue - channelsRateFactor) * this.channels];
      System.arraycopy(this.samples, channelsRateFactor * this.channels, shorts, 0, shorts.length);
      Pcm pcm = new Pcm(shorts, this.channels, this.rate);
      pcm.updateChannelsSamples();
      return pcm;
   }

   private static short[] getValues(byte[] size, int off, int d, int bits)  throws java.io.IOException {
      switch (bits) {
         case 8:
            short[] shorts = new short[d];

            for (int i = 0; i < d; i++) {
               shorts[i] = (short)((size[off + i] & 255) - 128 << 8);
            }

            return shorts;
         case 16:
            short[] shorts2 = new short[d / 2];

            for (int j = 0; j < shorts2.length; j++) {
               shorts2[j] = (short)getValue(size, off + j * 2);
            }

            return shorts2;
         case 24:
            short[] shorts3 = new short[d / 3];

            for (int k = 0; k < shorts3.length; k++) {
               shorts3[k] = (short)(size[off + k * 3 + 1] & 255 | size[off + k * 3 + 2] << 8);
            }

            return shorts3;
         default:
            throw new IOException("unsupported WAV bit depth " + bits);
      }
   }

   private static int getValue(byte[] d, int off) {
      return d[off] & 0xFF | (d[off + 1] & 0xFF) << 8;
   }

   public int getChannelsSamples() {
      return this.channels <= 0 ? 0 : this.samples.length / this.channels;
   }

   void updateChannelsSamples() {
      int channelsSamples = this.getChannelsSamples();
      int channelsSamplesLimit = Math.min(channelsSamples, (int)Math.round(3.0 * this.rate / 1000.0));

      for (int i = 0; i < channelsSamplesLimit; i++) {
         int channelsSamplesFactor = channelsSamples - channelsSamplesLimit + i;
         double channelsSamplesFactor2 = (double)(channelsSamplesLimit - 1 - i) / channelsSamplesLimit;

         for (int j = 0; j < this.channels; j++) {
            int index = channelsSamplesFactor * this.channels + j;
            this.samples[index] = (short)Math.round(this.samples[index] * channelsSamplesFactor2);
         }
      }
   }

   private static int getValue(int max, int v) {
      return Math.max(0, Math.min(v, max));
   }

   private static int getValueForByteArrayInt(byte[] d, int off) {
      return d[off] & 0xFF | (d[off + 1] & 0xFF) << 8 | (d[off + 2] & 0xFF) << 16 | (d[off + 3] & 0xFF) << 24;
   }

   public static Pcm getWavRiff(byte[] data)  throws java.io.IOException {
      if (data.length >= 12 && canFlag(data, 0, "RIFF") && canFlag(data, 8, "WAVE")) {
         int wavRiffLimit = 0;
         int valueForByteArrayInt2 = 0;
         int wavRiffValue = 0;
         int wavRiffValue2 = 0;
         int wavRiffValue3 = 12;

         while (wavRiffValue3 + 8 <= data.length) {
            int valueForByteArrayInt = getValueForByteArrayInt(data, wavRiffValue3 + 4);
            int off = wavRiffValue3 + 8;
            if (valueForByteArrayInt < 0 || off + valueForByteArrayInt > data.length) {
               valueForByteArrayInt = data.length - off;
            }

            if (canFlag(data, wavRiffValue3, "fmt ") && valueForByteArrayInt >= 16) {
               wavRiffValue2 = getValue(data, off);
               wavRiffLimit = getValue(data, off + 2);
               valueForByteArrayInt2 = getValueForByteArrayInt(data, off + 4);
               wavRiffValue = getValue(data, off + 14);
               if (wavRiffValue2 == 65534 && valueForByteArrayInt >= 26) {
                  wavRiffValue2 = getValue(data, off + 24);
               }
            } else if (canFlag(data, wavRiffValue3, "data")) {
               if (wavRiffValue2 != 1) {
                  throw new IOException("WAV is not plain PCM (format " + wavRiffValue2 + ")");
               }

               if (wavRiffLimit >= 1 && wavRiffLimit <= 2 && valueForByteArrayInt2 > 0) {
                  return new Pcm(getValues(data, off, valueForByteArrayInt, wavRiffValue), wavRiffLimit, valueForByteArrayInt2);
               }

               throw new IOException("unsupported WAV layout");
            }

            wavRiffValue3 = off + valueForByteArrayInt + (valueForByteArrayInt & 1);
         }

         throw new IOException("WAV has no data chunk");
      } else {
         throw new IOException("not a WAV file");
      }
   }

   private static boolean canFlag(byte[] t, int off, String d) {
      if (off + 4 > t.length) {
         return false;
      } else {
         for (int i = 0; i < 4; i++) {
            if (t[off + i] != d.charAt(i)) {
               return false;
            }
         }

         return true;
      }
   }
}
