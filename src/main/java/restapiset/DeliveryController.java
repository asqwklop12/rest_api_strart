package restapiset;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/delivery", produces = MediaTypes.HAL_JSON_VALUE)
public class DeliveryController {
    private final DeliveryRepository deliveryRepository;
    private final ModelMapper modelMapper;

    public DeliveryController(DeliveryRepository deliveryRepository, ModelMapper modelMapper) {
        this.deliveryRepository = deliveryRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@RequestBody DeliveryDto deliveryDto) {
        Delivery deliver = modelMapper.map(deliveryDto, Delivery.class);
        Delivery newDelivery = deliveryRepository.save(deliver);
        URI createUri = linkTo(DeliveryController.class).slash(newDelivery.getId()).toUri();
        return ResponseEntity.created(createUri).body(deliver);
    }
}
