package schedulers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Process;
import models.ExecutionRange;

public class PriorityScheduler implements Scheduler {
  private List<Process> listOfProcesses = new ArrayList<>();
  private List<Process> readyQueue = new ArrayList<>();
  private List<Process> executionOrder = new ArrayList<>();
  private List<ExecutionRange> executionRanges = new ArrayList<>();
  private int currentTime = 0;
  private int contextSwitchingTime = 0;

  public PriorityScheduler(int contextSwitchingTime) {
    this.contextSwitchingTime = contextSwitchingTime;
  }

  @Override
  public void schedule(List<Process> processes) {
    this.listOfProcesses = new ArrayList<>(processes);
    this.readyQueue = new ArrayList<>(processes);

    while (!this.readyQueue.isEmpty()) {
      List<Process> availableProcesses = readyQueue.stream()
          .filter(p -> p.getArrivalTime() <= currentTime)
          .toList();

      if (availableProcesses.isEmpty()) {
        // If no process has arrived, increment time
        currentTime++;
        continue;
      }

      // Select the process with the highest priority (lowest number)
      Process nextProcess = availableProcesses.stream()
          .min(Comparator.comparingInt(p -> p.getPriority()))
          .orElseThrow();

      // Simulate execution
      int startTime = currentTime;
      currentTime += nextProcess.getBurstTime();
      int endTime = currentTime;
      nextProcess.setTurnaroundTime(currentTime - nextProcess.getArrivalTime());
      nextProcess.setWaitingTime(nextProcess.getTurnaroundTime() - nextProcess.getBurstTime());

      // Add to execution order and remove from ready queue
      executionOrder.add(nextProcess);
      readyQueue.remove(nextProcess);
      executionRanges.add(new ExecutionRange(nextProcess, startTime, endTime));
      currentTime += contextSwitchingTime;
    }
  }

  @Override
  public void printMetrics() {
    int totalWaitingTime = 0, totalTurnaroundTime = 0;
    for (Process process : executionOrder) {
      totalWaitingTime += process.getWaitingTime();
      totalTurnaroundTime += process.getTurnaroundTime();
    }

    System.out.println("Average Waiting Time: " + (double) totalWaitingTime / executionOrder.size());
    System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / executionOrder.size());

  }

  @Override
  public void printExecutionOrder() {
    System.out.println("Execution Order:");
    for (Process process : executionOrder) {
      System.out.println("Process: " + process.getName() + " | Burst Time: " + process.getBurstTime()
          + " | Waiting Time: " + process.getWaitingTime() + " | Turnaround Time: " + process.getTurnaroundTime());
    }
  }

  public List<ExecutionRange> getExecutionOrder() {
    return executionRanges;
  }

  public List<Process> getProcesses() {
    return listOfProcesses;
  }
}
