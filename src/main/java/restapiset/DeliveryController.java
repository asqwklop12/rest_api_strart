package restapiset;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/delivery", produces = MediaTypes.HAL_JSON_VALUE)
public class DeliveryController {

  private final DeliveryRepository deliveryRepository;
  private final ModelMapper modelMapper;
  private final DeliveryValidation validation;
  public DeliveryController(DeliveryRepository deliveryRepository, ModelMapper modelMapper,
      DeliveryValidation validation) {
    this.deliveryRepository = deliveryRepository;
    this.modelMapper = modelMapper;
    this.validation = validation;
  }

  @PostMapping
  public ResponseEntity<?> createDelivery(@RequestBody @Valid DeliveryDto deliveryDto,
      Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors);
    }
    validation.validate(deliveryDto, errors);

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors);
    }

    Delivery deliver = modelMapper.map(deliveryDto, Delivery.class);
    Delivery newDelivery = deliveryRepository.save(deliver);
    URI createUri = linkTo(DeliveryController.class).slash(newDelivery.getId()).toUri();
    return ResponseEntity.created(createUri).body(deliver);
  }
}
