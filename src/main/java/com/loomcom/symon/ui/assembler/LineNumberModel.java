package com.loomcom.symon.ui.assembler;


import java.awt.Rectangle;

/**
 * <p>
 * A generic model interface which defines an underlying component with line
 * numbers.
 * From:
 * https://www.algosome.com/articles/line-numbers-java-jtextarea-jtable.html
 *
 * @author Greg Cope
 *
 *
 *
 */
public interface LineNumberModel {

    /**
     * <p>
     * Returns total number of lines.
     *
     * @return
     *
     */
    public int getNumberLines();

    /**
     * <p>
     * Returns a Rectangle defining the location in the view of the parameter
     * line. Only the y and height fields are required by callers.
     *
     * @param line
     *
     * @return A Rectangle defining the view coordinates of the line.
     *
     */
    public Rectangle getLineRect(int line);

}
