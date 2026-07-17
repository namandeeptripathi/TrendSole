package com.trendsole.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exchange_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exchange_number", nullable = false, unique = true)
    private String exchangeNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"statusHistory", "user"})
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"password", "orders"})
    private User customer;

    @Column(name = "exchange_reason", nullable = false)
    private String exchangeReason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "requested_size")
    private String requestedSize;

    @Column(name = "requested_color")
    private String requestedColor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exchange_request_images", joinColumns = @JoinColumn(name = "exchange_request_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status", nullable = false)
    private ExchangeStatus exchangeStatus = ExchangeStatus.PENDING;

    @Column(name = "admin_remarks")
    private String adminRemarks;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
