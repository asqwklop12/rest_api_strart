package restapiset;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @PostMapping
    public ResponseEntity createDelivery(Delivery delivery) {
        URI uri = URI.create("/api/delivery");
        return ResponseEntity.created(uri).build();
    }
}
