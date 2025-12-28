import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BmpImage {

    private static final int HEADER = 54;
    private final byte[] data;

    public BmpImage(String path) throws IOException {
        data = Files.readAllBytes(Path.of(path));
        if (data[0] != 'B' || data[1] != 'M')
            throw new IllegalArgumentException("Не BMP");
    }

    public byte[] pixels() {
        byte[] p = new byte[data.length - HEADER];
        System.arraycopy(data, HEADER, p, 0, p.length);
        return p;
    }

    public void setPixels(byte[] p) {
        System.arraycopy(p, 0, data, HEADER, p.length);
    }

    public int capacityBytes() {
        return (data.length - HEADER) / 8;
    }

    public void save(String path) throws IOException {
        Files.write(Path.of(path), data);
    }
}
