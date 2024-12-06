import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Process;
import models.ExecutionRange;
import schedulers.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TimelinePanel extends JPanel {
    private List<ExecutionRange> executionOrder;
    private int timelineLength;

    public TimelinePanel(List<ExecutionRange> executionOrder, int timelineLength) {
        this.executionOrder = executionOrder;
        this.timelineLength = timelineLength;
        setPreferredSize(new Dimension(timelineLength * 50, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int leftMargin = 70;
        int topMargin = 20;
        int lineHeight = 50;
        int usableHeight = panelHeight - topMargin;

        g2d.setColor(Color.decode("#526D82"));
        for (int i = topMargin; i < usableHeight; i += lineHeight) {
            g2d.drawLine(0, i, panelWidth, i);
        }

        g2d.setColor(Color.BLACK);
        for (int t = 0; t <= timelineLength; t++) {
            int x = leftMargin + t * 50;
            g2d.drawLine(x, topMargin, x, panelHeight - 20);
            g2d.drawString(String.valueOf(t), x - 5, panelHeight - 5);
        }

        for (int i = 0; i < executionOrder.size(); i++) {
            ExecutionRange range = executionOrder.get(i);

            int startX = leftMargin + range.getLeft() * 50;
            int endX = leftMargin + range.getRight() * 50;
            int y = topMargin + range.getProcess().getID() * lineHeight + 10;


            g2d.setColor(Color.decode("#" + range.getProcess().getColor()));
            g2d.fillRect(startX, y, endX - startX, lineHeight - 20);

            g2d.setColor(Color.decode("#27374D"));
            g2d.drawString(range.getProcess().getName(), 10, y + lineHeight / 2);
        }
    }
}