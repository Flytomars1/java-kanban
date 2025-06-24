package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private final HistoryLinkedList historyList = new HistoryLinkedList();



    //добавление задач в историю
    @Override
    public void add(Task task) {
        if (task == null) return;
        int taskId = task.getId();

        if (historyMap.containsKey(taskId)) {
            remove(taskId);
        }

        //будем хранить в истории не задачу, а копию задачи
        Task copy = new Task(task.getTitle(), task.getDescription());
        copy.setId(task.getId());
        copy.setStatus(task.getStatus());

        historyList.linkLast(copy);
        historyMap.put(taskId, historyList.getLastNode());
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = historyMap.remove(id);
        if (nodeToRemove != null) {
            historyList.removeNode(nodeToRemove);
        }
    }

    @Override
    public void deleteAll() {
        historyList.clear();
        historyMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = historyList.getHeadNode();
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }

        return result;
    }

    private class HistoryLinkedList {
        private Node head;
        private Node tail;

        public void clear() {
            head = null;
            tail = null;
        }

        public void linkLast(Task task) {
            Node newNode = new Node(task);
            if (tail == null) {
                head = newNode;
                tail = newNode;
            } else {
                newNode.prev = tail;
                tail.next = newNode;

                tail = newNode;
            }
        }

        public void removeNode(Node node) {
            Node prevNode = node.prev;
            Node nextNode = node.next;

            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                head = nextNode;
            }

            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                tail = prevNode;
            }
        }

        public Node getHeadNode() {
            return head;
        }

        public Node getLastNode() {
            return tail;
        }
    }

}