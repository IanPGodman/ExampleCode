package coding.example.services;

import coding.example.SelectedVeg;
import coding.example.database.DatabaseService;
import coding.example.database.dto.vegetable.VegetableDto;
import coding.example.database.entity.vegetable.Vegetable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public class VegetableService {
    private final static Logger log = LogManager.getLogger();

    DatabaseService database;
    final ImageService imgService;
    private List<Vegetable> availableVeg;

    public VegetableService(DatabaseService database, ImageService imgService){
        this.database = database;
        this.imgService = imgService;
    }

    private void availableVegRefresh(){
        availableVeg = database.getAvalableVeg();
    }

    public List<Vegetable>  getAvalableVeg(){
        availableVegRefresh();
        return availableVeg;
    }

    private Optional<Vegetable> getValidVeg(String vegtable) {
        log.info("getValidVeg - availableVeg = {}", availableVeg);
        availableVegRefresh();
        return availableVeg.stream().filter(v -> v.getName().equalsIgnoreCase(vegtable)).findFirst();
    }

    public List<SelectedVeg> addVegtable(String vegtableName, List<SelectedVeg> selectedVeg){
        availableVegRefresh();
        Optional<Vegetable> validVeg = getValidVeg(vegtableName);
        if (validVeg.isPresent()) {
            Optional<SelectedVeg> searchVeg = selectedVeg.stream().filter(v -> v.getDisplayName().equalsIgnoreCase(vegtableName)).findFirst();
            if (searchVeg.isPresent()) {
                searchVeg.get().incCount();
            } else {
                selectedVeg.add(new SelectedVeg(1, validVeg.get().getName(), validVeg.get().getImage_name()));
            }
            return selectedVeg;
        }
        else{
            throw new RuntimeException(String.format("Requested vegetable '%s' not found.",vegtableName)) ;
        }
    }

    public List<Vegetable> getAvailableList(){
        availableVegRefresh();
        return availableVeg;
    }

    public boolean deleteVeg(String veg) {
        if (availableVeg.stream().anyMatch(vegetable -> vegetable.getName().equals(veg))){
            // remove from db and refresh availableVeg
            if (database.deleteVegetable(veg)) {
                availableVegRefresh();
                return true;
            }
        }
        return false;
    }

    public String checkVegName(String name) {
        if (name.length() > 32){
            return String.format("'%s' exceeds max length of 32 characters!", name);
        }
        if (getValidVeg(name).isPresent()){
            return String.format("'%s' already exists!", name);
        }
        return "";
    }

    private static final boolean hasError = true;
    private static final boolean noError = false;

    public record AddVegetableResult(String message, boolean hasError){}

    public AddVegetableResult addNewVeg(String newVegName, MultipartFile newVegImage) {
        String errorMessage = checkVegName(newVegName);
        if (!errorMessage.isEmpty()) {
            return new AddVegetableResult(errorMessage, hasError);
        }
        // TODO check and see if image with this name exists, should really add this to the image service may be on an update.
        Optional<String> result = imgService.scaleAndSaveImage(newVegImage, newVegName);
        if (result.isPresent()) {
            VegetableDto dto = new VegetableDto(newVegName, result.get());
            if (database.addVegitable(dto)) {
                // refresh availableVeg
                availableVeg = database.getAvalableVeg();
                return new AddVegetableResult(String.format("%s added successfully.", newVegName), noError);
            }
            return new AddVegetableResult(String.format("Failed to update database for '%s'.", newVegName), hasError);
        }
        return new AddVegetableResult(String.format("%s Unable to scale and save file.", newVegName), hasError);
    }
}
