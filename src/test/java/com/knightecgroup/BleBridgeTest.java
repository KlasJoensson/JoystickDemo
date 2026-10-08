package com.knightecgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BleBridgeTest {

    private static final String PYTHON_EXECUTABLE = "python3";
    private static final String SCRIPT_PATH = "scripts/ble_bridge.py";

    private RecordingProcessLauncher launcher;
    private BleBridge bridge;

    @BeforeEach
    void setUp() {
        launcher = new RecordingProcessLauncher();
        bridge = new BleBridge(PYTHON_EXECUTABLE, SCRIPT_PATH, launcher);
    }

    @Nested
    @DisplayName("scan")
    class Scan {

        @Test
        @DisplayName("launches the python script with the scan command and timeout")
        void launchesWithExpectedCommand() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess("SCAN_DONE\n", "");

            bridge.scan(5.0);

            assertEquals(List.of(PYTHON_EXECUTABLE, SCRIPT_PATH, "scan", "5.0"), launcher.lastCommand);
        }

        @Test
        @DisplayName("parses DEVICE lines into BleDevices")
        void parsesDeviceLines() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess(
                    "DEVICE AA:BB:CC:DD:EE:FF TestDevice\n"
                            + "DEVICE 11:22:33:44:55:66 OtherDevice\n"
                            + "SCAN_DONE\n",
                    "");

            List<BleDevice> devices = bridge.scan(5.0);

            assertEquals(List.of(
                    new BleDevice("AA:BB:CC:DD:EE:FF", "TestDevice"),
                    new BleDevice("11:22:33:44:55:66", "OtherDevice")), devices);
        }

        @Test
        @DisplayName("defaults the device name to Unknown when the line has no name")
        void defaultsMissingNameToUnknown() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess("DEVICE AA:BB:CC:DD:EE:FF\nSCAN_DONE\n", "");

            List<BleDevice> devices = bridge.scan(5.0);

            assertEquals(List.of(new BleDevice("AA:BB:CC:DD:EE:FF", "Unknown")), devices);
        }

        @Test
        @DisplayName("stops reading once SCAN_DONE is seen, ignoring anything after it")
        void stopsAtScanDone() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess(
                    "DEVICE AA:BB:CC:DD:EE:FF TestDevice\n"
                            + "SCAN_DONE\n"
                            + "DEVICE 11:22:33:44:55:66 OtherDevice\n",
                    "");

            List<BleDevice> devices = bridge.scan(5.0);

            assertEquals(List.of(new BleDevice("AA:BB:CC:DD:EE:FF", "TestDevice")), devices);
        }

        @Test
        @DisplayName("ignores stray output lines instead of treating them as devices")
        void ignoresStrayLines() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess(
                    "Scanning for 5.0 seconds...\n"
                            + "DEVICE AA:BB:CC:DD:EE:FF TestDevice\n"
                            + "SCAN_DONE\n",
                    "");

            List<BleDevice> devices = bridge.scan(5.0);

            assertEquals(List.of(new BleDevice("AA:BB:CC:DD:EE:FF", "TestDevice")), devices);
        }

        @Test
        @DisplayName("does not fail when the script writes to stderr")
        void toleratesStderrOutput() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess("SCAN_DONE\n", "bleak: adapter warning\n");

            List<BleDevice> devices = bridge.scan(5.0);

            assertTrue(devices.isEmpty());
        }
    }

    @Nested
    @DisplayName("listen")
    class Listen {

        @Test
        @DisplayName("launches the python script with the device address, without the scan command")
        void launchesWithExpectedCommand() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess("", "");

            bridge.listen("AA:BB:CC:DD:EE:FF", line -> { });

            assertEquals(List.of(PYTHON_EXECUTABLE, SCRIPT_PATH, "AA:BB:CC:DD:EE:FF"), launcher.lastCommand);
        }

        @Test
        @DisplayName("invokes the callback once per line, in order")
        void invokesCallbackPerLine() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess("UP\nDOWN\nFIRE\n", "");

            List<String> received = new java.util.ArrayList<>();
            bridge.listen("AA:BB:CC:DD:EE:FF", received::add);

            assertEquals(List.of("UP", "DOWN", "FIRE"), received);
        }

        @Test
        @DisplayName("does not fail when the script writes to stderr")
        void toleratesStderrOutput() throws IOException, InterruptedException {
            launcher.nextProcess = new FakeProcess("UP\n", "bleak: disconnected\n");

            List<String> received = new java.util.ArrayList<>();
            bridge.listen("AA:BB:CC:DD:EE:FF", received::add);

            assertEquals(List.of("UP"), received);
        }
    }

    private static class RecordingProcessLauncher implements ProcessLauncher {
        private List<String> lastCommand;
        private FakeProcess nextProcess;

        @Override
        public Process launch(List<String> command) {
            lastCommand = command;
            return nextProcess;
        }
    }

    private static class FakeProcess extends Process {
        private final InputStream stdout;
        private final InputStream stderr;

        FakeProcess(String stdout, String stderr) {
            this.stdout = new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8));
            this.stderr = new ByteArrayInputStream(stderr.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public OutputStream getOutputStream() {
            return OutputStream.nullOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return stdout;
        }

        @Override
        public InputStream getErrorStream() {
            return stderr;
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
        }
    }
}
