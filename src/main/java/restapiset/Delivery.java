package restapiset;

import lombok.*;

import java.time.LocalDateTime;

@Builder @Setter @Getter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Delivery {
    private int id;
    private String itemName;
    private String userName;
    private LocalDateTime startDeliveryTime;
    private LocalDateTime updateDeliveryTime;
}
