package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;    //дата и время начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end;      //дата и время конца бронирования

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;              //вещь, которую пользователь бронирует

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;            //пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;   //статус бронирования
}
