package restapiset;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @Min(0)
    private Integer itemPrice;
}
