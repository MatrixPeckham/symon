/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.ui.assembler;

import static com.loomcom.symon.assembler.Token.Type.*;

import com.loomcom.symon.assembler.Token;
import com.loomcom.symon.assembler.Tokenizer;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

/**
 * Copied from
 * https://www.linuxquestions.org/questions/programming-9/java-jtextarea-and-syntax-highlighting-417575/
 * and extended for my purposes.
 *
 * @author matri
 */
public class ASMHilighterDocument extends DefaultStyledDocument {

    private final DefaultStyledDocument doc;

    private final Element rootElement;

    private final MutableAttributeSet normal;

    private final MutableAttributeSet keyword;

    private final MutableAttributeSet comment;

    private final MutableAttributeSet directive;

    private final MutableAttributeSet number;

    private final MutableAttributeSet quote;

    private final MutableAttributeSet error;

    private final ExecutorService executor
            = Executors.newSingleThreadExecutor();

    private Tokenizer tokens;

    private Future<List<Token>> tokenList = null;

    private List<Token> curTokens = null;

    private Timer timer;

    public ASMHilighterDocument() {

        doc = this;
        rootElement = doc.getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

        normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, Color.black);

        comment = new SimpleAttributeSet();
        Color green = new Color(0, 120, 0);
        StyleConstants.setForeground(comment, green);
        StyleConstants.setItalic(comment, true);

        keyword = new SimpleAttributeSet();
        Color blue = new Color(0, 0, 140);
        StyleConstants.setForeground(keyword, blue);
        StyleConstants.setBold(keyword, true);

        quote = new SimpleAttributeSet();
        Color red = new Color(140, 0, 0);
        StyleConstants.setForeground(quote, red);

        directive = new SimpleAttributeSet();
        StyleConstants.setForeground(directive, red);
        StyleConstants.setBold(directive, true);

        number = new SimpleAttributeSet();
        StyleConstants.setBold(number, true);

        error = new SimpleAttributeSet();
        StyleConstants.setUnderline(error, true);
        StyleConstants.setBackground(error, Color.RED);
        StyleConstants.setForeground(error, Color.black);

        tokens = new Tokenizer("");
        tokens.shouldCullComments(false);
        try {
            tokens.setString(getText(0, getLength()));
        } catch (BadLocationException ex) {
            Logger.getLogger(ASMHilighterDocument.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        timer = new Timer(50, ((e) -> {
            processTokens();
        }));

    }

    /*
     * Override to apply syntax highlighting after the document has been updated
     */
    @Override
    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
        super.insertString(offset, str, a);
        processChangedLines(offset, str.length());
    }

    /*
     * Override to apply syntax highlighting after the document has been updated
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, 0);
    }

    /*
     * Determine how many lines have been changed,
     * then apply highlighting to each line
     */
    private void processChangedLines(int offset, int length)
            throws BadLocationException {
        String content = doc.getText(0, doc.getLength());
        tokens.setString(content);
        tokenList = executor.submit(() -> {
            return tokens.scanTokens();
        });
        timer.start();
    }

    private void processTokens() {
        if (tokenList != null && tokenList.isDone()) {

            try {
                for (Token tok : curTokens = tokenList.get()) {
                    MutableAttributeSet attr;
                    switch (tok.type) {
                        case COMMENT:
                            attr = comment;
                            break;
                        case NUMBER:
                            attr = number;
                            break;
                        case STRING:
                            attr = quote;
                            break;
                        case OPCODE:
                            attr = keyword;
                            break;
                        case ALIGN:
                        case BYTE:
                        case DWORD:
                        case FILLWORD:
                        case FILL:
                        case CONST:
                        case IMPORT:
                        case WORD:
                        case ORG:
                        case TEXT:
                            attr = directive;
                            break;
                        default:
                            attr = normal;
                            break;
                    }
                    doc.setCharacterAttributes(tok.absSourceOffset, tok.length,
                            attr,
                            true);
                    if (tok.error) {
                        doc.setCharacterAttributes(tok.absSourceOffset,
                                tok.length, error, false);
                    }

                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ASMHilighterDocument.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(ASMHilighterDocument.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getTooltipAt(int offset) {

        if (curTokens == null) {
            return null;
        }

        int i = 0;
        while (i < curTokens.size()) {
            Token t = curTokens.get(i);
            if (t.absSourceOffset <= offset && offset < (t.absSourceOffset
                    + t.length)) {
                return t.tooltip;
            }
            if (t.absSourceOffset > offset) {
                return null;
            }
            i++;
        }
        return null;
    }


    /*
     * This updates the colored text and prepares for undo event
     */
    @Override
    protected void fireInsertUpdate(DocumentEvent evt
    ) {

        super.fireInsertUpdate(evt);

        try {
            processChangedLines(evt.getOffset(), evt.getLength());
        } catch (BadLocationException ex) {
            System.out.println("" + ex);
        }
    }

    /*
     * This updates the colored text and does the undo operation
     */
    @Override
    protected void fireRemoveUpdate(DocumentEvent evt
    ) {

        super.fireRemoveUpdate(evt);

        try {
            processChangedLines(evt.getOffset(), evt.getLength());
        } catch (BadLocationException ex) {
            System.out.println("" + ex);
        }
    }

}
