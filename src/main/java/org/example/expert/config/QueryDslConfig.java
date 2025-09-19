package org.example.expert.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Spring 설정 클래스임을 명시한다. 만약 이 클래스가 없다면 매번 new 해서 직접 생성해야한다. -> 테스트나 유지보수가 번거롭다.
public class QueryDslConfig {
    @PersistenceContext // @PersistenceContext를 통해 Spring이 관리하는 EntityManager를 주입 받아야 트랜잭션 관리나 영속성 컨텍스트가 제대로 작동
    private EntityManager entityManager; // JPAQueryFactory 내부적으로 EntityManager를 사용해서 쿼리를 실행하기 때문에

    @Bean // Bean으로 등록 시 "의존성 주입"을 통해 여러 곳에서 재사용할 수 있다.
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
