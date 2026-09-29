package com.knightecgroup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

public class DevicePickerDialog extends JDialog {

    private final BleBridge bridge;
    private final double scanTimeoutSeconds;
    private final DefaultListModel<BleDevice> listModel = new DefaultListModel<>();
    private final JList<BleDevice> deviceList = new JList<>(listModel);
    private final JLabel statusLabel = new JLabel("Scanning for BLE devices...", JLabel.CENTER);
    private final JButton connectButton = new JButton("Connect");
    private final JButton rescanButton = new JButton("Rescan");
    private final JButton keyboardButton = new JButton("Use keyboard");
    private String selectedAddress;

    public DevicePickerDialog(Frame owner, BleBridge bridge, double scanTimeoutSeconds) {
        super(owner, "Select BLE Device", true);
        this.bridge = bridge;
        this.scanTimeoutSeconds = scanTimeoutSeconds;

        setLayout(new BorderLayout(8, 8));
        setSize(420, 360);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        add(statusLabel, BorderLayout.NORTH);
        add(buildListPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        startScan();
    }

    private JScrollPane buildListPanel() {
        deviceList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BleDevice(String address, String name)) {
                    setText(name + "  (" + address + ")");
                }
                return c;
            }
        });
        deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceList.addListSelectionListener(e -> connectButton.setEnabled(deviceList.getSelectedIndex() >= 0));
        deviceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && deviceList.getSelectedIndex() >= 0) {
                    confirmSelection();
                }
            }
        });
        deviceList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmSelection");
        deviceList.getActionMap().put("confirmSelection", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                confirmSelection();
            }
        });

        return new JScrollPane(deviceList);
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        connectButton.setEnabled(false);
        connectButton.addActionListener(e -> confirmSelection());
        rescanButton.addActionListener(e -> startScan());
        keyboardButton.addActionListener(e -> useKeyboard());
        buttonPanel.add(rescanButton);
        buttonPanel.add(connectButton);
        buttonPanel.add(keyboardButton);
        return buttonPanel;
    }

    private void useKeyboard() {
        selectedAddress = "keyboard";
        dispose();
    }

    private void confirmSelection() {
        BleDevice selected = deviceList.getSelectedValue();
        if (selected != null) {
            selectedAddress = selected.address();
            dispose();
        }
    }

    private void startScan() {
        listModel.clear();
        connectButton.setEnabled(false);
        rescanButton.setEnabled(false);
        statusLabel.setText("Scanning for BLE devices...");

        SwingWorker<List<BleDevice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BleDevice> doInBackground() throws Exception {
                return bridge.scan(scanTimeoutSeconds);
            }

            @Override
            protected void done() {
                rescanButton.setEnabled(true);
                try {
                    List<BleDevice> devices = get();
                    devices.forEach(listModel::addElement);
                    statusLabel.setText(devices.isEmpty()
                            ? "No devices found. Try Rescan."
                            : "Select a device:");
                } catch (Exception e) {
                    statusLabel.setText("Scan failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Blocks until the user picks a device. Closing the window exits the app.
     */
    public String pickDevice() {
        setVisible(true);
        return selectedAddress;
    }
}
