package com.myapp.jobscraper;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TechController {

    private final TechRepository techRepository;

    public TechController(TechRepository techRepository) {
        this.techRepository = techRepository;
    }

    public void get() {

        TechStack techStack = new TechStack();
        techStack.setName("Java");
        techStack.setAppearances(2);
        techRepository.save(techStack);
    }
}
