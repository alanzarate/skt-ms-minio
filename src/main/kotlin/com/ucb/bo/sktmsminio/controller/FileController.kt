package com.ucb.bo.sktmsminio.controller

import com.ucb.bo.sktmsminio.bl.FileBl
import com.ucb.bo.sktmsminio.dto.FileDto
import com.ucb.bo.sktmsminio.dto.ResponseDto
import com.ucb.bo.sktmsminio.service.MinioService
import io.minio.errors.MinioException
import lombok.extern.slf4j.Slf4j
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Path


@RestController
@Slf4j
@RequestMapping("/api/v1/files")
class FileController @Autowired constructor(
    private val fileBl: FileBl,
    private val minioService: MinioService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    @PostMapping()
    fun uploadFile(@ModelAttribute requestBody: FileDto?): ResponseEntity<Any>{
        try{
            if (requestBody == null) throw NullPointerException("empty body")
            if ( requestBody.title == null) throw NullPointerException("empty title")
            if ( requestBody.file == null) throw NullPointerException("empty file")
            logger.info("El usuario quiere subir un archivo")
            val fileDto = fileBl.uploadFileBl(requestBody.title, requestBody.file, requestBody.filename)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto(fileDto, null, true)
                )
        }catch (ex: NullPointerException){
            logger.error("(NullPointerException) El usuario no pudo subir el archivo por que ${ex.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex.message, false)
                )
        }catch (ex2: Exception){
            logger.error("(Exception) El usuario no pudo subir el archivo por que: ${ex2.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex2.message, false)
                )
        }

    }
    @PostMapping("/multiple")
    fun uploadMultipleFile(@ModelAttribute requestBody: ArrayList<FileDto>?): ResponseEntity<Any>{
        try{
            if (requestBody.isNullOrEmpty()) throw NullPointerException("empty body")

            val fileDtoList = fileBl.uploadMultipleFileBl(requestBody)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto(fileDtoList, null, true)
                )
        }catch (ex: NullPointerException){
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex.message, false)
                )
        }catch (ex2: Exception){
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex2.message, false)
                )
        }


    }

    @GetMapping("/{id}")
    fun getUrlSigned(@PathVariable id: String): ResponseEntity<Any> {
        return fileBl.getSignedUrl(id)
    }

    @PostMapping("/all")
    fun getUrlSignedMultiple(@RequestBody requestBody: HashMap<String, Any> ):ResponseEntity<Any>{
        return fileBl.getSignedUrlMultiple(requestBody)
    }

    @GetMapping("/download/{id}")
    fun getDownload(@PathVariable id: String): ResponseEntity<Any>{
        return fileBl.downloadFile(id)
    }

    @GetMapping("/v2/{uuid}")
    @Throws(MinioException::class, IOException::class)
    fun getObject(@PathVariable("uuid") uuid: String,
                  @RequestParam customQuery:Map<String, String>,
                  response: HttpServletResponse) {
        fileBl.downloadFileV2(uuid, customQuery["filename"], response )

    }
}