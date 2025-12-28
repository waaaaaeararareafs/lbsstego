public class CryptoUtils {

    public static byte[] xor(byte[] data, String password) {
        byte[] key = password.getBytes();
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }

    // тест
    public static void selfTest() {
        String msg = "HELLO";
        String key = "KEY";

        byte[] enc = xor(msg.getBytes(), key);
        byte[] dec = xor(enc, key);

        if (!msg.equals(new String(dec))) {
            throw new RuntimeException("XOR тест не пройден");
        }
        System.out.println("XOR self-test OK");
    }
}

