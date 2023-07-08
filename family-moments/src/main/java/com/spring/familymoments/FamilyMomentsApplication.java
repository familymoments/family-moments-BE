package com.spring.familymoments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 처음에는 아무런 DataSource가 없기 때문에 exclude 옵션을 꼭 추가
// DB 연결 후, (exclude = DataSourceAutoConfiguration.class) 삭제
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class FamilyMomentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyMomentsApplication.class, args);
    }

}
