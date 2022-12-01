package com.example.auth.controllers

import com.example.auth.dtos.LoginDTO
import com.example.auth.dtos.RegisterDTO
import com.example.auth.dtos.UserDTO
import com.example.auth.models.User
import com.example.auth.services.UserService
import com.example.auth.utilities.Message
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/v1/")
class AuthController(private val userService: UserService) {

    //URL: http://localhost:8080/api/v1/register
    @PostMapping("register")
    fun register(@RequestBody registerDTO : RegisterDTO): ResponseEntity<UserDTO>{
        return ResponseEntity.ok(userService.save(registerDTO))
    }

    //URL: http://localhost:8080/api/v1/login
    @PostMapping("login")
    fun login(@RequestBody loginDTO : LoginDTO, response: HttpServletResponse): ResponseEntity<Any>{
        val user = userService.findByEmail(loginDTO.email)
            ?: return ResponseEntity.badRequest().body(Message("user not found!"))

        if (!user.comparePassword(loginDTO.password)) {
            return ResponseEntity.badRequest().body(Message("invalid password!"))
        }

        val issuer = user.id.toString()

        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) //1 day
            .signWith(SignatureAlgorithm.HS384, "secret").compact()

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("success"))
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt : String?): ResponseEntity<Any>{
        try {
            if (jwt == null){
                return ResponseEntity.status(401).body(Message("unauthenticated"))
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body

            return ResponseEntity.ok(userService.findById(body.issuer.toInt()))
        }catch (e: Exception){
            return ResponseEntity.status(401).body(Message("unauthenticated"))
        }
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        var cookie = Cookie("jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("success"))
    }
}