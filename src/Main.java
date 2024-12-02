import schedulers.*;
import models.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
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

            processes.add(new Process(name, color, arrivalTime, burstTime, priority));
        }

        while (true) {
            System.out.println("\nChoose a scheduler:");
            System.out.println("1. Priority Scheduling");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Shortest Remaining Time First (SRTF)");
            System.out.println("4. FCAI Scheduling");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            Scheduler scheduler = null;

            switch (choice) {
                case 1:
                    scheduler = new PriorityScheduler(contextSwitchingTime);
                    break;
                case 2:
                    // scheduler = new SJFScheduler(contextSwitchingTime);
                    break;
                case 3:
                    // scheduler = new SRTFScheduler(contextSwitchingTime);
                    break;
                case 4:
                    // scheduler = new FCAIScheduler(contextSwitchingTime);
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

            if (scheduler != null) {
                scheduler.schedule(processes);
                scheduler.printExecutionOrder();
                scheduler.printMetrics();
            }
        }
    }
}
