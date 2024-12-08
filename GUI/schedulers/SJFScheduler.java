package schedulers;

import java.util.*;
import models.Process;
import models.Process;
import models.ExecutionRange;


public class SJFScheduler implements Scheduler {

  private List<ExecutionRange> executionOrder = new ArrayList<>();
  private List<Process> currentProcesses = new ArrayList<>();
  private List<Process> readyQueue = new ArrayList<>();
  private int time = 0, completed = 0, total = 0, contextSwitchingTime = 0, agingThreshold = 10;

  public SJFScheduler(int contextSwitchingTime) {
    this.contextSwitchingTime = contextSwitchingTime;
  }

  @Override
  public void schedule(List<Process> processes) {
    List<Process> processes2 = new ArrayList<>();

    for (Process p : processes) {
      processes2.add(new Process(p.getName(), p.getColor(), p.getArrivalTime(), p.getBurstTime(), p.getPriority(), p.getID()));
    }

    total = processes2.size();
    currentProcesses = processes2;
    Map<Process, Integer> waitTimes = new HashMap<>();

    while (completed < total) {
      for (Process p : processes2) {
        if (p.getArrivalTime() == time) {
          readyQueue.add(p);
          waitTimes.put(p, time); // Track when the process entered the queue
        }
      }

      Process current = readyQueue.stream()
              .min(Comparator.comparingInt(p -> {
                int agingAdjustment = (time - waitTimes.getOrDefault(p, time)) >= agingThreshold ? -1 : 0;
                return ((Process)p).getBurstTime() + agingAdjustment;
              }).thenComparingInt(p -> ((Process)p).getArrivalTime()))
              .orElse(null);


      if (current != null) {
        if (!executionOrder.isEmpty() && executionOrder.get(executionOrder.size() - 1).getProcess() != current) {
          time += contextSwitchingTime;
        }

        int left = time, right = time + current.getBurstTime();
        time += current.getBurstTime();
        current.setRemainingBurstTime(0);
        current.setTurnaroundTime(time - current.getArrivalTime());
        current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

        if (!executionOrder.isEmpty() && executionOrder.get(executionOrder.size() - 1).getProcess() == current) {
          executionOrder.get(executionOrder.size() - 1).mergeRanges(right);
        } else {
          executionOrder.add(new ExecutionRange(current, left, right));
        }
        completed++;
        readyQueue.remove(current);
      } else {
        time++;
      }
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

  public List<ExecutionRange> getExecutionOrder() {
    return executionOrder;
  }

  public List<Process> getProcesses() {
    return currentProcesses;
  }
}
