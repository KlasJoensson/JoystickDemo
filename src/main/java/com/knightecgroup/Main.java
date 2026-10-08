package com.knightecgroup;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    private final static double SCAN_TIMEOUT_SECONDS = 5.0;
    private static boolean keyboardControl = false;
    private final static Game myGame = Game.initGame();
    private final static Logger log = Logger.getLogger(Main.class.getName());

    // We know the input is a '32-bit' integer encode as an 8 char string, with the bytes in reverse order
    private static int convertToInteger(String str) {
        String reversedString = str.substring(6, 8) +
                str.substring(4, 6) +
                str.substring(2, 4) +
                str.substring(0, 2);

        return Integer.parseInt(reversedString, 16);
    }

    private static void interpretResponse(int res) {
        switch (res) {
            case 1:
                log.finest("Stick up!");
                break;
            case 2:
                log.finest("Stick down!");
                break;
            case 4:
                log.finest("Stick left!");
                break;
            case 8:
                log.finest("Stick Right!");
                break;
            case 16:
                log.finest("Fire!");
                break;
            case 42:
                log.finest("Got first message!");
                break;
            case 2042:
                log.finest("Button 0 on HW pressed!");
                break;
            default:
                log.finest("WTF! (got: " + res + ")");
        }
    }

    static void main() {
        String pythonExecutable = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";
        BleBridge bridge = new BleBridge(pythonExecutable, "scripts/ble_bridge.py");

        String address = System.getenv("BLE_DEVICE_ADDRESS");
        if (address == null || address.isBlank()) {
            address = pickDeviceViaGui(bridge);
            if (address.equals("keyboard")) {
                keyboardControl = true;
            }
        }

        if (keyboardControl) {
            log.info("Uses the keyboard as input...");
            myGame.controlGame(21);
        } else {
            myGame.controlGame(22);
            log.info("Connecting to BLE device " + address + " ...");
            try {
                bridge.listen(address, line -> {
                    if (line.startsWith("NOTIFY ")) {
                        String[] parts = line.split(" ", 3);
                        int res = convertToInteger(parts[2]);
                        myGame.controlGame(res);
                        interpretResponse(res);
                    } else if (line.startsWith("DISCONNECTED")) {
                        myGame.disconnected();
                        log.info("[ble] " + line);
                    } else {
                        log.info("[ble] " + line);
                    }
                });
            } catch (IOException | InterruptedException e) {
                log.severe("[ble] ERROR: " + e.getMessage() );
            }
        }
    }

    private static String pickDeviceViaGui(BleBridge bridge) {
        DevicePickerDialog dialog = new DevicePickerDialog(null, bridge, SCAN_TIMEOUT_SECONDS);
        String address = dialog.pickDevice();
        if (address == null) {
            System.exit(0);
        }
        return address;
    }

}
