package com.example.partzforless.service

import com.example.partzforless.model.RefreshReturnClass
import com.example.partzforless.model.Url
import com.example.partzforless.model.User
import com.example.partzforless.repository.UrlRepository
import com.example.partzforless.repository.UserRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Calendar
import java.util.TimeZone

@Service
class UrlService @Autowired constructor(
    private val urlRepository: UrlRepository,
    private val userService: UserService,
    private val scraperService: ScraperService
) {

    fun getAllUrlsForCurrentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            return userService.getUserByName(authentication.name)
        }
        return null
    }


    fun getUrls(): List<Url> {
        return urlRepository.findAll()
    }

    @Transactional
    fun processUrl(refresh: (result: RefreshReturnClass) -> Unit) {
        var count = 0
        val urlList = getAllUrlsForCurrentUser()?.urls
        val chunkedList = urlList?.chunked(5)
        CoroutineScope(Dispatchers.IO).launch {
            test(chunkedList) {
                refresh(RefreshReturnClass(++count == urlList?.count(), count))
            }
        }

    }

    @Transactional
    suspend fun test(chunkedList: List<List<Url>>?, refresh: () -> Unit) {
        var error = false
        chunkedList?.forEach {
            if (error) {
                return
            }
            val currentUser = it[0].user?.username?.let { it1 -> userService.getUserByName(it1) }
            println("mansoor " + currentUser?.processRunning)
            val scope = CoroutineScope(Dispatchers.IO)
            if (currentUser?.processRunning == false) {
                return
            } else {
                val tasks = (it.indices).map { index ->
                    scope.async {
                        try {
                            scraperService.processURL(it[index])?.let { it1 ->
                                if (it[index].status != it1.status) addUrl(it1)
                                println("check")
                                refresh()
                            }
                        } catch (e: Exception) {
                            error = true
                            println(e.message)
                            return@async
                        }

                    }
                }
                tasks.awaitAll()
            }
        }
    }

    @Transactional
    fun addUrl(url: Url): Url {
        return urlRepository.save(url)
    }

    fun getOutOfStockUrls(): List<Url>? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val currentUser = userService.getUserByName(authentication.name)
            val urls = currentUser?.let { urlRepository.findUrlsByUserEqualsAndStatusEquals(it, 1) }
            return formatUrls(urls)
        }
        return null
    }

    fun getRecentUrls(): List<Url>? {
        val localDateTime = LocalDateTime.now()
        val currentTime = LocalDateTime.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth, 1, 1, 0)
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val currentUser = userService.getUserByName(authentication.name)
            val urls = currentUser?.let {
                urlRepository.findUrlsByUserEqualsAndUpdateDateGreaterThan(it, currentTime)
            }

            return formatUrls(urls)
        }
        return null
    }

    fun formatUrls(urls: List<Url>?): List<Url>? {
        urls?.forEachIndexed { index, it ->
            var tempUrl = ""
            it.url?.forEach {
                if (it == '?') {
                    urls[index].url = tempUrl
                    return@forEach
                } else {
                    tempUrl += it
                }
            }
        }
        return urls
    }
}