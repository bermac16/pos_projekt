package GUI;

import BL.Value;
import Exceptions.NoDataFoundException;
import Query.Query;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Matthias
 */
public class LineGraph extends javax.swing.JPanel {

    private double sidePadding = 0.2;
    private double topBotPadding = 0.15;

    private int barWidth = 50;

    private List<Value> values;
    private Color color = new Color(0, 0, 200);

    private double maxValue = 0;
    private double minValue = Double.MAX_VALUE;
    
    private long minDate = Long.MAX_VALUE;
    private long maxDate = 0;
    
    private String companySymbol;
    
    /**
     * Draws a line graph over the passed values, using the highest value of
     * each day
     * @param values all values that should be included
     * @param companySymbol the symbol of the company that is shown
     * @throws NoDataFoundException when there's nothing to draw
     */
    public LineGraph(List<Value> values, String companySymbol) throws NoDataFoundException {
        initComponents();

        this.values = values;
        this.companySymbol = companySymbol;
        
        if(values.isEmpty()) {
            throw new NoDataFoundException();
        }
        
        Collections.sort(values);

        
        for (Value value : values) {
            maxValue = Double.max(maxValue, value.getHigh());
            minValue = Double.min(minValue, value.getHigh());
            maxDate = Long.max(maxDate, value.getDate().toEpochDay());
            minDate = Long.min(minDate, value.getDate().toEpochDay());
        }
        
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int w = (int) (getWidth() * (1 - sidePadding * 2));
        int h = (int) (getHeight() * (1 - topBotPadding * 2));
        int sidePxl = (int) (getWidth() * sidePadding);
        int topPxl = (int) (getHeight() * topBotPadding);
        double yAxisStartValue = ((int) Math.max(0, maxValue - 1.5 * (maxValue - minValue))) / 10 * 10;
        double valuePerPxl = (maxValue - yAxisStartValue) / h;
        double stepvaluesize = ((int) (valuePerPxl * h / 5));
        if(stepvaluesize > 5) {
            stepvaluesize = ((int) stepvaluesize) / 5 * 5;
        }
        if(stepvaluesize < 1) stepvaluesize = 1;
        double steppixelsize = 1.0 / (valuePerPxl / stepvaluesize);

        // title
        g2d.setColor(Color.black);
        int fontSize = (int) Math.min(topPxl * 0.6, 25);
        g2d.setFont(new Font("Calibri", Font.PLAIN, fontSize));
        String companyName = "unknown";
        try {
            companyName = Query.companyName(companySymbol);
        } catch (SQLException e) {
        }
        g2d.drawString(companySymbol + " - " + companyName, sidePxl, (int) (topPxl * 0.65));

        // y axis
        g2d.setStroke(new BasicStroke(3));
        g2d.setFont(new Font("Calibri", Font.PLAIN, 13));
        g2d.setColor(Color.black);
        g2d.drawLine(sidePxl, topPxl + h, sidePxl, topPxl);
        double yAxisValue = yAxisStartValue;
        for (double i = topPxl + h; i >= topPxl; i -= steppixelsize) {
            g2d.drawLine(sidePxl - 5, (int) i, sidePxl + 5, (int) i);
            g2d.drawString(String.format("%.2f", yAxisValue), sidePxl - 40, (int) i);
            yAxisValue += stepvaluesize;
        }

        // x axis
        g2d.drawLine(sidePxl, topPxl + h, sidePxl + w, topPxl + h);
        g2d.drawString(LocalDate.ofEpochDay(minDate).format(DateTimeFormatter.ofPattern("dd.MM.YYYY")), sidePxl, topPxl + h + 15);
        g2d.drawString(LocalDate.ofEpochDay(maxDate).format(DateTimeFormatter.ofPattern("dd.MM.YYYY")), sidePxl + w - 5, topPxl + h + 15);
        
        // line graph
        g2d.setColor(color);
        for (int i = 1; i < values.size(); i++) {
            g2d.drawLine(calcXCoord(values.get(i-1).getDate().toEpochDay(), w, sidePxl), 
                    calcYCoord(values.get(i-1).getHigh(), h, topPxl, yAxisStartValue),
                    calcXCoord(values.get(i).getDate().toEpochDay(), w, sidePxl), 
                    calcYCoord(values.get(i).getHigh(), h, topPxl, yAxisStartValue));
        }
    }
    
    /**
     * calculate the x coordinate of a certain date, depending on graph size and date range
     * @param date
     * @param w width of the coord system
     * @param sidePxl padding on side of graph
     * @return the x coord of the date
     */
    private int calcXCoord(long date, int w, int sidePxl) {
        return sidePxl + (int) (w * (date - minDate) / (maxDate - minDate));
    }

    /**
     * calculate the y coordinate of a certain value, depending on graph size and value range
     * @param value
     * @param h height of coord system
     * @param topPxl padding on top and bottom of graph
     * @param yAxisStartValue the offset at which the y axis values start
     * @return  the y coord of the value
     */
    private int calcYCoord(double value, int h, int topPxl, double yAxisStartValue) {
        return topPxl + h - (int) (h * (value - yAxisStartValue) / (maxValue - yAxisStartValue));
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
