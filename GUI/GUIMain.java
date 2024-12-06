import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Process;
import models.ExecutionRange;
import schedulers.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class GUIMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.println("Enter the number of processes:");
        int n = scanner.nextInt();

        System.out.println("Enter context switching time:");
        int contextSwitchingTime = scanner.nextInt();

        System.out.println("Enter details for each process:");
        for (int i = 0; i < n; i++) {
            System.out.println("Process " + (i + 1) + ":");
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Color: ");
            String color = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();
            System.out.print("Quantum: ");
            int quantum = scanner.nextInt();

            Process process = new Process(name, color, arrivalTime, burstTime, priority, i);
            process.setQuantum(quantum);
            processes.add(process);
        }
        SwingUtilities.invokeLater(() -> new SchedulerLauncherGUI(processes, contextSwitchingTime));
    }
}
