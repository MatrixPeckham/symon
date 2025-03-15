/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.ui.assembler;

import static java.awt.Color.LIGHT_GRAY;
import static javax.swing.Action.SHORT_DESCRIPTION;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

/**
 *
 * @author matri
 */
public class AssemblerWindow extends JFrame {

    private JTextPane editor;

    private ASMHilighterDocument document;

    private LineNumberComponent lineNumbers;

    private JFileChooser chooser;

    private FileNameExtensionFilter filter;

    public AssemblerWindow() {
        setTitle("Assembler");

        filter = new FileNameExtensionFilter(
                "Assembler Files", "asm", "txt");

        chooser = new JFileChooser(System.getProperty("user.dir"));

        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);

        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem load = new JMenuItem(new OpenAction());
        JMenuItem save = new JMenuItem(new SaveAction());
        fileMenu.add(load);
        fileMenu.add(save);
        menubar.add(fileMenu);
        setJMenuBar(menubar);
        document = new ASMHilighterDocument();

        editor = new JTextPane() {

            @Override
            public String getToolTipText(MouseEvent event) {
                String tip = document.getTooltipAt(viewToModel2D(event.
                        getPoint()));
                return tip;//super.getToolTipText(event);
            }

        };
        editor.setToolTipText("TEST");

        lineNumbers = new LineNumberComponent(new LineNumberModelImpl());

        document.addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumbers.adjustWidth();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumbers.adjustWidth();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumbers.adjustWidth();
            }

        });

        editor.setDocument(document);

        editor.setBackground(LIGHT_GRAY);

        JScrollPane scroller = new JScrollPane(editor);
        scroller.setRowHeaderView(lineNumbers);
        getContentPane().add(scroller);
        pack();
        setSize(500, 500);
    }

    private class LineNumberModelImpl implements LineNumberModel {

        @Override
        public int getNumberLines() {
            try {
                String s = document.getText(0, document.getLength());
                int count = 1;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\n') {
                        count++;
                    }
                }
                return count;
            } catch (BadLocationException ex) {
                Logger.getLogger(AssemblerWindow.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            return 1;
        }

        @Override
        public Rectangle getLineRect(int line) {

            try {
                int offset = 0;
                String s = document.getText(0, document.getLength());
                while (line > 0 && offset < document.getLength()) {
                    if (s.charAt(offset) == '\n') {
                        line--;
                    }
                    offset++;
                }
                return (Rectangle) editor.modelToView2D(offset).getBounds();

            } catch (BadLocationException e) {

                e.printStackTrace();

                return new Rectangle();

            }

        }

    }

    class SaveAction extends AbstractAction {

        public SaveAction() {
            super("Save", null);
            putValue(SHORT_DESCRIPTION, "Save File");
        }

        public void actionPerformed(ActionEvent actionEvent) {
            int option = chooser.showSaveDialog(AssemblerWindow.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String filename = file.getAbsolutePath();
                if (!filename.contains(".")) {
                    save(addExtension(filename));
                    return;
                }
                String extension = filename.substring(filename.lastIndexOf("."));
                for (String ext : filter.getExtensions()) {
                    if (ext.equalsIgnoreCase(extension)) {
                        save(file);
                        return;
                    }
                }
                save(addExtension(filename.substring(0, filename.lastIndexOf(
                        "."))));
            }
        }

        void save(File f) {
            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
                PrintWriter write = new PrintWriter(f);
                write.print(editor.getText());
                write.close();
            } catch (IOException e) {
            }
        }

        File addExtension(String name) {
            name += "." + filter.getExtensions()[0];
            return new File(name);
        }

    }

    class OpenAction extends AbstractAction {

        public OpenAction() {
            super("Open", null);
            putValue(SHORT_DESCRIPTION, "Open File");
        }

        public void actionPerformed(ActionEvent actionEvent) {
            int option = chooser.showOpenDialog(AssemblerWindow.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                open(file);
            }
        }

        void open(File f) {
            try {
                String text = Files.readString(f.toPath());
                editor.setText(text);
            } catch (IOException e) {
            }
        }

        File addExtension(String name) {
            name += filter.getExtensions()[0];
            return new File(name);
        }

    }

}
