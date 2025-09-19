package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

@RequiredArgsConstructor
//@Repository // Spring bean으로 등록하기 위한 Repository 어노테이션
/**
 * class로 만드는 이유 QueryDsl은 JPAQueryFactory를 사용해서 "직접 쿼리 메서드"를 구현해야 하기에 클래스가 필요
 * 인터페이스는 메서드의 "형태"만 정의하고, 실제 로직은 구현체가 담당
 * 그렇기에 QueryDSL은 selectFrom(),where(),fetch() 같은 로직을 직접 작성해야 하므로 구현 클래스가 필수
 *
 * 추가로 클래스 명 + Impl 접미사를 붙여야 하는 이유는 Spring Data JPA가 자동으로 TodoCustomRepository와 연결해주기 때문
 * 만약 접미사를 붙이지 않으면 직접 Bean에 등록 해야한다.
 */
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * join(todo.user).fetchJoin()
     * -> Todo와 연관된 User를 한 번의 쿼리로 함께 조회 (JOIN FETCH) : Lazy를 우회해서 즉시 로딩
     * <p>
     * where(todo.id.eq(todoId))
     * -> 원하는 Todo ID로 필터링 (입력받은 값) '.eq' = '=' 이것과 같은 의미
     * <p>
     * fetchOne()
     * -> 단일 객체를 반환, 없으면 null, 여러 개면 예외 발생
     * -> 참고사항! 현재 todoId로 조회하는 거라 단건아라 괜찮지만, 이번 상황처럼 결과가 1개로 보장되는 경우에만 사용하길 권장!
     * -> 참고사항!! fetchFirst()를 사용할 수도 있다. 차이 점은 다수의 결과가 있더라도 첫 번째 값만 가져오기 때문이다. 여러 개의 동적 조회를 사용할 때는 위의 방법으로 고려해보자!
     * <p>
     * Optional.ofNullable()
     * -> null 일 수도 있으니 안전하게 감싼다.
     */
    @Override // implements 했으니 "구현" 해야한다.
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;

        Todo result = jpaQueryFactory
                .selectFrom(todo)
                .join(todo.user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
