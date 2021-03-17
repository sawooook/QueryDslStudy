package study.qurtydsl.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.qurtydsl.entity.Hello;
import study.qurtydsl.entity.QHello;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class HelloControllerTest {

    @Autowired
    EntityManager entityManager;

    @Test
    public void contextLoads() {
        Hello hello = new Hello();
        entityManager.persist(hello);

        JPAQueryFactory q = new JPAQueryFactory(entityManager);
        QHello h = QHello.hello;
        Hello result = q.selectFrom(h).fetchOne();

        assertThat(result).isEqualTo(hello);
    }
}