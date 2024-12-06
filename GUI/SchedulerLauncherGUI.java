import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Process;
import models.ExecutionRange;
import schedulers.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchedulerLauncherGUI extends JFrame {
    List<Process> processes = new ArrayList<>();
    int contextSwitchingTime = 0;

    public SchedulerLauncherGUI(List<Process> processes, int contextSwitchingTime) {
        this.processes = processes;
        this.contextSwitchingTime = contextSwitchingTime;
        setTitle("Scheduler Launcher");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(4, 1, 5, 5));

        JButton PrioritySchedulerButton = new JButton("Priority Scheduler");
        PrioritySchedulerButton.setBackground(Color.decode("#E5E1DA"));
        PrioritySchedulerButton.setForeground(Color.black);

        JButton SJFSchedulerButton = new JButton("SJF Scheduler");
        SJFSchedulerButton.setBackground(Color.decode("#E5E1DA"));
        SJFSchedulerButton.setForeground(Color.black);

        JButton SRTFSchedulerButton = new JButton("SRTF Scheduler");
        SRTFSchedulerButton.setBackground(Color.decode("#E5E1DA"));
        SRTFSchedulerButton.setForeground(Color.black);

        JButton FCAISchedulerButton = new JButton("FCAI Scheduler");
        FCAISchedulerButton.setBackground(Color.decode("#E5E1DA"));
        FCAISchedulerButton.setForeground(Color.black);

        PrioritySchedulerButton.addActionListener(e -> SchedulerWindow("Priority Scheduler"));
        SJFSchedulerButton.addActionListener(e -> SchedulerWindow("SJF Scheduler"));
        SRTFSchedulerButton.addActionListener(e -> SchedulerWindow("SRTF Scheduler"));
        FCAISchedulerButton.addActionListener(e -> SchedulerWindow("FCAI Scheduler"));

        add(PrioritySchedulerButton);
        add(SJFSchedulerButton);
        add(SRTFSchedulerButton);
        add(FCAISchedulerButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void SchedulerWindow(String title) {
        JFrame schedulerWindow = new JFrame(title);
        schedulerWindow.setSize(1200, 600);
        schedulerWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<ExecutionRange> executionRanges = new ArrayList<>();
        List<Process> currentProcesses = new ArrayList<>();

        if (title == "Priority Scheduler") {
//            PriorityScheduler priorityScheduler = new PriorityScheduler(contextSwitchingTime);
//            priorityScheduler.schedule(processes);
//            executionRanges = priorityScheduler.getExecutionOrder();
//            currentProcesses = priorityScheduler.getProcesses();
        }
        else if (title == "SJF Scheduler") {
//            SJFScheduler sjfScheduler = new SJFScheduler(contextSwitchingTime);
//            sjfScheduler.schedule(processes);
//            executionRanges = sjfScheduler.getExecutionOrder();
//            currentProcesses = sjfScheduler.getProcesses();
        }
        else if (title == "SRTF Scheduler") {
            SRTFScheduler srtfScheduler = new SRTFScheduler(contextSwitchingTime);
            srtfScheduler.schedule(processes);
            executionRanges = srtfScheduler.getExecutionOrder();
            currentProcesses = srtfScheduler.getProcesses();
        }
        else if (title == "FCAI Scheduler") {
//            FCAIScheduler fcaiScheduler = new FCAIScheduler(contextSwitchingTime);
//            fcaiScheduler.schedule(processes);
//            executionRanges = fcaiScheduler.getExecutionOrder();
//            currentProcesses = fcaiScheduler.getProcesses();
        }


        JPanel mainPanel = new JPanel(new BorderLayout());

        TimelinePanel timelinePanel = new TimelinePanel(executionRanges, executionRanges.getLast().getRight() + 10);
        JScrollPane scrollPane = new JScrollPane(timelinePanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel infoPanel = createProcessInfoPanel(currentProcesses);
        mainPanel.add(infoPanel, BorderLayout.EAST);

        schedulerWindow.add(mainPanel);
        schedulerWindow.setLocationRelativeTo(null);
        schedulerWindow.setVisible(true);
    }

    private JPanel createProcessInfoPanel(List<Process> processInfos) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(Color.decode("#89A8B2"));

        JLabel title = new JLabel("Processes Information", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.decode("#F1F0E8"));
        panel.add(title, BorderLayout.NORTH);


        String[] columnNames = {"Process", "Name", "Color", "Priority", "Waiting Time", "Turnaround Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (int i = 0; i < processInfos.size(); i++) {
            Process info = processInfos.get(i);
            model.addRow(new Object[]{i, info.getName(), "#" + info.getColor(), info.getPriority(), info.getWaitingTime(), info.getTurnaroundTime()});
        }

        JTable table = new JTable(model);
        table.setBackground(Color.GRAY);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

        JScrollPane tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }
}
