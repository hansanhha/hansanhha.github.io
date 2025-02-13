package hansanhha;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CountController {

    private final CountService countService;

    public CountController(CountService countService) {
        this.countService = countService;
    }

    @GetMapping
    public ResponseEntity<String> getValue() {
        return ResponseEntity.ok("hello counting module. your count number is: " + countService.getValue("1"));
    }

    @PostMapping("/api/hits")
    public ResponseEntity<Long> increaseCount() {
        return ResponseEntity.ok(countService.increment("1"));
    }

    @PostMapping("/api/hits/undo")
    public ResponseEntity<Long> decreaseCount() {
        return ResponseEntity.ok(countService.decrement("1"));
    }

    @DeleteMapping("/api/hits")
    public void delete() {
        countService.delete("1");
    }

}
