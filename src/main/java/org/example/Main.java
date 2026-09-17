package org.example;

public class Main {


    // We know the input is a 32 bit integer encode as a 8 char string, with the bytes in reverse order
    private static int convertToInteger(String str) {
        StringBuilder reversedString = new StringBuilder();
        reversedString.append(str.substring(6,8));
        reversedString.append(str.substring(4,6));
        reversedString.append(str.substring(2,4));
        reversedString.append(str.substring(0,2));

        return Integer.parseInt(reversedString.toString(), 16);
    }

    private static void interpretResponse(int res) {
        switch (res) {
            case 1:
                IO.println("Stick up!");
                break;
            case 2:
                IO.println("Stick down!");
                break;
            case 4:
                IO.println("Stick left!");
                break;
            case 8:
                IO.println("Stick Right!");
                break;
            case 16:
                IO.println("Fire!");
                break;
            case 42:
                IO.println("Got first message!");
                break;
            case 2042:
                IO.println("Button 0 on HW pressed!");
                break;
            default:
                IO.println("WTF! (got: " + res + ")");
        }
    }

    static void main() throws Exception {
        String address = System.getenv().getOrDefault("BLE_DEVICE_ADDRESS", "EA:87:17:89:58:94");
        String pythonExecutable = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";
        BleBridge bridge = new BleBridge(pythonExecutable, "scripts/ble_bridge.py");
        GUI myGUI = new GUI();
        myGUI.createWindow();
        IO.println("Connecting to BLE device " + address + " ...");
        bridge.listen(address, line -> {
            if (line.startsWith("NOTIFY ")) {
                String[] parts = line.split(" ", 3);
                int res = convertToInteger(parts[2]);
                IO.println("Message from " + parts[1] + ": " + parts[2] +" -> " + res);
                myGUI.updateLabel(res);
                interpretResponse(res);
            } else {
                IO.println("[ble] " + line);
            }
        });
    }
}
