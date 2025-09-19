package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

// 인터페이스는 여러 개를 구현(implements)이 아닌 상속(extends) 할 수 있다. 하지만 클래스는 불가능! 오직 여러 개의 인터페이스를 구현(implements) 할 수 있다.
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoCustomRepository {

    /**
     * 파라미터에 날씨의 값(weather)이 있을 수도 있고 없을(null) 수도 있다라고 조건을 건 후
     * And로 조회할 수정시간의 범위 값을 추가로 받는다.
     * 범위의 시작을 선언한 시작시간(startAt)과 범위의 끝을 선언한 끝시간(endAt)이 있을 수도 있고 없을(null) 수도 있다.
     * 시작시간 혹은 끝 시간 둘 중 하나의 값만 입력 받을 수도 있기에 between 보다는 연산자를 사용하여 조건을 걸었다.
     */
    @Query("""
            SELECT t
            FROM Todo t
            LEFT JOIN FETCH t.user u
            WHERE (:weather IS NULL OR t.weather = :weather)
            AND (:startAt IS NULL OR t.modifiedAt >= :startAt)
            AND (:endAt IS NULL OR t.modifiedAt <= :endAt)
            ORDER BY t.modifiedAt DESC
            """)
    Page<Todo> findTodos(@Param("weather") String weather,
                         @Param("startAt") LocalDateTime startAt,
                         @Param("endAt") LocalDateTime endAt,
                         Pageable pageable);
}
