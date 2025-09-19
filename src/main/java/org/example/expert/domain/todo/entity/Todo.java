package org.example.expert.domain.todo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "todos")
public class Todo extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    private String weather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "todo", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    /**
     * PERSIST 이기에 저장은 전이가 되지만, 삭제는 전이되지 않는다.
     * 삭제도 하고 싶다면 ALL로 변경하면 된다.
     */
    @OneToMany(mappedBy = "todo", cascade = CascadeType.PERSIST)
    private List<Manager> managers = new ArrayList<>();

    public Todo(String title, String contents, String weather, User user) {
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;

        /**
         * 아래와 같이 작성해도 동작하는 이유는 자바 객체 간의 관계가 메모리에서 먼저 설정되고,
         * JPA가 이를 영속성 켄텍스트에서 추적하며
         * Cascade 설정을 통해 연관된 Entity를 함께 저장하기 때문
         *
         * 다만, 양방향 관계를 유지하지 않으면 조회 시 문제가 생길 수 있다.
         * 그러니 연관관계를 동기화하는 코드를 Manager Entity 에 만들어 주는 것도 방법이다.
         * 예) Manager Entity에 동기화 코드 (편의 메서드) todo.getManagers().add(this);
         */
        this.managers.add(new Manager(user, this));
    }
}
