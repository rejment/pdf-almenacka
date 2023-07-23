package com.bachata.kalender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Namnsdag {
    public static void main(String[] args) throws IOException {

        List<String> strings = Files.readAllLines(Paths.get("namnsdag.txt"));

        Map<String, String> names = new HashMap<>();
        strings.forEach(line -> {
            String[] s = line.split("    ");
            names.put(s[0], s[1]);
        });

    }
}
