package schedulers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Process;


class ExecutionOrderData {
  private Process process;
  private int start;
  private int end;
  private int prevQuantum;
  private int prevFCAIFactor;

  public ExecutionOrderData(Process process, int start, int end, int prevQuantum, int prevFCAIFactor) {
    this.process = process;
    this.start = start;
    this.end = end;
    this.prevQuantum = prevQuantum;
    this.prevFCAIFactor = prevFCAIFactor;
  }

  public ExecutionOrderData(Process process, int start, int end) {
    this.process = process;
    this.start = start;
    this.end = end;
  }

  public Process getProcess() {
    return process;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public int getPrevQuantum() {
    return prevQuantum;
  }

  public int getPrevFCAIFactor() {
    return prevFCAIFactor;
  }

  public void mergeRanges(int newend) {
    this.end = newend;
  }
}


public class FCAIScheduler implements Scheduler {
  private List<Process> readyQueue = new ArrayList<>();
  private List<ExecutionOrderData> executionOrder = new ArrayList<>();
  private List<Process> completedProcesses = new ArrayList<>();
  private int currentTime = 0;
  private int contextSwitchingTime = 0;
  private int lastArrivalTime = 0;
  private int maxBrustTime = 0;

  public FCAIScheduler(int contextSwitchingTime) {
    this.contextSwitchingTime = contextSwitchingTime;
  }

  private int calculateFCAIFactor(Process process) {
    return (10 - process.getPriority()) 
          + (int) Math.ceil(process.getArrivalTime() / ((double) lastArrivalTime / 10))
          + (int) Math.ceil(process.getRemainingBurstTime() / ((double) maxBrustTime / 10));
  }

