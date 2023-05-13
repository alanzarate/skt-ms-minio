package com.ucb.bo.sktmsminio.service

import com.ucb.bo.sktmsminio.dto.FileDto
import io.minio.*
import io.minio.http.Method
import io.minio.messages.Item
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

@Slf4j
@Service
class MinioService @Autowired constructor(
    private val minioClient: MinioClient
) {
    @Value("\${minio.bucket}")
    lateinit var bucketName: String

    fun getListObjects(): List<FileDto>? {
        val objects: MutableList<FileDto> = ArrayList()
        try {
            val result: MutableIterable<Result<Item>>? = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build()
            )
            if (result != null) {
                for (item in result) {

                    objects.add(
                        FileDto(filename = item.get().objectName(),
                            size = item.get().size(),
                            //url = getPreSignedUrl(item.get().objectName()))
                            url = "no uri" )
                    )
                }
            }
            return objects
        } catch (e: Exception) {
            println("ERROR ${e.message}")
            //Logger.("Happened error when get list objects from minio: ", e)
        }
        return objects
    }

    fun getPreSignedUrl(filename: String): String?{
        return try {
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .`object`(filename)
                    .expiry(60 * 60 * 24) // one day
                    .build()
            )
        }catch (ex: Exception){

            null
        }
    }

    fun getObject(filename: String?): InputStream? {
        val stream: InputStream = try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(filename)
                    .build()
            )
        } catch (e: Exception) {
            println("Happened error when get list objects from minio: ${e.message}")

            return null
        }
        return stream
    }

    fun uploadFile(name: String, file: MultipartFile): Boolean{
        try{
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .contentType(file.contentType)
                .`object`(name)
                .stream(file.inputStream, file.size, -1)
                .build() )
            return true
        }catch (ex: Exception){
            println("Aqui hay un error ${ex.message}")
        }
        return false
    }
}