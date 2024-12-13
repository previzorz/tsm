package ru.previzorz.tsm.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.TaskPriority;
import ru.previzorz.tsm.entity.TaskStatus;

public class TaskSpecification {
    public static Specification<Task> hasAuthor(Long authorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Task> hasExecutor(Long executorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("executor").get("id"), executorId);
    }

    public static Specification<Task> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), TaskStatus.valueOf(status));
    }

    public static Specification<Task> hasPriority(String priority) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), TaskPriority.valueOf(priority));
    }
}
