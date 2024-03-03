package com.example.partzforless.model

import jakarta.persistence.*
import lombok.Data
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.validator.constraints.URL
import java.time.LocalDateTime

@Data
@Entity
@Table(name = "urls")
data class Url(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "url", nullable = false, unique = true, length = 400)
    var url: String? = null,
    @Column(name = "tracker", nullable = false, unique = true)
    var tracker: String? = null,
    @Column(name = "create_date", nullable = false)
    var createDate: LocalDateTime? = null,
    @Column(name = "update_date")
    var updateDate: LocalDateTime? = null,
    var status: Int=0,
    @ManyToOne
    var user: User? = null
) {
}