  @Override
  public void schedule(List<Process> processes) {
    // Deep copy the processes
    for (Process process : processes) {
      this.readyQueue.add(new Process(process));
    }

    // Sort by arrival time to simulate queue arrival
    readyQueue.sort(Comparator.comparingInt(p -> p.getArrivalTime()));

    lastArrivalTime = readyQueue.stream()
        .mapToInt(Process::getArrivalTime)
        .max()
        .orElse(0);

    maxBrustTime = readyQueue.stream()
        .mapToInt(Process::getBurstTime)
        .max()
        .orElse(0);

    for (Process process : readyQueue) {
      process.setFCAIFactor(calculateFCAIFactor(process));
    }

    // Initialize previous Process to track preemative scheduling
    Process prevProcess = null, nextProcess = null;
    int prevExecutionTime = 0;
    Boolean processCompletion = false;

    while (!this.readyQueue.isEmpty()) {
      List<Process> availableProcesses = readyQueue.stream()
          .filter(p -> p.getArrivalTime() <= currentTime)
          .toList();

      if (availableProcesses.isEmpty()) {
        // If no process has arrived, increment time
        currentTime++;
        continue;
      }

      // first filter
      if (currentTime == 0) {
        // Select the process that come first and if they came in the same time
        // then choose the lowest FCAI factor
        nextProcess = availableProcesses.stream()
            .min(Comparator.comparingInt(Process::getArrivalTime) // Primary comparison
                .thenComparingInt(Process::getFCAIFactor)) // Secondary comparison
            .orElseThrow();

        // Make the next process first if it is not
        if (readyQueue.indexOf(nextProcess) != 0) {
          readyQueue.remove(nextProcess);
          readyQueue.add(0, nextProcess);
        }
      } else if (!processCompletion) {
        nextProcess = availableProcesses.stream()
            .min(Comparator.comparingInt(Process::getFCAIFactor) // Primary comparison
                .thenComparingInt(Process::getArrivalTime)) // Secondary comparison 
            .orElseThrow();

        // If the previous process has the same FCAI factor in next process
        // then let the previous process continue
        if (prevProcess != null && nextProcess.getFCAIFactor() == prevProcess.getFCAIFactor())
          nextProcess = prevProcess;

        // Make the next process first if it is not
        if (readyQueue.indexOf(nextProcess) != 0) {
          readyQueue.remove(nextProcess);
          readyQueue.add(0, nextProcess);
        }
      } else {
        // Assign the next process to the next process in the queue
        nextProcess = readyQueue.get(0);
        processCompletion = false;
      }

      int prevQuantum = -1;
      Boolean isPreemetedByQuantum = false;

      if (prevProcess != null)
        prevQuantum = prevProcess.getQuantum();

      if (prevProcess != null && (!nextProcess.equals(prevProcess) || prevExecutionTime == prevQuantum)) {

        // Remove previous process from ready queue
        readyQueue.remove(prevProcess);

        // Update previous process quantum
        if (!nextProcess.equals(prevProcess)) {
          prevProcess.setQuantum(prevQuantum + (prevQuantum - prevExecutionTime));
        } else {
          isPreemetedByQuantum = true;
          prevProcess.setQuantum(prevQuantum + 2);
        }

        if (isPreemetedByQuantum && availableProcesses.size() > 1) {
          // Assign the next process to the next process in the queue
          nextProcess = readyQueue.get(0);
        }

        int prevFCAIFactor = prevProcess.getFCAIFactor();
        // Re-calculate previous process FCAI factor
        prevProcess.setFCAIFactor(calculateFCAIFactor(prevProcess));

        // Create and add execution order data to execution order
        ExecutionOrderData e = new ExecutionOrderData(
          new Process(prevProcess), // deep copy
          currentTime - prevExecutionTime,
          currentTime,
          prevQuantum,
          prevFCAIFactor
        );
        executionOrder.add(e);

        // Re-add previous process to ready queue in the right position
        int index = 0;
        for (int i = readyQueue.size() - 1; i >= 0; i--) {
          if (readyQueue.get(i).getArrivalTime() > currentTime)
            index = i;
          else
            break;
        }
        readyQueue.add(index, prevProcess);

        // Re-set previous Execution time
        prevExecutionTime = 0;

        // Add context Switching Time to current time
        currentTime += contextSwitchingTime;
      }

      // Execute 40% of quantum time non-preemptive and use ceil as (All calculations are performed using the ceil function)
      int nonPreemativeTime = (int) Math.ceil(0.4 * nextProcess.getQuantum());
      int nextExecutionTime = prevExecutionTime + nonPreemativeTime;
      int currentQuantum = nextProcess.getQuantum();
      
      // Check if the process still in CPU and exceeded the non-preemative time (40% of its quantum)
      if (prevProcess != null && currentQuantum == prevQuantum && prevProcess.equals(nextProcess))
        nonPreemativeTime = 1;
      // Check for exceeding current quantum
      else if (nextExecutionTime > currentQuantum)
        nonPreemativeTime = nextExecutionTime - currentQuantum;
      else if (nextExecutionTime > nextProcess.getRemainingBurstTime())
        nonPreemativeTime = nextProcess.getRemainingBurstTime();

      nextProcess.setRemainingBurstTime(nextProcess.getRemainingBurstTime() - nonPreemativeTime);
      currentTime += nonPreemativeTime;

      prevExecutionTime += nonPreemativeTime;

      if (nextProcess.getRemainingBurstTime() == 0) {
        nextProcess.setTurnaroundTime(currentTime - nextProcess.getArrivalTime());
        nextProcess.setWaitingTime(nextProcess.getTurnaroundTime() - nextProcess.getBurstTime());
        readyQueue.remove(nextProcess);
        completedProcesses.add(nextProcess);
        ExecutionOrderData e = new ExecutionOrderData(
          new Process(nextProcess), // deep copy
          currentTime - prevExecutionTime,
          currentTime
        );
        executionOrder.add(e);
        nextProcess = null;
        processCompletion = true;
        prevExecutionTime = 0;
        currentTime += contextSwitchingTime;
      }
      prevProcess = nextProcess;
    }
  }

  @Override
  public void printMetrics() {
    int totalWaitingTime = 0, totalTurnaroundTime = 0;
    System.out.println("Metrics:");
    for (Process process : completedProcesses) {
      totalWaitingTime += process.getWaitingTime();
      totalTurnaroundTime += process.getTurnaroundTime();

      System.out.println("Process: " + process.getName() + " | Burst Time: " + process.getBurstTime()
          + " | Waiting Time: " + process.getWaitingTime() + " | Turnaround Time: " + process.getTurnaroundTime());
    }

    System.out.println("Average Waiting Time: " + (double) totalWaitingTime / completedProcesses.size());
    System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / completedProcesses.size());
  }

  @Override
  public void printExecutionOrder() {
    System.out.println("Execution Order:");
    for (ExecutionOrderData e : executionOrder) {
      if (e.getProcess().getRemainingBurstTime() > 0) {
        System.out.println(
          "Process " + e.getProcess().getName()
          + " Executed from " + e.getStart()
          + " to " + e.getEnd()
          + ", Remaining brust time " + e.getProcess().getRemainingBurstTime()
          + ", Quantum updated from " +  e.getPrevQuantum()
          + " to " + e.getProcess().getQuantum()
          + " and FCAI Factor updated from " + e.getPrevFCAIFactor()
          + " to " + e.getProcess().getFCAIFactor()
        );
      } else {
        System.out.println(
          "Process " + e.getProcess().getName()
          + " Executed from " + e.getStart()
          + " to " + e.getEnd()
          + " and completed"
        );
      }
    }
  }
}
