package org.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MultipartBodyPublisher {

    public static byte[] build(File file, String boundary) throws IOException {
        List<byte[]> byteArrays = new ArrayList<>();

        String LINE = "\r\n";

        // Header
        byteArrays.add(("--" + boundary + LINE).getBytes(StandardCharsets.UTF_8));
        byteArrays.add((
                "Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getName() + "\"" + LINE
        ).getBytes(StandardCharsets.UTF_8));
        byteArrays.add(("Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" + LINE + LINE)
                .getBytes(StandardCharsets.UTF_8));

        // File content
        byteArrays.add(Files.readAllBytes(file.toPath()));
        byteArrays.add(LINE.getBytes(StandardCharsets.UTF_8));

        // End boundary
        byteArrays.add(("--" + boundary + "--" + LINE)
                .getBytes(StandardCharsets.UTF_8));

        return join(byteArrays);
    }

    private static byte[] join(List<byte[]> parts) {
        int size = parts.stream().mapToInt(p -> p.length).sum();
        byte[] result = new byte[size];
        int pos = 0;

        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, pos, part.length);
            pos += part.length;
        }
        return result;
    }
}
