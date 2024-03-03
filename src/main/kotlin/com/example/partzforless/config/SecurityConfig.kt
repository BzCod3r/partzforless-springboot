package com.example.partzforless.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    /*@Bean
    fun users(dataSource: DataSource): UserDetailsManager {
        println("userDetailsManager")
        return JdbcUserDetailsManager(dataSource)
    }*/

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean
    @Throws(Exception::class)
    fun filterChainTest(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize(PathRequest.toStaticResources().atCommonLocations(), permitAll)
                authorize("/register", permitAll)
                authorize("/createUser", permitAll)
                authorize("/h2-console/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            headers {
                this.frameOptions { sameOrigin }
            }
            formLogin {
                loginPage = "/login"
                permitAll = true
            }
            logout {
                logoutUrl = "/logout"
                logoutSuccessUrl = "/login?logout"
                permitAll=true
            }
        }
        http.csrf {
            it.disable()
        }
        http.headers {
            it.frameOptions {
                it.disable()
            }
        }


        return http.build()
    }

    /*@Bean
    fun jdbcUserDetailsManager(dataSource: DataSource): JdbcUserDetailsManager {
        return JdbcUserDetailsManager(dataSource)
    }*/
}