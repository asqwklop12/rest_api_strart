package restapiset;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class DeliveryValidation {

  public void validate(DeliveryDto deliveryDto, Errors errors) {
    if(deliveryDto.getDeliveryEndTime().isAfter(deliveryDto.getDeliveryTime())) {
      errors.rejectValue("DeliveryTime", "wrong time");
    }
  }

}
