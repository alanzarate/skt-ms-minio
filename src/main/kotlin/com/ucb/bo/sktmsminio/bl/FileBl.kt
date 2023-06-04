package com.ucb.bo.sktmsminio.bl

import com.ucb.bo.sktmsminio.dto.FileDto
import com.ucb.bo.sktmsminio.dto.ResponseDto
import com.ucb.bo.sktmsminio.service.MinioService
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.net.URLConnection
import java.util.*
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Service
@AllArgsConstructor
@NoArgsConstructor
class FileBl @Autowired constructor(
    private val minioService: MinioService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun uploadFileBl(title:String, file:MultipartFile, filename:String?): FileDto? {
        var returned: FileDto?
        var uuid = UUID.randomUUID().toString()
        if ( filename != null)  uuid = filename
        val success = minioService.uploadFile(uuid, file)
        returned = FileDto(
            title =  title,
            filename =  filename,
            uuidFile = uuid ,
            size =  file.size)
        if (!success) returned  = null
        logger.info("archivo subido exitosamente $uuid")
        return returned

    }

    fun getSignedUrl(filenameUUID: String):ResponseEntity<Any>{
        try {
            val str = minioService.getPreSignedUrl(filenameUUID) ?: throw Exception("No se encontro ninguno")
            logger.info("se obtuvo exitosamente el archivo $filenameUUID")
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

    fun downloadFileV2(uuid: String, fileName: String?, response: HttpServletResponse) {
        try{
            if (fileName == null) throw Exception()
            val inputStream: InputStream? = minioService.getObject(uuid)


            // Set the content type and attachment header.
            response.addHeader("Content-disposition", "attachment;filename=$fileName")
            response.contentType = URLConnection.guessContentTypeFromName(uuid)


            // Copy the stream to the response's output stream.
            IOUtils.copy(inputStream, response.outputStream)
            response.flushBuffer()
        }catch (ex: Exception){
            logger.error("BAD request")
            response.sendError(1,"BAD REQUEST")
            response.flushBuffer()
        }

    }

    fun getSignedUrlMultiple(body: HashMap<String, Any>): ResponseEntity<Any> {
        try {
            val listDto = ArrayList<FileDto>()
            val requestBody = body["uuid"] as ArrayList<*>
            for (obj in requestBody){
                    listDto.add(
                        FileDto(
                            uuidFile = obj as String,
                            url = minioService.getPreSignedUrl(obj)
                        )
                    )


            }

            return ResponseEntity
                .ok()
                .body(
                    ResponseDto(listDto, null, true)
                )
        }catch (ex2: Exception){
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex2.message, false)
                )
        }
    }


    fun  hasDuplicates(arr: ArrayList<String>): Boolean {
        return arr.size != arr.distinct().count()
    }
    fun uploadMultipleFileBl(requestBody: ArrayList<FileDto>): ArrayList<FileDto> {

        val arr: ArrayList<FileDto> = ArrayList()
        val checkDuplicated: ArrayList<String> = ArrayList()
        for (fileDto in requestBody){

            if ( fileDto.title == null) throw NullPointerException("empty title")
            if ( fileDto.file == null) throw NullPointerException("empty file")
            checkDuplicated.add(fileDto.title)
        }
        if (hasDuplicates(checkDuplicated)) throw Exception("Invalid titles")

        for (fileDto in requestBody) {
            if (fileDto.title != null && fileDto.file != null) {
                val pointer = uploadFileBl(
                    title = fileDto.title,
                    file = fileDto.file,
                    filename = fileDto.filename
                )
                if (pointer != null){
                    logger.info("Archivo subido correctamente")
                    arr.add(pointer)
                }
            }


        }
        return arr

    }
}