package manager;

import model.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    Node (Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }
}
