package com.example.partzforless

import com.example.partzforless.model.Authority
import com.example.partzforless.model.User
import com.example.partzforless.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class PartzforlessApplication @Autowired constructor(
    private val userService: UserService, private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        /*val user = User("test", passwordEncoder.encode("test"), true)
        println( userService.addUser(user))
        val authority=Authority(1L,"ROLE_USER",user)
        println(userService.addAuthority(authority))
        userService.showUser()*/
    }
}

fun main(args: Array<String>) {
    runApplication<PartzforlessApplication>(*args)
}
