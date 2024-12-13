package ru.previzorz.tsm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.previzorz.tsm.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
