package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 파라미터 값을 파람값으로 받기에 날씨의 값수정일이 있을 수도 있고 없을 수도 있다.
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE (:weather IS NULL OR t.weather = :weather) ORDER BY t.modifiedAt DESC")
    Page<Todo> findTodos(@Param("weather") String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    /**
     * - 할 일 검색 시 `weather` 조건으로도 검색할 수 있어야해요.
     * - `weather` 조건은 있을 수도 있고, 없을 수도 있어요!
     * - 할 일 검색 시 수정일 기준으로 기간 검색이 가능해야해요.
     * - 기간의 시작과 끝 조건은 있을 수도 있고, 없을 수도 있어요!
     * - JPQL을 사용하고, 쿼리 메소드명은 자유롭게 지정하되 너무 길지 않게 해주세요.
     * <p>
     * 💡 필요할 시, 서비스 단에서 if문을 사용해 여러 개의 쿼리(JPQL)를 사용하셔도 좋습니다.
     */

}
