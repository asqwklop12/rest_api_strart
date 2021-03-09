package restapiset;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

public class DeliveryModel extends EntityModel<Delivery> {

  public static EntityModel<Delivery> modelOf(Delivery delivery) {
    EntityModel<Delivery> deliveryModel = EntityModel.of(delivery);
    deliveryModel.add(linkTo(DeliveryController.class).withRel("query-events"));
    deliveryModel.add(linkTo(DeliveryController.class).slash(delivery.getId()).withRel("update-events"));
    deliveryModel.add(linkTo(DeliveryController.class).slash(delivery.getId()).withSelfRel());
    return deliveryModel;
  }
}
