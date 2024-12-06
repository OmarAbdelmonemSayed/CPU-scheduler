package models;

public class Process {
  private String name;
  private String color;
  private int arrivalTime;
  private int burstTime;
  private int priority;
  private int remainingBurstTime;
  private int waitingTime = 0;
  private int turnaroundTime = 0;
  private int quantum;
  private int FCAIFactor = 0;

  public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
    this.name = name;
    this.color = color;
    this.arrivalTime = arrivalTime;
    this.burstTime = burstTime;
    this.remainingBurstTime = burstTime;
    this.priority = priority;
  }

  // Create process from existing process to make original process in processes list unaffected from any modifications in copy 
  public Process(Process process) {
    this.name = process.name;
    this.color = process.color;
    this.arrivalTime = process.arrivalTime;
    this.burstTime = process.burstTime;
    this.remainingBurstTime = process.remainingBurstTime;
    this.priority = process.priority;
    this.quantum = process.quantum;
    this.FCAIFactor = process.FCAIFactor;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(int arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public int getBurstTime() {
    return burstTime;
  }

  public void setBurstTime(int burstTime) {
    this.burstTime = burstTime;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public int getRemainingBurstTime() {
    return remainingBurstTime;
  }

  public void setRemainingBurstTime(int remainingBurstTime) {
    this.remainingBurstTime = remainingBurstTime;
  }

  public int getWaitingTime() {
    return waitingTime;
  }

  public void setWaitingTime(int waitingTime) {
    this.waitingTime = waitingTime;
  }

  public int getTurnaroundTime() {
    return turnaroundTime;
  }

  public void setTurnaroundTime(int turnaroundTime) {
    this.turnaroundTime = turnaroundTime;
  }

  public int getQuantum() {
    return quantum;
  }

  public void setQuantum(int quantum) {
    this.quantum = quantum;
  }

  public int getFCAIFactor() {
    return FCAIFactor;
  }

  public void setFCAIFactor(int FCAIFactor)
  {
    this.FCAIFactor = FCAIFactor;
  }
}
