package com.example.partzforless.repository

import com.example.partzforless.model.Authority
import org.springframework.data.jpa.repository.JpaRepository

interface AuthorityRepository : JpaRepository<Authority, String> {
}