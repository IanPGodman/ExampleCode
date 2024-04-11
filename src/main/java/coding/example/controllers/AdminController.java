package coding.example.controllers;

import coding.example.database.dto.user.UserDtoWithRoles;
import coding.example.services.ImageService;
import coding.example.services.UserService;
import coding.example.services.VegetableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AdminController {
    private static final Logger log = LogManager.getLogger();

    UserService userService;
    VegetableService vegService;
    ImageService imgService;

    public AdminController(UserService userService, VegetableService vegService, ImageService imgService) {
        this.userService = userService;
        this.vegService = vegService;
        this.imgService = imgService;
    }

    @GetMapping("/removeveg")
    public String removeveg(Model model) {
        model.addAttribute("currentVeglist", vegService.getAvailableList());
        return "remove-veg";
    }

    @GetMapping({"/remove/{veg}"})
    public String addVegetable(@PathVariable("veg") String veg, Model model) {
        log.info("Vegetable to remove: {}", veg);
        vegService.deleteVeg(veg);
        model.addAttribute("currentVeglist", vegService.getAvailableList());
        return "remove-veg";
    }

    @GetMapping("/addveg")
    public String addveg() {
        return "add-veg";
    }

    @GetMapping({"/admin"})
    public String getAdmin(Model model) {
        List<UserDtoWithRoles> dtos = userService.getAllUserDto();
        model.addAttribute("users", dtos);
        model.addAttribute("page_description", "admin page");
        return "admin";
    }

    @RequestMapping("/403")
    public String accessDenied() {
        return "403";
    }

    @ResponseBody
    @RequestMapping(value = "/updateuser", method = RequestMethod.PUT, produces = "text/html")
    public String updateuser(@RequestBody String requestBody) {
        log.info("requestBody: {}", requestBody);
        return userService.updateUser(requestBody);
    }

    @ResponseBody
    @RequestMapping(value = "/addnewveg", method = RequestMethod.POST)
    public ResponseEntity<String> addNewVeg(  @RequestParam("veg-image") MultipartFile vegImage,
                                              @RequestParam("veg-name") String name) {
        log.info("requestBody with name: '{}' and file: {}", name, vegImage);
        VegetableService.AddVegetableResult result = vegService.addNewVeg(name, vegImage);
        return new ResponseEntity<>(result.message(), result.hasError() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }
}