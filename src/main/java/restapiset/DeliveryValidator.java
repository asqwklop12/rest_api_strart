package restapiset;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class DeliveryValidator {

  public void validate(DeliveryDto deliveryDto, Errors errors) {
    if(deliveryDto.getDeliveryTime().isAfter(deliveryDto.getDeliveryEndTime())) {
      errors.rejectValue("deliveryTime","wrong value");
    }
  }
}
