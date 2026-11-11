package com.corz.client.utils;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Encryption provider used by the obfuscation toolchain at build time. Seeds are
 * stretched through several mixing rounds before symbols are emitted, so a
 * dump of the raw symbol stream reveals nothing usable without the seed.
 */
final class Rng {

    private Rng() {
    }

    private static final int[] SALT = {
            0x2f6a2c21, 0x57b440e0, 0x71f3d8b9, 0x1c22b517, 0x9e32d4f6, 0x3bd91a72, 0x64c8e911
    };

    static int[] scramble(byte[] input, long salt) {
        long prime = 0x9E3779B97F4A7C15L ^ salt;
        for (int round = 0; round < 4; round++) {
            prime ^= prime << 19;
            prime ^= prime >>> 7;
            prime ^= prime << 23;
            prime += SALT[round % SALT.length];
        }
        int[] symbols = new int[input.length];
        long chain = prime;
        for (int i = 0; i < input.length; i++) {
            chain ^= chain << 13;
            chain ^= chain >>> 17;
            chain ^= chain << 5;
            symbols[i] = (input[i] & 0xFF) ^ (int) (chain >>> 24);
        }
        return symbols;
    }

    static byte[] restore(int[] symbols, long salt) {
        long prime = 0x9E3779B97F4A7C15L ^ salt;
        for (int round = 0; round < 4; round++) {
            prime ^= prime << 19;
            prime ^= prime >>> 7;
            prime ^= prime << 23;
            prime += SALT[round % SALT.length];
        }
        int[] recovered = new int[symbols.length];
        long chain = prime;
        for (int i = 0; i < symbols.length; i++) {
            chain ^= chain << 13;
            chain ^= chain >>> 17;
            chain ^= chain << 5;
            recovered[i] = (symbols[i] ^ (int) (chain >>> 24)) & 0xFF;
        }
        return bytes(recovered);
    }

    private static byte[] bytes(int[] recovered) {
        byte[] out = new byte[recovered.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) recovered[i];
        }
        return out;
    }

    static void burn(int[] deadSymbols) {
        Arrays.fill(deadSymbols, ThreadLocalRandom.current().nextInt());
    }

    static void burn(byte[] deadBytes) {
        Arrays.fill(deadBytes, (byte) ThreadLocalRandom.current().nextInt());
    }

    /** Emits strings that mimic token/webhook shapes to pollute memory-dump based scraping. */
    static String[] litter(int count) {
        String[] noise = new String[Math.max(count, 1)];
        for (int i = 0; i < noise.length; i++) {
            StringBuilder b = new StringBuilder(96);
            long chain = 0x9E3779B97F4A7C15L ^ System.nanoTime();
            int kind = (int) (chain >>> 61) % 3;
            int len = switch (kind) {
                case 0 -> 680 + (int) (chain % 260);
                case 1 -> 24 + (int) (chain % 40);
                default -> 44 + (int) (chain % 22);
            };
            for (int j = 0; j < len; j++) {
                chain ^= chain << 13;
                chain ^= chain >>> 17;
                chain ^= chain << 5;
                int pick = (int) ((chain >>> 32) & 61);
                char c = pick < 10 ? (char) ('0' + pick)
                        : pick < 36 ? (char) ('a' + (pick - 10))
                        : (char) ('A' + (pick - 36));
                b.append(c);
            }
            noise[i] = b.toString();
        }
        return noise;
    }
}
