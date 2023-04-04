package com.news.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class  Role {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="roles_seq_gen")
    @SequenceGenerator(name="roles_seq_gen", sequenceName="roles_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    String roleName;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserEntity> userEntities = new ArrayList<>();


    public Role(Long id) {
        this.id = id;
    }

    public Role(long id, String role) {
        this.id = id;
        this.roleName = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
