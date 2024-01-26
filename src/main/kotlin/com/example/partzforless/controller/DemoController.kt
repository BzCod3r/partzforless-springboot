package com.example.partzforless.controller

import com.example.partzforless.model.Authority
import com.example.partzforless.model.Url
import com.example.partzforless.model.User
import com.example.partzforless.service.ExcelService
import com.example.partzforless.service.MyRestService
import com.example.partzforless.service.UrlService
import com.example.partzforless.service.UserService
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.LocalDateTime
import java.util.*


@Controller
class DemoController @Autowired constructor(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val urlService: UrlService,
    private val excelService: ExcelService,
    private val myRestService: MyRestService
) {

    var isRunning = false
    var currentCount = 0


    @GetMapping("/")
    fun showHome(@ModelAttribute("url_") url: Url, model: Model): String {
        model.addAttribute("totalurls", urlService.getAllUrlsForCurrentUser()?.urls?.count())
        model.addAttribute("out", urlService.getOutOfStockUrls()!!.count())
        model.addAttribute("recent", urlService.getRecentUrls()!!.count())
        model.addAttribute("module", "home")
        model.addAttribute("running", isRunning)
        model.addAttribute("currentCount", currentCount)
        model.addAttribute("processRunning", userService.getCurrentUser()?.processRunning)
        return "index"
    }


    @GetMapping("/all")
    fun showAllUrls(model: Model): String {
        val urls = urlService.getAllUrlsForCurrentUser()?.urls
        var count = 1L
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
            it.id = count++
        }
        model.addAttribute("module", "all")
        model.addAttribute("urls", urls)
        return "pages/allurls"
    }

    @GetMapping("/outofstock")
    fun showOutOfStock(model: Model): String {
        model.addAttribute("module", "out")
        model.addAttribute("urls", urlService.getOutOfStockUrls())
        return "pages/outofstock"
    }

    @GetMapping("/recent")
    fun showRecent(model: Model): String {
        model.addAttribute("module", "recent")
        model.addAttribute("urls", urlService.getRecentUrls())
        return "pages/recent"
    }

    @PostMapping("/")
    fun addUrl(@ModelAttribute("url_") url: Url): String {
        url.id = 0L
        val localDateTime = LocalDateTime.now()
        url.createDate = LocalDateTime.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth, 0, 0, 0)
        println(url.createDate)
        url.updateDate = LocalDateTime.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth, 0, 0, 0)
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            println(authentication.name)
            val getUser = userService.getUserByName(authentication.name)
            url.user = getUser
            getUser!!.urls.add(url)
            getUser.urls.add(urlService.addUrl(url))
            println(getUser.urls.size)
            userService.addUser(getUser)
        } else {
            println("error")
        }

        return "redirect:/all"
    }

    @GetMapping("/login")
    fun showLogin(): String {
        return "login"
    }

    @GetMapping("/register")
    fun showRegisterPage(model: Model): String {
        model.addAttribute("user", User())
        model.addAttribute("userexists", false)
        return "register"
    }

    @PostMapping("/createUser")
    fun createUser(user: User, model: Model): String {

        if (userService.findUserByName(user.username!!)) {
            model.addAttribute("userexists", true)
            model.addAttribute("user", User())
            return "register"
        } else {
            user.enabled = true
            user.password = passwordEncoder.encode(user.password)
            val authority = Authority(0L, "EMPLOYEE", user)
            userService.addUser(user)
            userService.addAuthority(authority)
            return "login"
        }
    }

    @PostMapping("/refresh")
    fun refreshUrls(): String {
        val currentUser = userService.getCurrentUser()
        if (currentUser?.processRunning == false) {
            currentCount = 0
            currentUser.processRunning = true
            userService.addUser(currentUser)
            isRunning = true
            urlService.processUrl() {
                currentCount = it.count
                if (it.reachEnd) {
                    currentUser.processRunning = false
                    userService.addUser(currentUser)
                    isRunning = false
                    return@processUrl
                }
            }
            return "redirect:/"
        } else {
            currentUser!!.processRunning = false
            userService.addUser(currentUser)
            return "redirect:/"
        }
    }

    @PostMapping("/uploadfile")
    fun uploadFile(@RequestParam("file") file: MultipartFile, model: Model): String {
        val dataList: List<Url> = excelService.processExcel(file)
        dataList.forEach {
            try {
                urlService.addUrl(it)
            } catch (e: Exception) {
                println(e.message)
            }
            println(it.user?.username)
        }
        model.addAttribute("urls", dataList)
        return "redirect:/all"
    }

    @GetMapping("/changeip")
    fun changeIp(): String {

        return myRestService.makeApiRequest()
    }
}