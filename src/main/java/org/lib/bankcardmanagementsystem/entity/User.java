package org.lib.bankcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * Представляет собой пользователя.
 * Email пользователя уникален!
 */
@Entity
@Getter
@Setter
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * ID пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    /**
     * Имя пользователя.
     */
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    private String lastName;

    /**
     * Почта пользователя.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Пароль пользователя.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Роль пользователя.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Все карты пользователя.
     */
    @OneToMany
    private List<Card> card;
}
