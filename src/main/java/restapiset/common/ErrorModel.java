package restapiset.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;
import restapiset.IndexController;

public class ErrorModel extends EntityModel<Errors> {

  public static EntityModel<Errors> modelOf(Errors errors) {
    EntityModel<Errors> errorsModel = EntityModel.of(errors);
    errorsModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    return errorsModel;
  }
}
