package com.ucb.bo.sktmsminio.bl

import com.ucb.bo.sktmsminio.dto.FileDto
import com.ucb.bo.sktmsminio.dto.ResponseDto
import com.ucb.bo.sktmsminio.service.MinioService
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
@AllArgsConstructor
@NoArgsConstructor
class FileBl @Autowired constructor(
    private val minioService: MinioService
) {
    fun uploadFile(requestBody: FileDto?): ResponseEntity<Any> {
        try{
            var uuid = UUID.randomUUID().toString();
            if (requestBody == null) throw NullPointerException("empty body")
            if (requestBody.title == null) throw NullPointerException("empty title")
            if (requestBody.file == null) throw NullPointerException("empty file")
            if (requestBody.filename != null)  uuid = requestBody.filename

            println(requestBody.file.originalFilename)
            println(requestBody.file.contentType)

            val success = minioService.uploadFile(uuid, requestBody.file)
            if (!success) throw Exception("No se pudo subir")

            val fileDto = FileDto(
                title = requestBody.title,
                filename = requestBody.filename,
                uuidFile = uuid.toString(),
                size = requestBody.file.size)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto(fileDto, null, true)
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
    fun getSignedUrl(filenameUUID: String):ResponseEntity<Any>{
        try {
            val str = minioService.getPreSignedUrl(filenameUUID) ?: throw Exception("No se encontro ninguno")
            println(str)
            val fileDto = FileDto(
                uuidFile = filenameUUID,
                url = str)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto(fileDto, null, true)
                )
        }catch (ex2: Exception){
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex2.message, false)
                )
        }
    }

    fun downloadFile(filenameUUID: String): ResponseEntity<Any>{
        try {
            val fileBits = minioService.getObject(filenameUUID) ?: throw Exception("No se encontro ninguno")

            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(
                     IOUtils.toByteArray(fileBits)
                )
        }catch (ex2: Exception){
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex2.message, false)
                )
        }
    }

}