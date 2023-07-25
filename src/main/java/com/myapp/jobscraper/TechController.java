package com.myapp.jobscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class TechController {

    private final TechRepository techRepository;

    public TechController(TechRepository techRepository) {
        this.techRepository = techRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void get() throws IOException {
//        database test
//        TechStack techStack = new TechStack();
//        techStack.setName("Java");
//        techStack.setAppearances(5);
//        techRepository.save(techStack);

        String job1 = "https://nofluffjobs.com/pl/job/senior-fullstack-developer-java-kotlin-networkedassets-wroclaw";
        String html = getHtml(job1);

        getData(html);

    }

    private void getData(String html) {
        String regexSection = "(?<=Obowiązkowe)[\\s\\S]*?(?=posting-nice-to-have)";
        String regexSkill = "class=\"ng-star-inserted\">[^<+]";

        Pattern patternSection = Pattern.compile(regexSection);
        Matcher matcherSection = patternSection.matcher(html);

        while (matcherSection.find()) {
            String sectionHtml = matcherSection.group(0);
            Pattern patternSkill = Pattern.compile(regexSkill);
            Matcher matcherSkill = patternSkill.matcher(sectionHtml);

            while (matcherSection.find()) {
                String skill = matcherSkill.group(1);
                System.out.println(skill);
            }
        }

    }

    private String getHtml(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.html();
    }
}
