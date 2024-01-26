package com.example.partzforless.service

import com.example.partzforless.model.Url
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScraperService {
    fun processURL(url: Url): Url? {
        val returnUrl = url.copy()
        try {
            val doc: Document = Jsoup.connect(returnUrl.url!!).get()
            //println(doc)
            val currentStatus = doc.getElementsByClass("ux-message__title").text().contains("out of stock", true)
            if (currentStatus) {
                returnUrl.status = 1
            } else {
                returnUrl.status = 0
            }
            returnUrl.updateDate = LocalDateTime.now()
            return returnUrl
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }
}