package tracker.history;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedHashSet<Task> history = new LinkedHashSet<>();
    private static final int HISTORY_LIMIT = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            history.remove(task); // Удаляем дубликаты
            history.add(task);

            // Проверка размера истории
            if (history.size() > HISTORY_LIMIT) {
                Task first = history.iterator().next(); // Получаем первый элемент
                history.remove(first); // Удаляем самый старый элемент
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // Возвращаем список задач
    }
}