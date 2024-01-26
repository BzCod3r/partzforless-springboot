package com.example.partzforless.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MyRestService(
    private val restTemplate: RestTemplate,
) {

    fun makeApiRequest(): String {
        val url = "http://localhost:8080"  // Replace with your actual API endpoint

        // Make a GET request
        return restTemplate.getForObject(url, String::class.java)
            ?: throw RuntimeException("Request failed")
    }
}