package study.qurtydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QurtydslApplication {

	public static void main(String[] args) {
		SpringApplication.run(QurtydslApplication.class, args);
	}
}
