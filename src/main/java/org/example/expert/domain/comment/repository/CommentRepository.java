package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * JOIN FETCH을 활용하여 Comment와 연관된 User의 정보를 함께 조회한다.
     * 현재 연관관계가 지연 로딩(Lazy)로 설정되어 있어, 조회 시 N+1 문제가 발생할 수 있다.
     * JOIN FETCH를 통해 조회한 User의 정보가 영속성 컨텍스트 내에 존재하기에 접근 시 추가적인 SELECT 쿼리가 발생하지 않는다.
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.todo.id = :todoId")
    List<Comment> findByTodoIdWithUser(@Param("todoId") Long todoId);
}
