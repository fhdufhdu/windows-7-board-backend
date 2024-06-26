package com.fhdufhdu.windows7board.domain.board.service.dto.result

import com.fhdufhdu.windows7board.common.dto.CommonPaginationDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(name = "PostSummaries")
class PostSummaries(
    val posts: List<PostSummary>,
    number: Int,
    totalPages: Int,
    totalElements: Long,
) : CommonPaginationDto.Response(number, totalPages, totalElements) {
    class PostSummary(
        val id: Long,
        val userId: String,
        val title: String,
        val content: String,
        val createdAt: Date,
        val updatedAt: Date?
    )
}