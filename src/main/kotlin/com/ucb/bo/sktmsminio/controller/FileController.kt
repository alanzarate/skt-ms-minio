package com.ucb.bo.sktmsminio.controller

import com.ucb.bo.sktmsminio.bl.FileBl
import com.ucb.bo.sktmsminio.dto.FileDto
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
@RequestMapping("/api/v1/file")
class FileController @Autowired constructor(
    private val fileBl: FileBl
) {
    @PostMapping()
    fun uploadFile(@ModelAttribute requestBody: FileDto): ResponseEntity<Any>{
        return fileBl.uploadFile(requestBody)
    }
    @GetMapping("/{id}")
    fun getUrlSigned(@PathVariable id: String): ResponseEntity<Any> {
        return fileBl.getSignedUrl(id)
    }

    @GetMapping("/download/{id}")
    fun getDownload(@PathVariable id: String): ResponseEntity<Any>{
        return fileBl.downloadFile(id)
    }
}