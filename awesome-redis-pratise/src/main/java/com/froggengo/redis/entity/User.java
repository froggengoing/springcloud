package com.froggengo.redis.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class User implements Serializable {
    static final long serialVersionUID = 42L;
    private String  name;
    private String className;
    private int age;

}
