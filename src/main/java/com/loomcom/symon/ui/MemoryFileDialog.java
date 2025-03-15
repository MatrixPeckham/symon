/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.ui;

import com.loomcom.symon.Bus;
import com.loomcom.symon.exceptions.MemoryAccessException;
import com.loomcom.symon.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author matri
 */
public class MemoryFileDialog {

    private final JDialog dialog;

    private final Bus bus;

    private int startLoc = 0xFF00;

    private int endLoc = 0xFFFF;

    private JTextField startField;

    private JTextField endField;

    private JLabel fileName;

    private JButton browse;

    private JButton accept;

    private JButton cancel;

    private File file = null;

    private boolean isSaving = true;

    private JFileChooser fileChooser;

    MemoryWindow parent;

    public MemoryFileDialog(MemoryWindow parent, boolean modal, Bus bus) {
        this.parent = parent;
        dialog = new JDialog(parent, modal);
        this.bus = bus;
        createUi();
    }

    private void createUi() {

        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        JLabel startLabel = new JLabel("Start Location");
        JLabel endLabel = new JLabel("End Location");
        JLabel fileLabel = new JLabel("File");

        startField = new JTextField(8);
        endField = new JTextField(8);
        fileName = new JLabel();
        browse = new JButton("Browse...");
        browse.addActionListener((ActionEvent al) -> {
            int result;
            if (isSaving) {
                result = fileChooser.showSaveDialog(dialog);
            } else {
                result = fileChooser.showOpenDialog(dialog);
            }
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                String displayName = file.getAbsolutePath();
                if (displayName.length() > 20) {
                    displayName = "..." + displayName.substring(displayName.
                            length() - 17);
                }
                fileName.setText(displayName);
            }
        });
        fileChooser = new JFileChooser(System.getProperty("user.dir"));

        startLabel.setLabelFor(startField);
        endLabel.setLabelFor(endField);
        fileLabel.setLabelFor(fileName);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(startLabel, constraints);

        constraints.gridx = 1;
        panel.add(startField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(endLabel, constraints);

        constraints.gridx = 1;
        panel.add(endField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(fileLabel, constraints);

        constraints.gridx = 1;
        panel.add(fileName, constraints);

        constraints.gridx = 2;
        panel.add(browse, constraints);

        dialog.getContentPane().add(panel, BorderLayout.PAGE_START);

        JPanel buttonPanel = new JPanel();

        cancel = new JButton("Cancel");
        cancel.addActionListener((ActionEvent e) -> {
            dialog.setVisible(false);
        });

        accept = new JButton("Accept");
        accept.addActionListener((ActionEvent e) -> {
            setMemoryValues();
            if (isSaving) {
                save();
            } else {
                load();
            }
        });

        buttonPanel.add(cancel);
        buttonPanel.add(accept);

        dialog.getContentPane()
                .add(buttonPanel, BorderLayout.PAGE_END);
        dialog.pack();
    }

    private void load() {
        try (FileInputStream in = new FileInputStream(file)) {
            DataInputStream dis = new DataInputStream(in);
            long len = file.length();
            int loc = startLoc;
            for (long i = 0; i < len; i++) {
                try {
                    bus.write(loc++, dis.readByte());
                } catch (MemoryAccessException e) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO,
                            "Tried to write read-only: " + Utils.wordToHex(loc));
                }
            }
            dialog.setVisible(false);
        } catch (IOException e) {
        }
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            DataOutputStream dos = new DataOutputStream(out);
            for (int i = startLoc; i <= endLoc; i++) {
                dos.writeByte(bus.read(i, true));
            }
            dialog.setVisible(false);
        } catch (MemoryAccessException | IOException e) {
        } finally {
        }
    }

    private void setMemoryValues() {
        int tempStart = startLoc;
        try {
            startLoc = Integer.parseInt(startField.getText(), 16);
        } catch (NumberFormatException e) {
            startLoc = tempStart;

        }
        int tempEnd = endLoc;
        try {
            endLoc = Integer.parseInt(endField.getText(), 16);
        } catch (NumberFormatException e) {
            endLoc = tempEnd;
        }
    }

    private void setFields() {
        startField.setText(Utils.wordToHex(startLoc));
        endField.setText(Utils.wordToHex(endLoc));
    }

    public void showSaveDialog(int start) {
        dialog.setTitle("Save");
        this.startLoc = start;
        this.endLoc = start + 255;
        setFields();
        isSaving = true;
        endField.setEnabled(true);
        dialog.setVisible(true);
    }

    public void showLoadDialog(int start) {
        dialog.setTitle("Load");
        this.startLoc = start;
        setFields();
        isSaving = false;
        endField.setEnabled(false);
        dialog.setVisible(true);
        parent.updateState();
    }

}
