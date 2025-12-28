import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {

        // Если аргументов нет — запускаем самотест
        if (args.length == 0) {
            selfTest();
            return;
        }

        // ================== СКРЫТИЕ ==================
        if (args[0].equalsIgnoreCase("hide")) {
            // args: hide cover.bmp secret.bin result.bmp password
            if (args.length != 5) {
                printUsage();
                return;
            }

            BmpImage img = new BmpImage(args[1]);
            byte[] secret = Files.readAllBytes(Path.of(args[2]));

            // XOR-шифрование
            byte[] encrypted = CryptoUtils.xor(secret, args[4]);

            // payload = [4 байта длины][зашифрованные данные]
            byte[] payload = new byte[4 + encrypted.length];
            payload[0] = (byte) (encrypted.length >>> 24);
            payload[1] = (byte) (encrypted.length >>> 16);
            payload[2] = (byte) (encrypted.length >>> 8);
            payload[3] = (byte) (encrypted.length);

            System.arraycopy(encrypted, 0, payload, 4, encrypted.length);

            // Проверка вместимости
            if (payload.length > img.capacityBytes()) {
                throw new IllegalArgumentException(
                        "Недостаточная вместимость BMP-контейнера"
                );
            }

            byte[] pixels = img.pixels();
            StegoLSB.embed(pixels, payload);
            img.setPixels(pixels);
            img.save(args[3]);

            System.out.println("Скрытие выполнено успешно");
        }

        // ================== ИЗВЛЕЧЕНИЕ ==================
        else if (args[0].equalsIgnoreCase("extract")) {
            // args: extract stego.bmp output.bin password
            if (args.length != 4) {
                printUsage();
                return;
            }

            BmpImage img = new BmpImage(args[1]);
            byte[] pixels = img.pixels();

            // Читаем длину (4 байта = 32 бита = 32 пикселя)
            byte[] lenBytes = StegoLSB.extract(pixels, 4);
            int length =
                    ((lenBytes[0] & 0xFF) << 24) |
                    ((lenBytes[1] & 0xFF) << 16) |
                    ((lenBytes[2] & 0xFF) << 8) |
                    (lenBytes[3] & 0xFF);

            if (length <= 0 || length > img.capacityBytes()) {
                throw new IllegalArgumentException("Некорректная длина данных");
            }

            // Извлекаем зашифрованные данные
            byte[] encrypted = StegoLSB.extract(
                    Arrays.copyOfRange(pixels, 4 * 8, pixels.length),
                    length
            );

            // XOR-расшифрование
            byte[] decrypted = CryptoUtils.xor(encrypted, args[3]);

            Files.write(Path.of(args[2]), decrypted);
            System.out.println("Извлечение выполнено успешно");
        }

        else {
            printUsage();
        }
    }

    // ================== САМОТЕСТ ==================
    private static void selfTest() {
        String message = "TEST MESSAGE";
        String password = "password";

        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        byte[] enc = CryptoUtils.xor(data, password);
        byte[] dec = CryptoUtils.xor(enc, password);

        String restored = new String(dec, StandardCharsets.UTF_8);

        if (!message.equals(restored)) {
            throw new RuntimeException("Self-test FAILED");
        }

        System.out.println("Self-test OK (XOR + UTF-8)");
    }

    // ================== USAGE ==================
    private static void printUsage() {
        System.out.println("Использование:");
        System.out.println("  hide <cover.bmp> <secret.bin> <result.bmp> <password>");
        System.out.println("  extract <stego.bmp> <output.bin> <password>");
    }
}
