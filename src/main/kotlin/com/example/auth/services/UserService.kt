package com.example.auth.services

import com.example.auth.dtos.LoginDTO
import com.example.auth.dtos.RegisterDTO
import com.example.auth.dtos.UserDTO
import com.example.auth.models.User
import com.example.auth.repositories.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(private val userRepository: UserRepository) {

    fun save(registerDTO: RegisterDTO) : UserDTO{

        val user = User()

        user.name = registerDTO.name
        user.email = registerDTO.email
        user.password = registerDTO.password

        userRepository.save(user)

        return user
            .let {
                UserDTO(it.id, it.name, it.email)
            }
    }

    fun findByEmail(email : String) : User?{
        return userRepository.findByEmail(email)
    }

    fun findById(id : Int): Optional<User>{
        return userRepository.findById(id)
    }
}