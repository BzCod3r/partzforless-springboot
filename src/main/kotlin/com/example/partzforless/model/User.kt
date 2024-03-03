package com.example.partzforless.model

import jakarta.persistence.*
import lombok.Data

@Data
@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "username", length = 50)
    var username: String? = null,

    @Column(length = 500, nullable = false)
    var password: String? = null,

    @Column(nullable = false)
    var enabled: Boolean? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var authorities: Set<Authority> = HashSet(),

    @OneToMany(mappedBy = "user")
    val urls: MutableList<Url> = mutableListOf(),

    var processRunning: Boolean = false
)