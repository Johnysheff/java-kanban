package tracker.history;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private final Map<Integer, Node> taskNodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) return;

        Node existingNode = taskNodeMap.get(task.getTaskId());
        if (existingNode != null) {
            removeNode(existingNode);
        }

        linkLast(task);
    }

    // Добавление задачи в конец двусвязного списка
    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        taskNodeMap.put(task.getTaskId(), tail);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasksList.add(current.task);
            current = current.next;
        }
        return tasksList;
    }

    // Удаления задач из списка
    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        taskNodeMap.remove(node.task.getTaskId());
    }

    @Override
    public void remove(int id) {
        Node existingNode = taskNodeMap.get(id);
        removeNode(existingNode);
    }
}