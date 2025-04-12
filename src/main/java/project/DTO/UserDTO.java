package project.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class UserDTO {
    private String homeAddress;
    private String workAddress;
    private Date workStartTime;
    public UserDTO(){}
    public UserDTO(String homeAddress, String workAddress, Date workStartTime) {
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
        this.workStartTime = workStartTime;
    }

}
