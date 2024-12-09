package schedulers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.Process;

class ExecutionRange {
  private Process process;
  private int left;
  private int right;

  public ExecutionRange(Process process, int left, int right) {
      this.process = process;
      this.left = left;
      this.right = right;
  }

  public Process getProcess() {
      return process;
  }

  public int getLeft() {
      return left;
  }

  public int getRight() {
      return right;
  }

  public void mergeRanges(int newRight) {
      this.right = newRight;
  }
}

public class SRTFScheduler implements Scheduler {

  private List<ExecutionRange> executionOrder = new ArrayList<>();
  private List<Process> currentProcesses = new ArrayList<>();
  private List<Process> readyQueue = new ArrayList<>();
    private Set<Process> completedProcesses = new HashSet<>();
  private int time = 0, completed = 0, total = 0, contextSwitchingTime = 0;

  public SRTFScheduler(int contextSwitchingTime) {
    this.contextSwitchingTime = contextSwitchingTime;
  }

  @Override
  public void schedule(List<Process> processes) {
    List<Process> processes2 = new ArrayList<>();

    for (Process p : processes) {
      processes2.add(new Process(p.getName(), p.getColor(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));
    }

    total = processes2.size();
    currentProcesses = processes2;
    Map<Process, Integer> lastQueueTimes = new HashMap<>();

    for (Process p : processes2) {
      p.setPriority(p.getRemainingBurstTime());
    }

    while (completed < total) {
        for (Process p : processes2) {
          if (p.getArrivalTime() <= time && !readyQueue.contains(p) && !completedProcesses.contains(p)) {
            readyQueue.add(p);
            lastQueueTimes.put(p, time);
          }
        }

        // Solve the starvation using aging
        for (Process p : readyQueue) {
            if (lastQueueTimes.get(p) >= 0 && time - lastQueueTimes.get(p) >= 10) {
              p.setPriority(Math.max(0, p.getPriority() - 1));
              lastQueueTimes.put(p, time);
            }
        }

        Process current = readyQueue.stream()
        .min(Comparator.comparingInt(Process::getPriority)
                .thenComparingInt(Process::getRemainingBurstTime))
        .orElse(null);

        if (current != null) {
          if (!executionOrder.isEmpty() && executionOrder.get(executionOrder.size() - 1).getProcess() != current) {
            time += contextSwitchingTime;
          }

          int left = time, right = time + 1;
          current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
          current.setPriority(Math.max(0, current.getPriority() - 1));
          lastQueueTimes.put(current, time + 1);

          if (!executionOrder.isEmpty() && executionOrder.get(executionOrder.size() - 1).getProcess() == current) {
            executionOrder.get(executionOrder.size() - 1).mergeRanges(right);
          } else {
            executionOrder.add(new ExecutionRange(current, left, right));
          }

          if (current.getRemainingBurstTime() == 0) {
              completed++;
              current.setTurnaroundTime(time + 1 - current.getArrivalTime());
              current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
              readyQueue.remove(current);
              completedProcesses.add(current);
          }
        }
        time++;
    }
  }

  @Override
  public void printMetrics() {
    float totalWaitingTime = 0, totalTurnaroundTime = 0;
    for (Process p : currentProcesses) {
      totalWaitingTime += p.getWaitingTime();
      totalTurnaroundTime += p.getTurnaroundTime();
      System.out.println("Process " + p.getName() + ": Waiting Time = " + p.getWaitingTime() + "  Turnaround  Time = " + p.getTurnaroundTime());
    }
    System.out.println("Average Waiting Time = " + totalWaitingTime / total);
    System.out.println("Average Turnaround  Time = " + totalTurnaroundTime / total);
  }

  @Override
  public void printExecutionOrder() {
    for (ExecutionRange e : executionOrder) {
      System.out.println("Process " + e.getProcess().getName() + " from " + e.getLeft() + " to " + e.getRight());
    }
  }
}