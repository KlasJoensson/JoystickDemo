package com.knightecgroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BleBridge {

    private final String pythonExecutable;
    private final String scriptPath;

    public BleBridge(String pythonExecutable, String scriptPath) {
        this.pythonExecutable = pythonExecutable;
        this.scriptPath = scriptPath;
    }

    public List<BleDevice> scan(double timeoutSeconds) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(pythonExecutable, scriptPath, "scan",
                String.valueOf(timeoutSeconds)).start();

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

        List<BleDevice> devices = new ArrayList<>();
        try (BufferedReader out = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = out.readLine()) != null) {
                if (line.startsWith("DEVICE ")) {
                    String[] parts = line.split(" ", 3);
                    devices.add(new BleDevice(parts[1], parts.length > 2 ? parts[2] : "Unknown"));
                } else if (line.equals("SCAN_DONE")) {
                    break;
                } else {
                    System.err.println("[ble_bridge] " + line);
                }
            }
        }

        process.waitFor();
        return devices;
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
