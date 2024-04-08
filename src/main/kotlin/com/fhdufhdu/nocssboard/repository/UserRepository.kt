package com.fhdufhdu.nocssboard.repository

import com.fhdufhdu.nocssboard.entity.Post
import com.fhdufhdu.nocssboard.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String> {
}