package com.fhdufhdu.windows7board.domain.board.controller

import com.fhdufhdu.windows7board.domain.board.controller.dto.CommentAddtionRequest
import com.fhdufhdu.windows7board.domain.board.controller.dto.PostAdditionRequest
import com.fhdufhdu.windows7board.domain.board.controller.dto.PostCommentDetailsRequest
import com.fhdufhdu.windows7board.domain.board.controller.dto.PostSummariesRequest
import com.fhdufhdu.windows7board.domain.board.service.BoardService
import com.fhdufhdu.windows7board.domain.board.service.dto.command.PostSummariesCommand
import com.fhdufhdu.windows7board.domain.board.service.dto.result.CommentDetails
import com.fhdufhdu.windows7board.domain.board.service.dto.result.PostDetail
import com.fhdufhdu.windows7board.domain.board.service.dto.result.PostSummaries
import com.fhdufhdu.windows7board.domain.board.service.dto.result.SavedPost
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("board")
@Validated
@ResponseBody
class BoardController(
    private val boardService: BoardService
) {
    @PreAuthorize("permitAll")
    @GetMapping("posts")
    fun getPostSummaries(@Valid @ModelAttribute query: PostSummariesRequest): PostSummaries {
        val page = PostSummariesCommand.Page(query.pageNumber, query.pageSize)

        // enum 값 변환
        val sortCriteria = when (query.sortCriteria) {
            PostSummariesRequest.SortCriteria.TITLE -> PostSummariesCommand.SortCriteria.TITLE
            PostSummariesRequest.SortCriteria.CONTENT -> PostSummariesCommand.SortCriteria.CONTENT
            PostSummariesRequest.SortCriteria.CREATED_AT -> PostSummariesCommand.SortCriteria.CREATED_AT
            PostSummariesRequest.SortCriteria.USER_ID -> PostSummariesCommand.SortCriteria.USER_ID
        }
        val sort = PostSummariesCommand.Sort(sortCriteria, query.sortDirection)

        var search: PostSummariesCommand.Search? = null
        if (query.searchQuery != null){
            // enum 값 변환
            val searchCriteria = when (query.searchCriteria) {
                PostSummariesRequest.SearchCriteria.TITLE -> PostSummariesCommand.SearchCriteria.TITLE
                PostSummariesRequest.SearchCriteria.CONTENT -> PostSummariesCommand.SearchCriteria.CONTENT
                PostSummariesRequest.SearchCriteria.USER_ID -> PostSummariesCommand.SearchCriteria.USER_ID
            }
            search = PostSummariesCommand.Search(query.searchQuery!!, searchCriteria)
        }

        val findPostsInput = PostSummariesCommand(page, sort, search)

        return boardService.filterPostSummaries(findPostsInput)
    }

    @PreAuthorize("permitAll")
    @GetMapping("post/{postId}")
    fun getPostDetail(@PathVariable postId: Long): PostDetail {
        return boardService.fetchPostDetail(postId)
    }

    @PostMapping("post")
    @ResponseStatus(HttpStatus.CREATED)
    fun postPost(@AuthenticationPrincipal userId: String, @Valid @RequestBody body: PostAdditionRequest): SavedPost {
        return boardService.addPost(body.title, body.content, userId)
    }

    @DeleteMapping("post/{postId}")
    fun deletePost(@PathVariable postId: Long, @AuthenticationPrincipal userId: String) {
        boardService.deletePost(postId, userId)
    }

    @PreAuthorize("permitAll")
    @GetMapping("post/{postId}/comments")
    fun getPostCommentDetails(
        @Valid @ModelAttribute query: PostCommentDetailsRequest,
        @PathVariable postId: Long
    ): CommentDetails {
        return boardService.fetchCommentDetails(postId, query.pageNumber, query.pageSize)
    }

    @PostMapping("post/{postId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    fun postComment(
        @Valid @RequestBody body: CommentAddtionRequest,
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: String
    ) {
        boardService.addComment(body.content, postId, userId)
    }

    @DeleteMapping("post/{postId}/comment/{commentId}")
    fun deleteComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal userId: String
    ) {
        boardService.removeComment(postId, commentId, userId)
    }

}