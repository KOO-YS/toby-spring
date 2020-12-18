package com.training.spring.domain;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class User {
    String id;
    String name;
    String password;
}
