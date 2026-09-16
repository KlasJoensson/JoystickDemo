package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class BleBridge {

    private final String pythonExecutable;
    private final String scriptPath;

    public BleBridge(String pythonExecutable, String scriptPath) {
        this.pythonExecutable = pythonExecutable;
        this.scriptPath = scriptPath;
    }

    public void listen(String deviceAddress, Consumer<String> onLine) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(pythonExecutable, scriptPath, deviceAddress).start();

        Thread errorReader = new Thread(() -> {
            try (BufferedReader err = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = err.readLine()) != null) {
                    System.err.println("[ble_bridge] " + line);
                }
            } catch (IOException ignored) {
            }
        });
        errorReader.setDaemon(true);
        errorReader.start();

        try (BufferedReader out = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = out.readLine()) != null) {
                onLine.accept(line);
            }
        }

        process.waitFor();
    }
}
