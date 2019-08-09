package tonmat.util;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class ResourceManager {
    private ResourceManager() {
    }

    public static URL get(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource);
    }

    public static InputStream open(String resource) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }

    public static String getString(String resource) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(open(resource), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteBuffer getBuffer(String resource) {
        try (final BufferedInputStream stream = new BufferedInputStream(open(resource))) {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final byte[] buffer = new byte[8192];
            int len;
            while ((len = stream.read(buffer)) > 0)
                bytes.write(buffer, 0, len);
            final ByteBuffer bb = BufferUtils.createByteBuffer(bytes.size());
            bb.put(bytes.toByteArray()).flip();
            return bb;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
