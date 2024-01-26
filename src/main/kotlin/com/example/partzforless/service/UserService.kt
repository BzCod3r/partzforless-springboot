package com.example.partzforless.service

import com.example.partzforless.model.Authority
import com.example.partzforless.model.User
import com.example.partzforless.repository.AuthorityRepository
import com.example.partzforless.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService @Autowired
constructor(
    val userRepository: UserRepository,
    val authorityRepository: AuthorityRepository
) {


    @Transactional
    fun getUserByName(username: String): User? {
        try {
            return userRepository.findByUsername(username)
        } catch (e: Exception) {
            println(e)
            return null
        }
    }

    fun findUserByName(username: String): Boolean {
        try {
            return userRepository.findByUsername(username).username != null
        } catch (e: Exception) {
            return false
        }
    }

    @Transactional
    fun showUser() {
        println(userRepository.findAll())
    }

    @Transactional
    fun addAuthority(authority: Authority): Authority {
        return authorityRepository.save(authority)
    }

    @Transactional
    fun addUser(user: User): User {
        return userRepository.save(user)
    }

    fun getCurrentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val currentUser=userRepository.findByUsername(authentication.name)
            return currentUser
        }
        return null
    }
}