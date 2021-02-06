package restapiset;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    private int id;
    private String item;
    private String user;
    private LocalDateTime deliveryTime;
    private LocalDateTime deliveryEndTime;
    private DeliveryStatus status;
}
