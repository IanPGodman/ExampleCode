package coding.example.controllers;

import coding.example.DbSetup;
import coding.example.JpaTestConfig;
import coding.example.SelectedVeg;
import coding.example.database.JpaDatabase;
import coding.example.database.entity.role.RoleRepository;
import coding.example.database.entity.user.UserRepository;
import coding.example.database.entity.vegetable.VegetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {JpaTestConfig.class})
@ActiveProfiles(profiles = "jpa")
public class PageControllerJpaTest extends PageContollerTestAbstractBase {

    @Autowired
    public PageControllerJpaTest(PageController pageController, JpaDatabase jpaDatabaseBean, UserRepository userRepo, RoleRepository roleRepo, VegetableRepository vegRepo, DbSetup dbSetup) {
        jpaDatabaseBean.jpaDatabaseSetup(userRepo, roleRepo, vegRepo);
        this.pageController = pageController;
        this.dbSetup = dbSetup;
    }

    DbSetup dbSetup;

    @BeforeEach
    public void initDB(){
        assertTrue(dbSetup.setUp());
    }

    @Test
    @Transactional
    public void healthEndpointTest(){
        assertEquals("{\"recordCount\": 99}", pageController.health());
    }

    @Test
    @Transactional
    public void loginMethodTest(){
        MockModel model = new MockModel();
        String page = pageController.login(model);
        assertEquals(2, model.getNumberOfAttributes());
        assertEquals("login page", model.getAttribute("page_description"));
        assertEquals("Coding Example UNDER_TEST", model.getAttribute("app_name"));
        assertNotNull(page);
        assertEquals(LOG_IN_TEMPLATE_NAME, page);
    }

    @Test
    @Transactional
    public void indexMethodTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        String page = pageController.index(request, model);
        assertEquals(5, model.getNumberOfAttributes());
        assertEquals("index page", model.getAttribute("page_description"));
        assertEquals("Coding Example UNDER_TEST", model.getAttribute("app_name"));
        assertNotNull(page);
        assertEquals(INDEX_TEMPLATE_NAME, page);
    }

    @Test
    @Transactional
    public void loginErrorMethodTest(){
        MockModel model = new MockModel();
        String page = pageController.loginError(model);
        assertEquals(1, model.getNumberOfAttributes());
        assertEquals("Login failure", model.getAttribute("errorMessage"));
        assertNotNull(page);
        assertEquals(LOGIN_ERROR_TEMPLATE_NAME, page);
    }

    @Test
    @Transactional
    public void addVegMethodTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        String page = addAndTestVeg(model, tomatoVegDets.initExpected());
        addVeg(model, redPepperVegDets);

        List<SelectedVeg> selectedList = getSelectedVegList(model);
        testVeg(selectedList, tomatoVegDets, redPepperVegDets);
        assertNotNull(page);
        assertEquals(SELECTED_VEG_TEMPLATE_NAME, page);
    }

    private String addVeg(MockModel model, VegDetails vegDetails){
        String page = pageController.addVegetable(vegDetails.getVegName(), request, model);
        vegDetails.incExpected();
        return page;
    }

    private String addAndTestVeg(MockModel model, VegDetails vegDetails){
        String page = addVeg(model, vegDetails);
        var selectedList = getSelectedVegList(model);
        testVeg(selectedList, vegDetails);
        return page;
    }

    private void testVeg(List<SelectedVeg> selectedList, VegDetails... dets){
        for (VegDetails veg : dets) {
            Optional<SelectedVeg> r = selectedList.stream().filter(s -> Objects.equals(veg.displayName, s.getDisplayName())).findFirst();
            if (r.isPresent()){
                assertVegAttributes(r.get(), veg);
            }
            else{
                fail("Veg not found in selected list: " + veg.getDisplayName());
            }
        }
    }

    private List<SelectedVeg> getSelectedVegList(MockModel model){
        assertEquals(1, model.getNumberOfAttributes());
        Object selectedVeg = model.getAttribute("selectedlist");
        assertInstanceOf(List.class, selectedVeg);
        @SuppressWarnings("unchecked")
        List<SelectedVeg> list = (List<SelectedVeg>) selectedVeg;
        return list;
    }

    private void assertVegAttributes(SelectedVeg veg, VegDetails vegDetails){
        assertEquals(vegDetails.getDisplayName(), veg.getDisplayName());
        assertEquals(vegDetails.getExpected(), veg.getCount());
        assertEquals(vegDetails.getImageName(), veg.getImageName());
    }

    @Test
    @Transactional
    public void clearMethodTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        String page = addAndTestVeg(model, parsnipDets.initExpected());
        assertNotNull(page);
        assertEquals(SELECTED_VEG_TEMPLATE_NAME, page);
        pageController.clearSelected(request, model);
        var selectedList = getSelectedVegList(model);
        assertTrue(selectedList.isEmpty());
    }

    @Test
    @Transactional
    public void recipeMethodTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        addAndTestVeg(model, parsnipDets.initExpected());
        String page = pageController.getRecipe(request, model);
        assertNotNull(page);
        assertEquals("{\"success\": \"suggestions\"}", page);
    }

    @Test
@Transactional
    public void recipeMethodNoIngreadientsTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        String page = pageController.getRecipe(request, model);
        assertNotNull(page);
        assertEquals("{\"error\": \"No ingredients selected!\"}", page);
    }

    @Test
    @Transactional
    public void suggestionsMethodTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        addAndTestVeg(model, carrotVegDets.initExpected());
        String page = pageController.suggestions(request, model);
        assertNotNull(page);
        assertEquals(SUGGESTIONS_TEMPLATE_NAME, page);
        Object recipeList = model.getAttribute("recipeList");
        assertNotNull(recipeList);
        // Dont care whats in the list just that we have something!
        assertFalse(((List<?>)recipeList).isEmpty());
    }

    @Test
    @Transactional
    public void suggestionsMethodNoIngreadientsTest(){
        when(request.getSession()).thenReturn(session);
        setupSecurity(false);

        MockModel model = new MockModel();
        Exception exception = assertThrows(RuntimeException.class, () -> pageController.suggestions(request, model));

        assertEquals("List of ingredients not supplied!", exception.getMessage());
    }
}
