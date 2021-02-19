package restapiset;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class DeliveryModel extends EntityModel<Delivery> {
  public DeliveryModel(Delivery delivery, Link... links) {
    super(delivery, links);
    add(linkTo(DeliveryController.class).withRel("query-events"));
    add(linkTo(DeliveryController.class).slash(delivery.getId()).withRel("update-events"));
    add(linkTo(DeliveryController.class).slash(delivery.getId()).withSelfRel());
  }
}
