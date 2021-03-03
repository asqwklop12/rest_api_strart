package restapiset;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping("/delivery")
  public ResponseEntity<?> index() {
    var resource = new RepresentationModel<>();
    resource.add(linkTo(IndexController.class).withRel("delivery"));
    return ResponseEntity.ok().body(resource);
  }

}
