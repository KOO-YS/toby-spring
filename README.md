# toby-spring
토비의 스프링 3.1 🍏

<del>브랜치로 분류해서 실습해보고 안되면, 멀티모듈로 관리해보기
https://cla9.tistory.com/7 </del>


## Project Setting

#### MySQL Setting
    ```
    CREATE database toby DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
    
    mysql> use toby
    Database changed
    mysql> create table users (
        -> id varchar(10) primary key,
        -> name varchar(20) not null,
        -> password varchar(10) not null
        -> );
    Query OK, 0 rows affected (0.04 sec)
    
    mysql> show tables;
    +----------------+
    | Tables_in_toby |
    +----------------+
    | users          |
    +----------------+
    1 row in set (0.01 sec)
    
    mysql>
    ```

