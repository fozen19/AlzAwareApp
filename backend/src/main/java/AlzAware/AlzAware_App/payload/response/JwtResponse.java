package AlzAware.AlzAware_App.payload.response;

import lombok.Data;



@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String roles;

    public JwtResponse(String accessToken, Long id, String username, String email, String firstName, String lastName,
            String phoneNumber, String roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }
}