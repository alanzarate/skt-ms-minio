package com.ucb.bo.sktmsminio.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.web.multipart.MultipartFile

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
class FileDto (
    val title: String? = null,
    val description: String? = null,
    val file: MultipartFile? = null,
    val url: String? = null,
    val size: Long? = null,
    val filename: String? = null,
    val uuidFile: String? = null,
){

}