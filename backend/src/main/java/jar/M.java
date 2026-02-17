package jar;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jar.dto.D;

@RestController
@RequestMapping("/")
@Component
public class M {
    @GetMapping()
    public D getD(){
        return new D();
    }
    @Scheduled(fixedRate=5000)
    public void scheduledMethod(){
        System.out.println("\n\t Task 1 ...");
    }

}
