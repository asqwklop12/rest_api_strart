package restapiset;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private String item;
    private String user;
}
