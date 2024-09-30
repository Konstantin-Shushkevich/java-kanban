package history;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node tail;
    private Node head;

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(tail, task, null);
        tail = newNode;

        history.put(task.getId(), newNode);

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev == null && next == null) { // Удаление голово-хвоста
            head = null;
            tail = null;
        } else if (prev != null && next == null) { // Удаление хвоста
            prev.setNext(null);
            tail = prev;
        } else if (prev == null) { // Удаление головы, если элементов >2
            next.setPrev(null);
            head = next;
        } else { // Удаление не крайних элементов
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        if (head == null) {
            return tasks;
        }
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    private static class Node {
        private Task data;
        private Node next;
        private Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public Task getData() {
            return data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }
}
