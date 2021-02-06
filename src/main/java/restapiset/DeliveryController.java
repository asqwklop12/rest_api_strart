package restapiset;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/delivery", produces = MediaTypes.HAL_JSON_VALUE)
public class DeliveryController {

    @PostMapping
    public ResponseEntity createDelivery(Delivery deliver) {
        URI createUri = linkTo(DeliveryController.class).slash("{id}").toUri();
        deliver.setId(10);
        return ResponseEntity.created(createUri).body(deliver);
    }
}
