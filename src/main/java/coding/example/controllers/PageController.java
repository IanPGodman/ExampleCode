package coding.example.controllers;

import coding.example.SelectedVeg;
import coding.example.config.ApplicationData;
import coding.example.services.SpoonacularService;
import coding.example.services.VegetableService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Controller
public class PageController {
	private static final Logger log = LogManager.getLogger();

	ApplicationData appData;

	VegetableService vegService;
	SpoonacularService spoonacularService;

	public PageController(VegetableService vegService, SpoonacularService spoonacularService, ApplicationData appData) {
		this.vegService = vegService;
		this.appData = appData;
		this.spoonacularService = spoonacularService;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model ) {
		model.addAttribute("app_name", appData.getAppName());
		model.addAttribute("page_description", "login page");
		return "login";
	}

	@PostMapping("/performlogin")
	public String performLogin(@RequestBody LoginModel loginModel, Model model ) {
		model.addAttribute("app_name", appData.getAppName());
		model.addAttribute("error", true);
		return "index";
	}

	private boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

		return roles.contains("ROLE_ADMIN");
	}

	@RequestMapping({"/", "/index"})
	public String index(HttpServletRequest request, Model model ) {
		List<SelectedVeg> selectedVegList = getSessionSelectedVegList(request);
		model.addAttribute("app_name", appData.getAppName());
		model.addAttribute("veglist", vegService.getAvailableList());
		model.addAttribute("selectedlist", selectedVegList);
		model.addAttribute("adminDisabled", !isAdmin());
		model.addAttribute("page_description", "index page");
		return "index";
	}

	@RequestMapping("/hello/{who}")
	public String hello( @PathVariable("who") String who, Model model ) {
		model.addAttribute("who", who);
		return "hello";
	}

	@ResponseBody
	@GetMapping("/health")
	public String health() {
		int recordCout = 99;
		return "{\"recordCount\": " + recordCout + "}";
	}

	@GetMapping("/login-error")
	public String loginError( Model model ) {
		model.addAttribute("errorMessage", "Login failure");
		return "loginerror";
	}

	@GetMapping({"/add/{veg}"})
	public String addVegetable(@PathVariable("veg") String veg, HttpServletRequest request, Model model  ) {
		List<SelectedVeg> selectedVegList = getSessionSelectedVegList(request);
		List<SelectedVeg> selectedList = vegService.addVegtable(veg, selectedVegList);
		model.addAttribute("selectedlist", selectedList);
		return "selected-veg";
	}


	@RequestMapping({"/clear"})
	public String clearSelected(HttpServletRequest request, Model model) {
		List<SelectedVeg> emptySelectedVegList = new ArrayList<>();
		request.getSession().setAttribute("selected_veg", emptySelectedVegList);
		model.addAttribute("selectedlist", emptySelectedVegList);
		return "selected-veg";
	}

	private static final String SESSION_ERROR = "SESSION ERROR";
	private static final String ERROR_NO_INGREDIENTS = "{\"error\": \"No ingredients selected!\"}";
	private static final String SUCCESS_RESPONSE = "{\"success\": \"suggestions\"}";


	@ResponseBody
	@RequestMapping({"/recipe"})
	public String getRecipe(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		List<SelectedVeg> selectedVeg = (List<SelectedVeg>)session.getAttribute("selected_veg");

		return isValidSelectedVeg(selectedVeg) ? SUCCESS_RESPONSE : ERROR_NO_INGREDIENTS;
	}

	private boolean isValidSelectedVeg(List<SelectedVeg> selectedVeg) {
		return selectedVeg != null && !selectedVeg.isEmpty();
	}

	@RequestMapping({"/suggestions"})
	public String suggestions(HttpServletRequest request, Model model) {
		List<SelectedVeg> selectedVegList = getSessionSelectedVegList(request);
		Future<List<Map<String, String>>> recipesFuture = spoonacularService.getRecipies(
				selectedVegList.stream().map(SelectedVeg::getDisplayName).toList());
        return addRecipesToModel(recipesFuture, model);
    }

	private String addRecipesToModel(Future<List<Map<String, String>>> recipesFuture, Model model) {
		try {
			model.addAttribute("recipeList", recipesFuture.get(5, TimeUnit.SECONDS));
			return "suggestions";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the list of SelectedVeg from the session, if it does not exists creates a new one
	 * adds it to the session and return s that.
	 *
	 * @param request set server request
	 * @return the list of SelectedVeg
	 */
	private List<SelectedVeg> getSessionSelectedVegList(HttpServletRequest request) {
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Optional<List<SelectedVeg>> list = Optional.ofNullable((List)session.getAttribute("selected_veg"));
		if (list.isEmpty()){
			List<SelectedVeg> newList = new ArrayList<>();
			session.setAttribute("selected_veg", newList);
			return newList;
		}
		return list.get();
	}
}