package restapiset;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Delivery {
    private int id;
    private String item;
    private String user;
    private LocalDateTime deliveryTime;
    private LocalDateTime deliveryEndTime;
    private DeliveryStatus status;
    private Integer deliveryCost;
}
