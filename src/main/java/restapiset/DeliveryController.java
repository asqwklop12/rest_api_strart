package restapiset;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import restapiset.common.ErrorModel;

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
  public ResponseEntity<?> createDelivery(@RequestBody @Valid DeliveryDto deliveryDto,
      Errors errors) {
    if (errors.hasErrors()) {
      return errorResource(errors);
    }
    deliveryValidator.validate(deliveryDto, errors);
    if (errors.hasErrors()) {
      return errorResource(errors);
    }
    Delivery deliver = modelMapper.map(deliveryDto, Delivery.class);
    Delivery newDelivery = deliveryRepository.save(deliver);
    WebMvcLinkBuilder selfRelationBuilder = linkTo(DeliveryController.class)
        .slash(newDelivery.getId());
    URI createUri = selfRelationBuilder.toUri();
    EntityModel<Delivery> model = DeliveryModel.modelOf(deliver);
    model.add(Link.of("http://localhost:8080/docs/index.html#resources-events-create").withRel("profile"));
    return ResponseEntity.created(createUri).body(model);
  }

  @GetMapping
  public ResponseEntity<?> deliveryList(Pageable pageable,
      PagedResourcesAssembler<Delivery> assembler) {
    Page<Delivery> page = deliveryRepository.findAll(pageable);

    var pagedModel = assembler.toModel(page, DeliveryModel::modelOf);
    pagedModel
        .add(Link.of("http://localhost:8080/docs/index.html#resources-events-list").withRel("profile"));
    return ResponseEntity.ok(pagedModel);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getEvent(@PathVariable Integer id) {
    Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);
    if (optionalDelivery.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Delivery delivery = optionalDelivery.get();
    EntityModel<Delivery> deliveryModel = DeliveryModel.modelOf(delivery);
    deliveryModel
        .add(Link.of("http://localhost:8080/docs/index.html#get-event").withRel("profile"));
    return ResponseEntity.ok(deliveryModel);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateEvent(@PathVariable Integer id,
      @RequestBody @Valid DeliveryDto deliveryDto,
      Errors errors) {
    Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);
    if (optionalDelivery.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    if (errors.hasErrors()) {
      return errorResource(errors);
    }

    deliveryValidator.validate(deliveryDto, errors);
    if (errors.hasErrors()) {
      return errorResource(errors);
    }
    Delivery map = modelMapper.map(deliveryDto, Delivery.class);
    Delivery newDelivery = deliveryRepository.save(map);
    EntityModel<Delivery> entityModel = DeliveryModel.modelOf(newDelivery);
    entityModel
        .add(Link.of("http://localhost:8080/docs/index.html#update-event").withRel("profile"));
    return ResponseEntity.ok(entityModel);
  }

  private ResponseEntity<?> errorResource(Errors errors) {
    return ResponseEntity.badRequest().body(ErrorModel.modelOf(errors));
  }
}
