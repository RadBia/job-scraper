package com.myapp.jobscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resources;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        Resource resource = new ClassPathResource("url.txt");
        String data = "";
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            data = FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] urls = data.split("\n");

//        String job1 = "https://nofluffjobs.com/pl/job/senior-fullstack-developer-java-kotlin-networkedassets-wroclaw";
//        String job2 = "https://nofluffjobs.com/pl/job/fullstack-developer-react-java-emagine-warszawa-1";
//        urls.add(job1);
//        urls.add(job2);

        List<TechStack> techStacks = new ArrayList<>();


        for (String url : urls) {
            String html = getHtml(url);
            List<String> skills = getData(html);

            skills.forEach(skill -> {
                TechStack techStackToAdd = techStacks.stream()
                        .filter(techStack -> techStack.getName()
                                .equals(skill))
                        .findFirst()
                        .orElseGet(() -> {
                            TechStack newtechStack = new TechStack();
                            newtechStack.setName(skill);
                            return newtechStack;
                        });
                techStackToAdd.setAppearances(techStackToAdd.getAppearances() + 1);
                techStacks.add(techStackToAdd);
            });

            techRepository.saveAll(techStacks);

        }
        System.out.println("THE END");
    }

    private List<String> getData(String html) {

        List<String> skills = new ArrayList<>();

        String regexSection = "(?<=Obowiązkowe)[\\s\\S]*?(?=posting-nice-to-have)";
        String regexSkill = "class=\"ng-star-inserted\">([^<]+)";

        Pattern patternSection = Pattern.compile(regexSection);
        Matcher matcherSection = patternSection.matcher(html);

        while (matcherSection.find()) {
            String sectionHtml = matcherSection.group(0);
            Pattern patternSkill = Pattern.compile(regexSkill);
            Matcher matcherSkill = patternSkill.matcher(sectionHtml);

            while (matcherSkill.find()) {
                String skill = matcherSkill.group(1);
                skills.add(skill.trim());
            }
        }
        return skills;
    }

    private String getHtml(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .get();
        return document.html();
    }
}
