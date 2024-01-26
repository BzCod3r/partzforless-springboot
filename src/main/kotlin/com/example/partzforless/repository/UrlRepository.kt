package com.example.partzforless.repository

import com.example.partzforless.model.Url
import com.example.partzforless.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface UrlRepository : JpaRepository<Url, Long> {
    fun findUrlsByUserEqualsAndStatusEquals(user: User,status:Int): List<Url>
    fun findUrlsByUserEqualsAndUpdateDateGreaterThan(user: User, date: LocalDateTime): List<Url>
}