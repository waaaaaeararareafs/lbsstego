import java.util.Arrays;

public class StegoLSB {

    public static void embed(byte[] pixels, byte[] secret) {
        int bits = secret.length * 8;
        if (bits > pixels.length)
            throw new IllegalArgumentException("Недостаточная вместимость контейнера");

        int bitIndex = 0;
        for (byte b : secret) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >> i) & 1;
                pixels[bitIndex] = (byte) ((pixels[bitIndex] & 0xFE) | bit);
                bitIndex++;
            }
        }
    }

    public static byte[] extract(byte[] pixels, int length) {
        byte[] result = new byte[length];
        int bitIndex = 0;

        for (int i = 0; i < length; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                b = (byte) ((b << 1) | (pixels[bitIndex] & 1));
                bitIndex++;
            }
            result[i] = b;
        }
        return result;
    }
}
