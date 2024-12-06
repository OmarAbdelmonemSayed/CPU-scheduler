package schedulers;

import java.util.List;

import models.Process;

public interface Scheduler {
  void schedule(List<Process> processes);

  void printMetrics();

  void printExecutionOrder();
}
