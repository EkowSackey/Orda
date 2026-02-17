package com.example.orda.model;

import com.example.orda.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@CompoundIndex(def = "{'email': 1}", unique = true)
public class User {
    @Id
    private String id;
    private String employeeId;
    private String name;
    private String email;
    private Role role;
}
