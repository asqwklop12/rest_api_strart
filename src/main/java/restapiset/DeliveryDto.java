package restapiset;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    @NotNull
    private String item;
    @NotNull
    private String user;
    @NotNull
    private LocalDateTime deliveryTime;
    @NotNull
    private LocalDateTime deliveryEndTime;
    @NotNull
    private Integer itemPrice;
}
