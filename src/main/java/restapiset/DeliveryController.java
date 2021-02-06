package restapiset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/delivery", produces = MediaTypes.HAL_JSON_VALUE)
public class DeliveryController {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @PostMapping
    public ResponseEntity createDelivery(@RequestBody Delivery deliver) {
        Delivery newDelivery = deliveryRepository.save(deliver);
        URI createUri = linkTo(DeliveryController.class).slash(newDelivery.getId()).toUri();
        return ResponseEntity.created(createUri).body(deliver);
    }
}
