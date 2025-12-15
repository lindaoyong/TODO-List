package com.todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Team name cannot be blank")
    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    // 团队创建者（主管理员）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // 多对多：团队成员（包含创建者），关联中间表`team_members`
    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    // 团队内的所有TODO（一对多）
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Todo> todos = new HashSet<>();

    // 团队成员角色（可选：细化权限，比如ADMIN/ MEMBER）
    @ElementCollection
    @CollectionTable(
            name = "team_member_roles",
            joinColumns = @JoinColumn(name = "team_id")
    )
    @MapKeyJoinColumn(name = "user_id") // key=用户ID，value=角色
    @Column(name = "role")
    private java.util.Map<User, TeamRole> memberRoles = new java.util.HashMap<>();

    // 团队角色枚举
    public enum TeamRole {
        ADMIN, // 管理员：可编辑/删除团队、邀请成员、管理所有TODO
        MEMBER // 普通成员：仅可查看/编辑自己创建的团队TODO，或按权限查看他人TODO
    }

    // 自动将创建者加入成员，并设为ADMIN
    @PrePersist
    protected void onCreate() {
        if (creator != null) {
            this.members.add(creator);
            this.memberRoles.put(creator, TeamRole.ADMIN);
        }
    }
}