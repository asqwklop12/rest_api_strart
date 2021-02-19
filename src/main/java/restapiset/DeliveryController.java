package restapiset;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/delivery", produces = MediaTypes.HAL_JSON_VALUE)
public class DeliveryController {
    private final DeliveryRepository deliveryRepository;
    private final ModelMapper modelMapper;
    private final DeliveryValidator deliveryValidator;

    public DeliveryController(DeliveryRepository deliveryRepository, ModelMapper modelMapper,
        DeliveryValidator deliveryValidator) {
        this.deliveryRepository = deliveryRepository;
        this.modelMapper = modelMapper;
        this.deliveryValidator = deliveryValidator;
    }

    @PostMapping
    public ResponseEntity<?> createDelivery(@RequestBody @Valid DeliveryDto deliveryDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        deliveryValidator.validate(deliveryDto,errors);
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Delivery deliver = modelMapper.map(deliveryDto, Delivery.class);
        Delivery newDelivery = deliveryRepository.save(deliver);
        WebMvcLinkBuilder selfRelationBuilder = linkTo(DeliveryController.class).slash(newDelivery.getId());
        URI createUri = selfRelationBuilder.toUri();
        DeliveryModel model = new DeliveryModel(deliver);

        return ResponseEntity.created(createUri).body(model);
    }
}
