package com.example.partzforless.model

import jakarta.persistence.*
import lombok.Data

@Data
@Entity
@Table(name = "authorities")
data class Authority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 50, nullable = false)
    var authority: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    var user: User
)
