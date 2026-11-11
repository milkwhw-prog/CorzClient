package com.corz.client.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.Pcm;

final class OggDecoder {
   private OggDecoder() {
   }

   static Pcm getStbVorbis(byte[] stbVorbisBytes)  throws java.io.IOException {
      ByteBuffer memAlloc = MemoryUtil.memAlloc(stbVorbisBytes.length);

      Pcm stbVorbis;
      try {
         memAlloc.put(stbVorbisBytes).flip();
         int[] ints = new int[1];
         long stb_vorbis_open_memory = STBVorbis.stb_vorbis_open_memory(memAlloc, ints, null);
         if (stb_vorbis_open_memory == 0L) {
            throw new IOException("stb_vorbis error " + ints[0]);
         }

         try {
            STBVorbisInfo malloc = STBVorbisInfo.malloc();

            int stbVorbisFactor;
            int stbVorbisValue;
            try {
               STBVorbis.stb_vorbis_get_info(stb_vorbis_open_memory, malloc);
               stbVorbisFactor = malloc.channels();
               stbVorbisValue = malloc.sample_rate();
            } catch (Throwable e) {
               if (malloc != null) {
                  try {
                     malloc.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (malloc != null) {
               malloc.close();
            }

            if (stbVorbisFactor < 1 || stbVorbisFactor > 2 || stbVorbisValue <= 0) {
               throw new IOException("unsupported OGG layout");
            }

            int stbVorbisStreamLengthInSamples = STBVorbis.stb_vorbis_stream_length_in_samples(stb_vorbis_open_memory);
            if (stbVorbisStreamLengthInSamples <= 0) {
               throw new IOException("empty OGG");
            }

            short[] shorts = new short[stbVorbisStreamLengthInSamples * stbVorbisFactor];
            int stbVorbisBound = STBVorbis.stb_vorbis_get_samples_short_interleaved(stb_vorbis_open_memory, stbVorbisFactor, shorts);
            if (stbVorbisBound < stbVorbisStreamLengthInSamples) {
               short[] copyOf = new short[Math.max(0, stbVorbisBound) * stbVorbisFactor];
               System.arraycopy(shorts, 0, copyOf, 0, copyOf.length);
               shorts = copyOf;
            }

            stbVorbis = new Pcm(shorts, stbVorbisFactor, stbVorbisValue);
         } finally {
            STBVorbis.stb_vorbis_close(stb_vorbis_open_memory);
         }
      } finally {
         MemoryUtil.memFree(memAlloc);
      }

      return stbVorbis;
   }
}
