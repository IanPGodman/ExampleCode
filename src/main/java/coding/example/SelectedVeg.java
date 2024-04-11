package coding.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class SelectedVeg {
    @Setter
    int count;
    String displayName;
    String imageName;

    public void incCount() {
        count++;
    }
}
