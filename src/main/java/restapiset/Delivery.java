package restapiset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Delivery {
    @Id
    @GeneratedValue
    private int id;
    private String item;
    private String username;
    private LocalDateTime deliveryTime;
    private LocalDateTime deliveryEndTime;
    private DeliveryStatus status = DeliveryStatus.READY;
    private Integer itemPrice;
    private Integer deliveryCost = 5000;
}
