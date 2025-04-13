package project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@Entity
@Table(name="address_and_time")
public class AddressAndTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String homeAddress;
    private String workAddress;
    private LocalTime workStartTime;
    @OneToOne(mappedBy = "addressAndTime")
    private User user;
    public AddressAndTime() {}
    public AddressAndTime(String homeAddress, String workAddress, LocalTime workStartTime) {
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
        this.workStartTime = workStartTime;
    }

}
