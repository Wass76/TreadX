package com.TreadX.user.entity;

import com.TreadX.user.Enum.Permission;
import com.TreadX.user.Enum.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@SuperBuilder(builderMethodName = "builder")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseUser {

    private String position;

    @Override
    protected String getSequenceName() {
        return "user_id_seq";
    }

//    @Override
//    public String getUsername() {
//        return email;  // Use email as username for authentication
//    }
}
