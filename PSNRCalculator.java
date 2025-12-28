// Файл: PSNRCalculator.java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PSNRCalculator {
    
    private static final int BMP_HEADER_SIZE = 54;
    
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Использование: java PSNRCalculator <оригинал.bmp> <стего.bmp>");
            return;
        }
        
        byte[] originalBytes = Files.readAllBytes(Path.of(args[0]));
        byte[] stegoBytes = Files.readAllBytes(Path.of(args[1]));
        
        // Проверяем, что файлы имеют одинаковый размер
        if (originalBytes.length != stegoBytes.length) {
            System.out.println("Ошибка: размеры файлов не совпадают");
            return;
        }
        
        // Извлекаем только пиксельные данные (после заголовка)
        int pixelsStart = BMP_HEADER_SIZE;
        int pixelsLength = originalBytes.length - pixelsStart;
        
        if (pixelsLength <= 0) {
            System.out.println("Ошибка: некорректный размер файла");
            return;
        }
        
        // Рассчитываем MSE (среднеквадратическую ошибку)
        double mse = 0;
        for (int i = pixelsStart; i < originalBytes.length; i++) {
            int origPixel = originalBytes[i] & 0xFF;  // Преобразуем в беззнаковое
            int stegoPixel = stegoBytes[i] & 0xFF;
            int diff = origPixel - stegoPixel;
            mse += diff * diff;
        }
        mse /= pixelsLength;  // Среднее значение
        
        // Рассчитываем PSNR
        double psnr = 10 * Math.log10(255.0 * 255.0 / mse);
        
        System.out.printf("Результаты сравнения:%n");
        System.out.printf("  MSE: %.4f%n", mse);
        System.out.printf("  PSNR: %.2f дБ%n", psnr);
        
        // Интерпретация результата
        if (psnr > 40) {
            System.out.println("  Качество: отличное (изменения незаметны)");
        } else if (psnr > 30) {
            System.out.println("  Качество: хорошее (незначительные изменения)");
        } else {
            System.out.println("  Качество: низкое (изменения заметны)");
        }
    }
}
