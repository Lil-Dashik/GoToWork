package project.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CommuteRequestDTO {
    private UserDTO userDTO;
    private UserDetailsDTO userDetailsDTO;
    public CommuteRequestDTO() {}
    public CommuteRequestDTO(UserDTO userDTO, UserDetailsDTO userDetailsDTO) {
        this.userDTO = userDTO;
        this.userDetailsDTO = userDetailsDTO;
    }


}
