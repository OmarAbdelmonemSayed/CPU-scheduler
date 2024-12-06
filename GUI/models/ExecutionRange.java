package models;


public class ExecutionRange {
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