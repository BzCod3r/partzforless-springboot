package com.example.partzforless.service

import com.example.partzforless.model.Url
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.time.LocalDateTime

@Service
class ExcelService @Autowired constructor(private val userService: UserService) {

    fun processExcel(file: MultipartFile): List<Url> {
        val dataList = mutableListOf<Url>()
        val currenUser = userService.getCurrentUser()
        try {
            val workbook = WorkbookFactory.create(file.inputStream)
            val sheet = workbook.getSheetAt(0) // Assuming the data is in the first sheet

            val rowIterator = sheet.iterator()
            rowIterator.next() // Skip the header row

            while (rowIterator.hasNext()) {
                val row = rowIterator.next()
                val url = Url()
                val localDateTime = LocalDateTime.now()
                // Assuming the first cell contains data for Column1 and the second cell for Column2
                url.user = currenUser
                url.tracker = row.getCell(0).stringCellValue
                url.url = row.getCell(1).stringCellValue
                url.createDate =
                    LocalDateTime.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth, 0, 0, 0)
                url.updateDate = url.createDate
                dataList.add(url)
            }
        } catch (e: IOException) {
            // Handle IOException appropriately (e.g., log or throw a custom exception)
            e.printStackTrace()
        }

        return dataList
    }
}